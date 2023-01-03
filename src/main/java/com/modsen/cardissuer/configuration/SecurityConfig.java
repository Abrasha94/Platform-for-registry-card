package com.modsen.cardissuer.configuration;

import com.modsen.cardissuer.security.jwt.JwtConfigurer;
import com.modsen.cardissuer.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;


@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
    public static final String ADMIN_ENDPOINT = "/api/v1/admin/**";
    public static final String MODERATOR_ENDPOINT = "/api/v1/moderator/**";
    public static final String ACCOUNTANT_ENDPOINT = "/api/v1/accountant/**";
    public static final String USER_ENDPOINT = "/api/v1/user/**";

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(LOGIN_ENDPOINT).permitAll()
                .antMatchers(ADMIN_ENDPOINT).hasAuthority("admin permission")
                .antMatchers(MODERATOR_ENDPOINT).hasAuthority("moderator permission")
                .antMatchers(ACCOUNTANT_ENDPOINT).hasAuthority("accountant permission")
                .antMatchers(USER_ENDPOINT).hasAuthority("user permission")
                .anyRequest()
                .authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
    }

}
