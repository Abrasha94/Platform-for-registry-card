package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKeycloakUserId(String keycloakUserId);

    Optional<User> findByName(String name);

    @Transactional
    @Modifying
    @Query("update User u set u.status = ?1 where u.id = ?2")
    void updateStatus(Status status, Long userId);

    @Transactional
    @Modifying
    @Query("update User u set u.password = ?1 where u.id = ?2")
    void updatePassword(String password, Long id);

}
