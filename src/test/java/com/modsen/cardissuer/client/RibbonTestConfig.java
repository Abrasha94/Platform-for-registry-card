package com.modsen.cardissuer.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ServerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.netflix.ribbon.StaticServerList;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@TestConfiguration
@ActiveProfiles("ribbon-test")
public class RibbonTestConfig {

    @Autowired
    private WireMockServer mockBalanceService;

    @Autowired
    private WireMockServer secondMockBalanceService;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public WireMockServer mockBalanceService() {
        return new WireMockServer(options().dynamicPort());
    }

    @Bean(name = "secondMockBalanceService", initMethod = "start", destroyMethod = "stop")
    public WireMockServer secondMockBalanceService() {
        return new WireMockServer(options().dynamicPort());
    }

    @Bean
    public ServerList<Server> ribbonServerList() {
        return new StaticServerList<>(
                new Server("localhost", mockBalanceService.port()),
                new Server("localhost", secondMockBalanceService.port()));
    }
}
