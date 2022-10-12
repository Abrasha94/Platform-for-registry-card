package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CompanyRepositoryTest {

    @Autowired
    CompanyRepository companyRepository;

    @Test
    void should_create_company() {
        Company company = new Company();
        company.setStatus(Status.ACTIVE);
        company.setName("Test company");
        final Company savedCompany = companyRepository.save(company);
        assertEquals(company, savedCompany);
    }

    @Test
    void should_find_company_by_id() {
        final Optional<Company> companyById = companyRepository.findById(1L);
        assertTrue(companyById.isPresent());
    }

    @Test
    void should_update_company_status_by_id() {
        companyRepository.updateStatus(Status.BANNED, 1L);
        final Company company = companyRepository.findById(1L).get();
        assertEquals(Status.BANNED, company.getStatus());
    }

    @Test
    void should_delete_company_by_id() {
        companyRepository.deleteById(1L);
        final Optional<Company> companyById = companyRepository.findById(1L);
        assertTrue(companyById.isEmpty());
    }
}