package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Transactional
    @Modifying
    @Query("update Company c set c.status = ?1 where c.id = ?2")
    void updateStatus(Status status, Long companyId);
}