package com.modsen.cardissuer.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

public class BalanceMocks {
    public static void setupMockBalanceResponse(WireMockServer mockService) throws IOException {
        mockService.stubFor(WireMock.get(WireMock.urlEqualTo("/api/v1/balance/123"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                StreamUtils.copyToString(
                                        BalanceMocks.class.getClassLoader().getResourceAsStream("payload/get-balance-response.json"),
                                        Charset.defaultCharset()))));
    }
}
