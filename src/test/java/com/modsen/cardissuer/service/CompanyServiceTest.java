package com.modsen.cardissuer.service;

import com.modsen.cardissuer.dto.request.ChangeCompanyStatusDto;
import com.modsen.cardissuer.dto.request.RegisterCompanyDto;
import com.modsen.cardissuer.dto.response.CompanyResponseDto;
import com.modsen.cardissuer.exception.CompanyNotFoundException;
import com.modsen.cardissuer.exception.UserNotFoundException;
import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.Status;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.repository.CompanyRepository;
import com.modsen.cardissuer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @InjectMocks
    CompanyService companyService;

    @Mock
    CompanyRepository companyRepository;

    @Mock
    UserRepository userRepository;

    Company company = new Company();
    User user = new User();

    @BeforeEach
    void setUp() {
        company.setId(1L);
        company.setStatus(Status.ACTIVE);
        company.setName("test");
        company.setUsers(List.of(user));
    }

    @Test
    void whenSaveCompany_thenReturnRightCompany() {
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        final Company saveCompany = companyService.save(new RegisterCompanyDto("test name"));

        verify(companyRepository, times(1)).save(any(Company.class));
        assertThat(saveCompany).isEqualTo(company);
    }

    @Test
    void whenChangeStatus_thenStatusChangedInUsersToo() {
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        final Company updatedCompany = companyService.changeStatus(1L, new ChangeCompanyStatusDto(Status.BANNED));

        verify(companyRepository, times(1)).findById(1L);
        verify(companyRepository, times(1)).save(any(Company.class));
        verify(userRepository, times(1)).findById(any());
        verify(userRepository, times(1)).save(any(User.class));
        assertThat(updatedCompany).isEqualTo(company);
    }

    @Test
    void whenChangeStatusIsBad_thenThrowExceptions() {
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.changeStatus(1L, new ChangeCompanyStatusDto(Status.BANNED)))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessage("Company not found!");

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(company);
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.changeStatus(1L, new ChangeCompanyStatusDto(Status.BANNED)))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found!");
    }

    @Test
    void whenFindById_thenCompanyFounded() {
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        final Company byId = companyService.findById(1L);

        verify(companyRepository, times(1)).findById(1L);
        assertThat(byId).isEqualTo(company);
    }

    @Test
    void whenFindByIdIsNotFind_thenThrowException() {
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.findById(1L))
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessage("Company not found!");
    }

    @Test
    void whenFindAll_thenReturnRightDto() {
        when(companyRepository.findAll()).thenReturn(List.of(company));

        final List<CompanyResponseDto> all = companyService.findAll();

        verify(companyRepository, times(1)).findAll();
        assertThat(all.get(0).getName()).isEqualTo(company.getName());
    }

    @Test
    void whenFindAllEmptyList_thenThrowException() {
        when(companyRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> companyService.findAll())
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessage("Company not found!");
    }
}