package com.modsen.cardissuer.service;

import com.modsen.cardissuer.dto.*;
import com.modsen.cardissuer.exception.UserNotFoundException;
import com.modsen.cardissuer.model.Access;
import com.modsen.cardissuer.model.Status;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.repository.AccessRepository;
import com.modsen.cardissuer.repository.CompanyRepository;
import com.modsen.cardissuer.repository.RoleRepository;
import com.modsen.cardissuer.repository.UserRepository;
import com.modsen.cardissuer.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.Array;
import java.util.Arrays;
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
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserService(UserRepository userRepository, CompanyRepository companyRepository,
                       RoleRepository roleRepository, AccessRepository accessRepository,
                       BCryptPasswordEncoder encoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.roleRepository = roleRepository;
        this.accessRepository = accessRepository;
        this.encoder = encoder;
        this.jwtTokenProvider = jwtTokenProvider;
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
        final String token = jwtTokenProvider.resolveToken(request);
        final String userName = jwtTokenProvider.getName(token);
        final User reqUser = userRepository.findByName(userName).orElseThrow();
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
}
