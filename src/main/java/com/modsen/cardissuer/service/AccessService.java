package com.modsen.cardissuer.service;

import com.modsen.cardissuer.model.Access;
import com.modsen.cardissuer.repository.AccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccessService {

    private final AccessRepository accessRepository;

    @Autowired
    public AccessService(AccessRepository accessRepository) {
        this.accessRepository = accessRepository;
    }

    public List<Access> getAllAccess() {
        return accessRepository.findAll();
    }
}
