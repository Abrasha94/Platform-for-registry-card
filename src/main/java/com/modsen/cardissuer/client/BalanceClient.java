package com.modsen.cardissuer.client;

import com.modsen.cardissuer.model.Balance;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Balance-from-card")
@RibbonClient(name = "Balance-from-card")
public interface BalanceClient {

    @GetMapping("/api/v1/balance/{cardNumber}")
    ResponseEntity<Balance> getBalance(@PathVariable("cardNumber") Long cardNumber);
}
