package com.modsen.cardissuer.caching;

import com.modsen.cardissuer.client.BalanceClient;
import com.modsen.cardissuer.model.Balance;
import com.modsen.cardissuer.service.CardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ImportAutoConfiguration(classes = {CacheAutoConfiguration.class, RedisAutoConfiguration.class})
@EnableCaching
public class CardServiceCachingIntegrationTest {

    public static final long CARD_NUMBER = 1L;

    @MockBean
    private BalanceClient balanceClient;

    @Autowired
    private CardService cardService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void whenGetBalanceTwice_thenReturnCachedValue() {
        final Balance balance = new Balance(BigDecimal.TEN, CARD_NUMBER);

        given(balanceClient.getBalance(CARD_NUMBER)).willReturn(
                new ResponseEntity<>(balance, HttpStatus.OK));

        final Balance balanceFromCard = cardService.getBalanceFromCard(CARD_NUMBER);
        final Balance balanceFromCardCache = cardService.getBalanceFromCard(CARD_NUMBER);

        assertThat(balanceFromCard.getBalance()).isEqualTo(BigDecimal.TEN);
        assertThat(balanceFromCardCache.getBalance()).isEqualTo(BigDecimal.TEN);

        verify(balanceClient, times(1)).getBalance(CARD_NUMBER);
        assertThat(itemFromCache()).isNotNull();
    }

    private Object itemFromCache() {
        return cacheManager.getCache("balanceCache");
    }

    @TestConfiguration
    static class EmbeddedRedisConfig {

        private final RedisServer redisServer;

        EmbeddedRedisConfig() {
            this.redisServer = new RedisServer();
        }

        @PostConstruct
        public void startRedis() {
            redisServer.start();
        }

        @PreDestroy
        public void stopRedis() {
            this.redisServer.stop();
        }
    }
}

