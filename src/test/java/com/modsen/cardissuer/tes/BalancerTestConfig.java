package com.modsen.cardissuer.tes;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("balancer-test")
public class BalancerTestConfig {

//    @Bean(initMethod = "start", destroyMethod = "stop")
//    public WireMockServer mockBooksService() {
//        return new WireMockServer(WireMockConfiguration.options().dynamicPort());
//    }
//
//    @Autowired
//    private WireMockServer balanceService;
}
