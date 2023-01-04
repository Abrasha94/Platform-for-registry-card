package com.modsen.cardissuer.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.Status;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyResponse {
    private Long id;
    private String name;
    private Status status;

    public static CompanyResponse fromCompany(Company company) {
        final CompanyResponse companyResponse = new CompanyResponse();
        companyResponse.setId(company.getId());
        companyResponse.setName(company.getName());
        companyResponse.setStatus(company.getStatus());
        return companyResponse;
    }
}
