package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


class CompanyRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    CompanyRepository companyRepository;

    Company company = new Company();

    @BeforeEach
    void setUp() {
        company.setName("test");
        company.setStatus(Status.ACTIVE);
        companyRepository.save(company);
    }

    @Test
    void whenSaveCompany_thenReturnRightCompany() {
        Company companyNew = new Company();
        companyNew.setName("test name");
        companyNew.setStatus(Status.ACTIVE);

        final Company saveCompany = companyRepository.save(companyNew);

        assertThat(saveCompany.getName()).isEqualTo(companyNew.getName());
    }

    @Test
    void whenFindCompanyById_thenReturnCompany() {
        final List<Company> all = companyRepository.findAll();

        assertThat(all).isNotEmpty();

        final Optional<Company> byId = companyRepository.findById(all.get(0).getId());

        assertThat(byId).isPresent();
        assertThat(byId.get()).isEqualTo(company);
    }

    @Test
    void whenUpdateCompanyById_thenCompanyUpdated() {
        final List<Company> all = companyRepository.findAll();

        assertThat(all).isNotEmpty();

        final Company changedCompany = companyRepository.findById(all.get(0).getId()).get();
        changedCompany.setName("test test");
        final Company saveCompany = companyRepository.save(changedCompany);

        assertThat(saveCompany.getName()).isEqualTo(changedCompany.getName());
    }

    @Test
    void whenDeleteById_thenCompanyDeleted() {
        final List<Company> all = companyRepository.findAll();

        assertThat(all).isNotEmpty();

        companyRepository.deleteById(all.get(0).getId());

        final Optional<Company> byId = companyRepository.findById(all.get(0).getId());

        assertThat(byId).isEmpty();
    }
}