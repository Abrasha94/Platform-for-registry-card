package com.modsen.cardissuer.service;

import com.modsen.cardissuer.dto.request.ChangeCompanyStatusDto;
import com.modsen.cardissuer.dto.response.CompanyResponseDto;
import com.modsen.cardissuer.dto.request.RegisterCompanyDto;
import com.modsen.cardissuer.exception.CompanyNotFoundException;
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

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Company save(RegisterCompanyDto registerCompanyDto) {
        final Company company = new Company();
        company.setName(registerCompanyDto.getName());
        company.setStatus(Status.ACTIVE);
        return companyRepository.save(company);
    }

    public Company changeStatus(Long id, ChangeCompanyStatusDto dto) {
        final Company company = findById(id);
        if (company == null) {
            throw new CompanyNotFoundException("Company not found!");
        }
        company.setStatus(dto.getStatus());
        companyRepository.updateStatus(dto.getStatus(), id);
        final List<User> users = company.getUsers();
        for (User user : users) {
            userRepository.updateStatus(dto.getStatus(), user.getId());
        }
        return company;
    }

    public Company findById(Long id) {
        return companyRepository.findById(id).orElse(null);
    }

    public List<CompanyResponseDto> findAll() {
        final List<Company> companies = companyRepository.findAll();
        return companies.stream()
                .map(CompanyResponseDto::fromCompany)
                .collect(Collectors.toList());
    }
}
