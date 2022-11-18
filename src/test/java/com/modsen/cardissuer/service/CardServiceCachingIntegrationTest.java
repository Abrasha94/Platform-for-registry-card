package com.modsen.cardissuer.service;

import com.modsen.cardissuer.client.BalanceClient;
import com.modsen.cardissuer.model.Balance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Import({CacheConfig.class, CardService.class})
@ExtendWith(SpringExtension.class)
@EnableCaching
@ImportAutoConfiguration(classes = {
        CacheAutoConfiguration.class,
        RedisAutoConfiguration.class
})
public class CardServiceCachingIntegrationTest {

    @MockBean
    private BalanceClient balanceClient;

    @Autowired
    private CardService cardService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void givenRedisCaching_whenGetBalanceFromCard_thenBalanceReturnedFromCache() {
        final Balance balance = new Balance(BigDecimal.TEN, 1L);
        Mockito.when(balanceClient.getBalance(1L)).thenReturn(new ResponseEntity<>(balance, HttpStatus.OK));

        final Balance balanceCacheMiss = cardService.getBalanceFromCard(1L);
        final Balance balanceCacheHit = cardService.getBalanceFromCard(1L);

        assertThat(balanceCacheMiss).isEqualTo(balance);
        assertThat(balanceCacheHit).isEqualTo(balance);

        Mockito.verify(balanceClient, Mockito.times(1)).getBalance(1L);
        assertThat(cacheManager.getCache("balanceCache").get(1L).get()).isEqualTo(balance);
    }
}
