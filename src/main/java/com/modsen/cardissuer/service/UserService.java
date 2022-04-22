package com.modsen.cardissuer.service;

import com.modsen.cardissuer.dto.UserDto;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public void save(UserDto userDto) {
        final User user = new User();
        user.setName(userDto.getName());
        user.setPassword(encoder.encode(userDto.getPassword()));
        user.setCompany(userDto.getCompany());
        user.setRole(userDto.getRole());
        userRepository.save(user);
    }

    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }
}
