package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.RefreshToken;
import com.modsen.cardissuer.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RefreshTokenRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    RefreshToken refreshToken = new RefreshToken();
    User user = new User();

    @BeforeEach
    void setUp() {
        user.setId(1L);
        refreshToken.setToken("test token");
        refreshToken.setExpireDate(Timestamp.from(Instant.now()));
        refreshToken.setUser(user);
    }

    @Test
    void should_create_refresh_token() {
        final RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        assertEquals(refreshToken, savedToken);
    }

    @Test
    void should_find_refresh_token_by_token() {
        entityManager.persist(refreshToken);
        final RefreshToken test_token = refreshTokenRepository.findByToken("test token").get();
        assertEquals(refreshToken, test_token);
    }

    @Test
    void should_update_refresh_token_by_id() {
        final RefreshToken token = entityManager.persist(refreshToken);
        final RefreshToken foundedToken = refreshTokenRepository.findById(token.getId()).get();
        foundedToken.setToken("update token");
        refreshTokenRepository.save(foundedToken);
        final RefreshToken changedToken = refreshTokenRepository.findById(token.getId()).get();
        assertEquals(foundedToken.getId(), changedToken.getId());
        assertEquals("update token", changedToken.getToken());
    }

    @Test
    void should_delete_refresh_token_by_id() {
        final RefreshToken token = entityManager.persist(refreshToken);
        refreshTokenRepository.deleteById(token.getId());
        final Optional<RefreshToken> tokenById = refreshTokenRepository.findById(token.getId());
        assertTrue(tokenById.isEmpty());
    }
}