package com.modsen.apigateway.configuration;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ModifyHeaderRouteConfig extends AbstractGatewayFilterFactory<ModifyHeaderRouteConfig.Config> {

    public ModifyHeaderRouteConfig() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
            exchange.getPrincipal()
                    .map(principal -> (KeycloakPrincipal<KeycloakSecurityContext>) principal)
                    .map(kPrincipal -> kPrincipal
                            .getKeycloakSecurityContext()
                            .getIdToken()
                            .getSubject())
                    .subscribe(config::setKeycloakUserId);
            exchange.getRequest()
                    .getHeaders()
                    .add("keycloakUserID", config.getKeycloakUserId());

            return chain.filter(exchange);
        }, 0);
    }


    public static class Config {
        private String keycloakUserId;

        public String getKeycloakUserId() {
            return keycloakUserId;
        }

        public void setKeycloakUserId(String keycloakUserId) {
            this.keycloakUserId = keycloakUserId;
        }
    }

}
