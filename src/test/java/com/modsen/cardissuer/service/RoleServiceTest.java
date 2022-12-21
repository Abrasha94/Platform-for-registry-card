package com.modsen.cardissuer.service;

import com.modsen.cardissuer.model.Role;
import com.modsen.cardissuer.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    RoleService roleService;

    @Mock
    RoleRepository roleRepository;

    @Test
    void whenGetAllRoles_thenReturnOneRole() {
        Mockito.when(roleRepository.findAll()).thenReturn(List.of(new Role()));

        final List<Role> allRoles = roleService.getAllRoles();

        assertThat(allRoles.size()).isEqualTo(1);
    }
}