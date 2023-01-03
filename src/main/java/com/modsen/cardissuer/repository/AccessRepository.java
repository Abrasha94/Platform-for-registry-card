package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.Access;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessRepository extends JpaRepository<Access, Long> {
    Access findByPermission(String permission);
}