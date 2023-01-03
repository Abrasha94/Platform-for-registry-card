package com.modsen.cardissuer.service;

import com.modsen.cardissuer.dto.request.AccountantRegisterUserDto;
import com.modsen.cardissuer.dto.request.AdminRegisterUserDto;
import com.modsen.cardissuer.dto.request.ChangePasswordDto;
import com.modsen.cardissuer.dto.request.ChangeUserPermissionDto;
import com.modsen.cardissuer.dto.request.ChangeUserStatusDto;
import com.modsen.cardissuer.dto.response.UserResponseDto;
import com.modsen.cardissuer.exception.WrongPasswordException;
import com.modsen.cardissuer.exception.UserNotFoundException;
import com.modsen.cardissuer.model.Access;
import com.modsen.cardissuer.model.Status;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.repository.AccessRepository;
import com.modsen.cardissuer.repository.CompanyRepository;
import com.modsen.cardissuer.repository.RoleRepository;
import com.modsen.cardissuer.repository.UserRepository;
import com.modsen.cardissuer.util.MethodsUtil;
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

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final AccessRepository accessRepository;
    private final BCryptPasswordEncoder encoder;
    private final MethodsUtil methodsUtil;

    @Autowired
    public UserService(UserRepository userRepository, CompanyRepository companyRepository,
                       RoleRepository roleRepository, AccessRepository accessRepository,
                       BCryptPasswordEncoder encoder, MethodsUtil methodsUtil) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.roleRepository = roleRepository;
        this.accessRepository = accessRepository;
        this.encoder = encoder;
        this.methodsUtil = methodsUtil;
    }

    public User save(AdminRegisterUserDto userDto) {
        final User user = new User();
        user.setName(userDto.getName());
        user.setPassword(encoder.encode(userDto.getPassword()));
        user.setStatus(Status.ACTIVE);
        user.setCompany(companyRepository.getOne(userDto.getCompaniesId()));
        user.setRole(roleRepository.getOne(userDto.getRoleId()));
        user.setAccessSet(userDto.getAccessId().stream().map(accessRepository::getOne).collect(Collectors.toSet()));
        return userRepository.save(user);
    }

    public User saveInCompany(AccountantRegisterUserDto dto, HttpServletRequest request) {
        final User reqUser = methodsUtil.getUserFromRequest(request);
        final User user = new User();
        user.setName(dto.getName());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setStatus(Status.ACTIVE);
        user.setCompany(reqUser.getCompany());
        user.setRole(roleRepository.getOne(4L));
        user.setAccessSet(Collections.singleton(accessRepository.getOne(4L)));
        return userRepository.save(user);
    }

    public User changeStatus(Long id, ChangeUserStatusDto dto) {
        final User user = findById(id);
        if (user == null) {
            throw new UserNotFoundException("User not found!");
        } else {
            user.setStatus(dto.getStatus());
            userRepository.updateStatus(dto.getStatus(), id);
            return user;
        }
    }

    public User changePermission(Long id, ChangeUserPermissionDto dto) {
        final User user = findById(id);
        if (user == null) {
            throw new UserNotFoundException("User not found!");
        } else {
            final Set<Access> accessSet = dto.getAccessId().stream().map(accessRepository::getOne).collect(Collectors.toSet());
            user.setAccessSet(accessSet);
            userRepository.updateAccess(accessSet, id);
            return user;
        }
    }

    public User findByName(String name) {
        return userRepository.findByName(name).orElse(null);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<UserResponseDto> findAllAccountants() {
        final List<User> accountants = userRepository.findByRole(roleRepository.getOne(3L));
        if (accountants == null) {
            throw new UserNotFoundException("Accountants not found!");
        }
        return accountants.stream().map(UserResponseDto::fromUser).collect(Collectors.toList());
    }

    public void changePassword(ChangePasswordDto dto, HttpServletRequest request) {
        final User user = methodsUtil.getUserFromRequest(request);
        if (!encoder.matches(dto.getOldPass(), user.getPassword())) {
            throw new WrongPasswordException("Wrong old password!");
        }
        if (encoder.matches(dto.getNewPass(), user.getPassword())) {
            throw new WrongPasswordException("Old and new password don't be the same!");
        }
        userRepository.updatePassword(encoder.encode(dto.getNewPass()), user.getId());
    }

}
