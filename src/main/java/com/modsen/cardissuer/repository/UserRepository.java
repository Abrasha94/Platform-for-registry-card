package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.Role;
import com.modsen.cardissuer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKeycloakUserId(String keycloakUserId);

    Optional<User> findByName(String name);

    List<User> findByRole(Role role);

}
