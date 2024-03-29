package com.modsen.cardissuer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.modsen.cardissuer.client")
@EnableHystrix
@EnableCaching
public class CardIssuerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardIssuerApplication.class, args);
	}

}
