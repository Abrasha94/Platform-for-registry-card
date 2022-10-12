package com.modsen.cardissuer.util;

import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.repository.UserRepository;
import com.modsen.cardissuer.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class MethodsUtil {

    public final UserRepository userRepository;
    public final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public MethodsUtil(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public User getUserFromRequest(HttpServletRequest request) {
        final String token = jwtTokenProvider.resolveToken(request);
        final String userName = jwtTokenProvider.getName(token);
        return userRepository.findByName(userName).orElseThrow();
    }

}
