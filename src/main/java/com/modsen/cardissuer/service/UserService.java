package com.modsen.cardissuer.service;

import com.modsen.cardissuer.dto.request.AccountantRegisterUserDto;
import com.modsen.cardissuer.dto.request.AdminRegisterUserDto;
import com.modsen.cardissuer.dto.request.ChangePasswordDto;
import com.modsen.cardissuer.dto.request.ChangeUserPermissionDto;
import com.modsen.cardissuer.dto.request.ChangeUserStatusDto;
import com.modsen.cardissuer.dto.response.UserResponseDto;
import com.modsen.cardissuer.exception.CompanyNotFoundException;
import com.modsen.cardissuer.exception.RoleNotFoundException;
import com.modsen.cardissuer.exception.WrongPasswordException;
import com.modsen.cardissuer.exception.UserNotFoundException;
import com.modsen.cardissuer.model.Access;
import com.modsen.cardissuer.model.Status;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.repository.AccessRepository;
import com.modsen.cardissuer.repository.CompanyRepository;
import com.modsen.cardissuer.repository.RoleRepository;
import com.modsen.cardissuer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserService {

    public static final String HEADER_KEYCLOAKUSERID = "keycloakUserID";
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final AccessRepository accessRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository, CompanyRepository companyRepository,
                       RoleRepository roleRepository, AccessRepository accessRepository,
                       BCryptPasswordEncoder encoder) {

        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.roleRepository = roleRepository;
        this.accessRepository = accessRepository;
        this.encoder = encoder;
    }

    public User save(AdminRegisterUserDto userDto) {

        final User user = new User();
        user.setName(userDto.getName());
        user.setPassword(encoder.encode(userDto.getPassword()));
        user.setStatus(Status.ACTIVE);
        user.setCompany(companyRepository.findById(userDto.getCompaniesId()).orElseThrow(() -> new CompanyNotFoundException("Company not found!")));
        user.setRole(roleRepository.findById(userDto.getRoleId()).orElseThrow(() -> new RoleNotFoundException("Role not found!")));
        user.setAccessSet(userDto.getAccessId().stream()
                .map(id -> accessRepository.findById(id).orElseThrow(() -> new RoleNotFoundException("Role not found!")))
                .collect(Collectors.toSet()));

        return userRepository.save(user);
    }

    public User saveInCompany(AccountantRegisterUserDto dto, HttpServletRequest request) {

        final User reqUser = userRepository
                .findByKeycloakUserId(request.getHeader(HEADER_KEYCLOAKUSERID))
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        final User user = new User();
        user.setName(dto.getName());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setStatus(Status.ACTIVE);
        user.setCompany(reqUser.getCompany());
        user.setRole(roleRepository.findById(4L).orElseThrow(() -> new RoleNotFoundException("Role not found!")));
        user.setAccessSet(Collections.singleton(accessRepository
                .findById(4L)
                .orElseThrow(() -> new RoleNotFoundException("Role not found!"))));

        return userRepository.save(user);
    }

    public User changeStatus(Long id, ChangeUserStatusDto dto) {

        final User user = findById(id);
        user.setStatus(dto.getStatus());

        userRepository.updateStatus(dto.getStatus(), id);

        return user;
    }

    public User changePermission(Long id, ChangeUserPermissionDto dto) {

        final User user = findById(id);

        final Set<Access> accessSet = dto.getAccessId().stream()
                .map(id1 -> accessRepository.findById(id1).orElseThrow(() -> new RoleNotFoundException("Role not found!")))
                .collect(Collectors.toSet());
        user.setAccessSet(accessSet);
        userRepository.updateAccess(accessSet, id);

        return user;
    }

    public User findByName(String name) {

        return userRepository.findByName(name).orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    public User findById(Long id) {

        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    public List<UserResponseDto> findAllAccountants() {

        final List<User> accountants = userRepository.findByRole(
                roleRepository.findById(3L).orElseThrow(() -> new RoleNotFoundException("Role not found!")));

        return accountants.stream().map(UserResponseDto::fromUser).collect(Collectors.toList());
    }

    public void changePassword(ChangePasswordDto dto, HttpServletRequest request) {

        final User user = userRepository
                .findByKeycloakUserId(request.getHeader(HEADER_KEYCLOAKUSERID))
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        if (!encoder.matches(dto.getOldPass(), user.getPassword())) {
            throw new WrongPasswordException("Wrong old password!");
        }

        if (encoder.matches(dto.getNewPass(), user.getPassword())) {
            throw new WrongPasswordException("Old and new password don't be the same!");
        }

        userRepository.updatePassword(encoder.encode(dto.getNewPass()), user.getId());
    }

}
