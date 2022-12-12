package com.modsen.cardissuer.service;

import com.modsen.cardissuer.exception.RefreshTokenException;
import com.modsen.cardissuer.model.RefreshToken;
import com.modsen.cardissuer.repository.RefreshTokenRepository;
import com.modsen.cardissuer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.token.refresh}")
    private Long validityMillis;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        final RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.getOne(userId));
        refreshToken.setExpireDate(Timestamp.from(Instant.now().plusMillis(validityMillis)));
        refreshToken.setToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpireDate().compareTo(Timestamp.from(Instant.now())) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException("Refresh token is expired!");
        }
        return token;
    }
}
