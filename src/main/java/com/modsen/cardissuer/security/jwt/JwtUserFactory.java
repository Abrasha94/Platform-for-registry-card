package com.modsen.cardissuer.security.jwt;

import com.modsen.cardissuer.model.Access;
import com.modsen.cardissuer.model.Status;
import com.modsen.cardissuer.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class JwtUserFactory {

//    private JwtUserFactory() {
//    }
//
//    public static JwtUser create(User user) {
//        return new JwtUser(
//                user.getId(),
//                user.getName(),
//                user.getPassword(),
//                user.getStatus().equals(Status.ACTIVE),
//                mapToGrantedAuthority(new HashSet<>(user.getAccessSet()))
//        );
//    }
//
//    private static List<GrantedAuthority> mapToGrantedAuthority(Set<Access> accessSet) {
//        return accessSet.stream()
//                .map(access -> new SimpleGrantedAuthority(access.getPermission()))
//                .collect(Collectors.toList());
//    }
}
