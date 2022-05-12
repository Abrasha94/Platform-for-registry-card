package com.modsen.cardissuer.rest;

import com.modsen.cardissuer.dto.request.AuthRequestDto;
import com.modsen.cardissuer.dto.request.RefreshTokenRequest;
import com.modsen.cardissuer.dto.response.AuthResponseDto;
import com.modsen.cardissuer.dto.request.ChangePasswordDto;
import com.modsen.cardissuer.dto.response.RefreshTokenResponse;
import com.modsen.cardissuer.exception.RefreshTokenException;
import com.modsen.cardissuer.exception.WrongPasswordException;
import com.modsen.cardissuer.model.RefreshToken;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.security.jwt.JwtTokenProvider;
import com.modsen.cardissuer.service.RefreshTokenService;
import com.modsen.cardissuer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/auth/")
public class AuthRestControllerV1 {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthRestControllerV1(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider,
                                UserService userService, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("login")
    public ResponseEntity login(@Valid @RequestBody AuthRequestDto authRequestDto) {
        try {
            final String name = authRequestDto.getName();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(name, authRequestDto.getPassword()));
            User user = userService.findByName(name);
            if (user == null) {
                throw new UsernameNotFoundException("User with name " + name + " not found");
            }
            final String token = jwtTokenProvider.createToken(name, user.getAccessSet());
            final RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
            return ResponseEntity.ok(new AuthResponseDto(name, token, refreshToken.getToken()));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid name or password");
        }
    }

    @PostMapping("refreshtoken")
    public ResponseEntity refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        final String refreshToken = request.getRefreshToken();
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    final String token = jwtTokenProvider.createToken(user.getName(), user.getAccessSet());
                    return ResponseEntity.ok(new RefreshTokenResponse(token, refreshToken));
                })
                .orElseThrow(() -> new RefreshTokenException("Refresh token is wrong!"));
    }

    @PostMapping("change-password")
    public ResponseEntity changePassword(@RequestBody ChangePasswordDto dto, HttpServletRequest request) {
        try {
            userService.changePassword(dto, request);
            return ResponseEntity.ok("Password successfully changed!");
        } catch (WrongPasswordException e) {
            return ResponseEntity.badRequest().body(e);
        }
    }
}
