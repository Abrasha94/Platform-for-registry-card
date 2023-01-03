package com.modsen.cardissuer.model;

import lombok.Getter;

@Getter
public class Authentication {

    private String access_token;
    private Integer expires_in;
    private Integer refresh_expires_in;
    private String refresh_token;
    private String token_type;
    private String session_state;
    private String scope;
}
