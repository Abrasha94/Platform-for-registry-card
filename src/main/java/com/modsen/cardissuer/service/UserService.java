package com.modsen.cardissuer.service;

import com.modsen.cardissuer.dto.request.AccountantRegisterUserDto;
import com.modsen.cardissuer.dto.request.AdminRegisterUserDto;
import com.modsen.cardissuer.dto.request.ChangePasswordDto;
import com.modsen.cardissuer.dto.request.ChangeUserStatusDto;
import com.modsen.cardissuer.exception.CompanyNotFoundException;
import com.modsen.cardissuer.exception.WrongPasswordException;
import com.modsen.cardissuer.exception.UserNotFoundException;
import com.modsen.cardissuer.model.Status;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.repository.CompanyRepository;
import com.modsen.cardissuer.repository.UserRepository;
import com.modsen.cardissuer.util.MethodsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final BCryptPasswordEncoder encoder;
    private final MethodsUtil methodsUtil;

    @Autowired
    public UserService(UserRepository userRepository, CompanyRepository companyRepository,
                       BCryptPasswordEncoder encoder, MethodsUtil methodsUtil) {

        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.encoder = encoder;
        this.methodsUtil = methodsUtil;
    }

    public User save(AdminRegisterUserDto userDto) {

        final User user = new User();
        user.setName(userDto.getName());
        user.setPassword(encoder.encode(userDto.getPassword()));
        user.setStatus(Status.ACTIVE);
        user.setCompany(companyRepository.findById(userDto.getCompaniesId()).orElseThrow(() -> new CompanyNotFoundException("Company not found!")));

        return userRepository.save(user);
    }

    public User saveInCompany(AccountantRegisterUserDto dto) {

        final User reqUser = userRepository
                .findByKeycloakUserId(methodsUtil.getKeycloakUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        final User user = new User();
        user.setName(dto.getName());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setStatus(Status.ACTIVE);
        user.setCompany(reqUser.getCompany());

        return userRepository.save(user);
    }

    public User changeStatus(Long id, ChangeUserStatusDto dto) {

        final User user = findById(id);
        user.setStatus(dto.getStatus());

        userRepository.updateStatus(dto.getStatus(), id);

        return user;
    }

    public User findByName(String name) {

        return userRepository.findByName(name).orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    public User findById(Long id) {

        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    public void changePassword(ChangePasswordDto dto, HttpServletRequest request) {

        final User user = userRepository
                .findByKeycloakUserId(methodsUtil.getKeycloakUserId())
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
