package com.modsen.cardissuer.rest;

import com.modsen.cardissuer.dto.request.AuthRequestDto;
import com.modsen.cardissuer.dto.request.RefreshTokenRequest;
import com.modsen.cardissuer.dto.response.AuthResponseDto;
import com.modsen.cardissuer.dto.response.RefreshTokenResponse;
import com.modsen.cardissuer.model.Authentication;
import com.modsen.cardissuer.model.RefreshToken;
import com.modsen.cardissuer.security.jwt.JwtTokenProvider;
import com.modsen.cardissuer.service.RefreshTokenService;
import com.modsen.cardissuer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/auth/")
public class AuthRestControllerV1 {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String CLIENT_ID = "client_id";
    public static final String GRANT_TYPE = "grant_type";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String AUTH_URL = "http://127.0.0.1:8080/realms/my-realm/protocol/openid-connect/token";


    @Value("${keycloak.credentials.secret}")
    private String secret;
    @Value("${keycloak.resource}")
    private String clientId;

    private final RestTemplate restTemplate;

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthRestControllerV1(RestTemplateBuilder builder, JwtTokenProvider jwtTokenProvider,
                                UserService userService, RefreshTokenService refreshTokenService) {
        this.restTemplate = builder.build();
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto authRequestDto) {

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final MultiValueMap<String, String> variablesMap = new LinkedMultiValueMap<>();
        variablesMap.set(USERNAME, authRequestDto.getName());
        variablesMap.set(PASSWORD, authRequestDto.getPassword());
        variablesMap.set(CLIENT_ID, clientId);
        variablesMap.set(GRANT_TYPE, PASSWORD);
        variablesMap.set(CLIENT_SECRET, secret);

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(variablesMap, httpHeaders);

        final ResponseEntity<Authentication> response = restTemplate.postForEntity(AUTH_URL, request, Authentication.class);

        return new ResponseEntity<>(new AuthResponseDto(authRequestDto.getName(),
                response.getBody().getAccess_token(),
                response.getBody().getRefresh_token()), response.getStatusCode());
    }

    @PostMapping("refreshtoken")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {


        return null;
    }
//
//    @PostMapping("change-password")
//    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto dto, HttpServletRequest request) {
//        try {
//            userService.changePassword(dto, request);
//            return new ResponseEntity<>("Password successfully changed!", HttpStatus.OK);
//        } catch (WrongPasswordException e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//    }
}
