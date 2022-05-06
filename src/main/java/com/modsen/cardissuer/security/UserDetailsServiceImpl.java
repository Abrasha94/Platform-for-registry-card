package com.modsen.cardissuer.security;

import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.security.jwt.JwtUserFactory;
import com.modsen.cardissuer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        final User user = userService.findByName(name);
        if (user == null) {
            throw new UsernameNotFoundException("User doesn't exists");
        }
        return JwtUserFactory.create(user);
    }
}
