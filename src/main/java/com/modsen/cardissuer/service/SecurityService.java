package com.modsen.cardissuer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

//@Service
public class SecurityService {

//    private final AuthenticationManager authenticationManager;
//
//    private final UserDetailsService userDetailsService;
//
//    @Autowired
//    public SecurityService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
//        this.authenticationManager = authenticationManager;
//        this.userDetailsService = userDetailsService;
//    }
//
//    public String findLoggedInUserName() {
//        final Object details = SecurityContextHolder.getContext().getAuthentication().getDetails();
//        if (details instanceof UserDetails) {
//            return ((UserDetails) details).getUsername();
//        }
//        return null;
//    }
//
//    public void autoLogin(String name, String password) {
//        final UserDetails userDetails = userDetailsService.loadUserByUsername(name);
//        final UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
//        authenticationManager.authenticate(authenticationToken);
//        if (authenticationToken.isAuthenticated()) {
//            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//        }
//    }

}
