package com.modsen.cardissuer.service;

import com.modsen.cardissuer.dto.request.AccountantRegisterUserDto;
import com.modsen.cardissuer.dto.request.AdminRegisterUserDto;
import com.modsen.cardissuer.dto.request.ChangeUserPermissionDto;
import com.modsen.cardissuer.dto.request.ChangeUserStatusDto;
import com.modsen.cardissuer.dto.response.UserResponse;
import com.modsen.cardissuer.exception.CompanyNotFoundException;
import com.modsen.cardissuer.exception.Messages;
import com.modsen.cardissuer.exception.RoleNotFoundException;
import com.modsen.cardissuer.exception.UserNotFoundException;
import com.modsen.cardissuer.model.Access;
import com.modsen.cardissuer.model.Status;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.repository.AccessRepository;
import com.modsen.cardissuer.repository.CompanyRepository;
import com.modsen.cardissuer.repository.RoleRepository;
import com.modsen.cardissuer.repository.UserRepository;
import com.modsen.cardissuer.rest.AccountantRestControllerV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public static final long ID = 4L;
    public static final long ACCOUNTANT_ID = 3L;

    private Logger logger = LoggerFactory.getLogger(AccountantRestControllerV1.class);

    public static final String HEADER_KEYCLOAKUSERID = "keycloakUserID";
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final AccessRepository accessRepository;
    private final BCryptPasswordEncoder encoder;
    private final Messages messages;

    @Autowired
    public UserService(UserRepository userRepository, CompanyRepository companyRepository,
                       RoleRepository roleRepository, AccessRepository accessRepository,
                       BCryptPasswordEncoder encoder, Messages messages) {

        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.roleRepository = roleRepository;
        this.accessRepository = accessRepository;
        this.encoder = encoder;
        this.messages = messages;
    }

    public User save(AdminRegisterUserDto userDto) {

        final User user = new User();
        user.setName(userDto.getName());
        user.setPassword(encoder.encode(userDto.getPassword()));
        user.setStatus(Status.ACTIVE);
        user.setCompany(companyRepository.findById(userDto.getCompaniesId()).orElseThrow(() -> new CompanyNotFoundException(messages.companyNotFound)));
        user.setRole(roleRepository.findById(userDto.getRoleId()).orElseThrow(() -> new RoleNotFoundException(messages.roleNotFound)));
        user.setAccessSet(userDto.getAccessId().stream()
                .map(id -> accessRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(messages.roleNotFound)))
                .collect(Collectors.toSet()));

        return userRepository.save(user);
    }

    public User saveInCompany(AccountantRegisterUserDto dto, HttpServletRequest request) {

        final User reqUser = userRepository
                .findByKeycloakUserId(request.getHeader(HEADER_KEYCLOAKUSERID))
                .orElseThrow(() -> new UserNotFoundException(messages.userNotFound));

        final User user = new User();
        user.setName(dto.getName());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setStatus(Status.ACTIVE);
        user.setCompany(reqUser.getCompany());
        user.setRole(roleRepository.findById(ID).orElseThrow(() -> new RoleNotFoundException(messages.roleNotFound)));
        user.setAccessSet(Collections.singleton(accessRepository
                .findById(ID)
                .orElseThrow(() -> new RoleNotFoundException(messages.roleNotFound))));

        return userRepository.save(user);
    }

    public User changeStatus(Long id, ChangeUserStatusDto dto) {

        final User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(messages.userNotFound));
        user.setStatus(dto.getStatus());

        return userRepository.save(user);
    }

    public User changePermission(Long id, ChangeUserPermissionDto dto) {

        final User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(messages.userNotFound));

        final Set<Access> accessSet = dto.getAccessId().stream()
                .map(id1 -> accessRepository.findById(id1).orElseThrow(() -> new RoleNotFoundException(messages.roleNotFound)))
                .collect(Collectors.toSet());
        user.setAccessSet(accessSet);

        return userRepository.save(user);
    }

    public User findByName(String name) {

        return userRepository.findByName(name).orElseThrow(() -> new UserNotFoundException(messages.userNotFound));
    }

    public User findById(Long id) {

        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(messages.userNotFound));
    }

    public List<UserResponse> findAllAccountants() {

        final List<User> accountants = userRepository.findByRole(
                roleRepository.findById(ACCOUNTANT_ID).orElseThrow(() -> new RoleNotFoundException(messages.roleNotFound)));

        return accountants.stream().map(UserResponse::fromUser).collect(Collectors.toList());
    }
}
