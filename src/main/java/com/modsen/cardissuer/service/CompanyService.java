package com.modsen.cardissuer.service;

import com.modsen.cardissuer.dto.request.ChangeCompanyStatusDto;
import com.modsen.cardissuer.dto.response.CompanyResponseDto;
import com.modsen.cardissuer.dto.request.RegisterCompanyDto;
import com.modsen.cardissuer.exception.CompanyNotFoundException;
import com.modsen.cardissuer.exception.UserNotFoundException;
import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.Status;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.repository.CompanyRepository;
import com.modsen.cardissuer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Autowired
    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company save(RegisterCompanyDto registerCompanyDto) {
        final Company company = new Company();
        company.setName(registerCompanyDto.getName());
        company.setStatus(Status.ACTIVE);
        return companyRepository.save(company);
    }

    public Company changeStatus(Long id, ChangeCompanyStatusDto dto) {
        final Company company = companyRepository.findById(id).orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
        company.setStatus(dto.getStatus());
        final Company savedCompany = companyRepository.save(company);

        final List<User> users = savedCompany.getUsers();
        for (User user : users) {
            final User foundedUser = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User not found!"));
            foundedUser.setStatus(dto.getStatus());
            userRepository.save(user);
        }

        return savedCompany;
    }

    public Company findById(Long id) {
        return companyRepository.findById(id).orElseThrow(() -> new CompanyNotFoundException("Company not found!"));
    }

    public List<CompanyResponseDto> findAll() {
        final List<Company> companies = companyRepository.findAll();
        if (companies.isEmpty()) {
            throw new CompanyNotFoundException("Company not found!");
        }
        return companies.stream()
                .map(CompanyResponseDto::fromCompany)
                .collect(Collectors.toList());
    }
}
