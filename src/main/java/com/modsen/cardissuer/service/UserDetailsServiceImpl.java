package com.modsen.cardissuer.service;

import com.modsen.cardissuer.model.Access;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        final User user = userRepository.findByName(name).orElseThrow(()
                -> new UsernameNotFoundException("User doesn't exists"));

        final Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        for (Access access : user.getAccessSet()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(access.getPermission()));
        }
        return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(), grantedAuthorities);
    }
}
