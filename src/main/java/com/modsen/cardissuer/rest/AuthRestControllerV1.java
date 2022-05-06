package com.modsen.cardissuer.rest;

import com.modsen.cardissuer.dto.AuthRequestDto;
import com.modsen.cardissuer.dto.AuthResponseDto;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.security.jwt.JwtTokenProvider;
import com.modsen.cardissuer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth/")
public class AuthRestControllerV1 {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Autowired
    public AuthRestControllerV1(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity login(@RequestBody AuthRequestDto authRequestDto) {
        try {
            final String name = authRequestDto.getName();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(name, authRequestDto.getPassword()));
            User user = userService.findByName(name);
            if (user == null) {
                throw new UsernameNotFoundException("User with name " + name + " not found");
            }
            final String token = jwtTokenProvider.createToken(name, user.getAccessSet());
            return ResponseEntity.ok(new AuthResponseDto(name, token));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid name or password");
        }
    }
}
