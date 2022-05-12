package com.modsen.cardissuer.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.Status;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyResponseDto {
    private Long id;
    private String name;
    private Status status;

    public static CompanyResponseDto fromCompany(Company company) {
        final CompanyResponseDto companyResponseDto = new CompanyResponseDto();
        companyResponseDto.setId(company.getId());
        companyResponseDto.setName(company.getName());
        companyResponseDto.setStatus(company.getStatus());
        return companyResponseDto;
    }
}
