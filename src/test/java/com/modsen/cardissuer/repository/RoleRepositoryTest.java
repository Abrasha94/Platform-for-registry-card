package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


class RoleRepositoryTest extends AbstractRepositoryTest{

    @Autowired
    RoleRepository roleRepository;

    Role role = new Role();

    @BeforeEach
    void setUp() {
        role.setName("test");
        roleRepository.save(role);
    }


    @Test
    void whenSaveRole_thenReturnRightRole() {
        Role roleNew = new Role();
        roleNew.setName("test name");

        final Role saveRole = roleRepository.save(roleNew);

        assertThat(saveRole.getName()).isEqualTo(roleNew.getName());
    }

    @Test
    void whenFindRoleById_thenReturnRole() {
        final List<Role> all = roleRepository.findAll();

        assertThat(all).isNotEmpty();

        final Optional<Role> byId = roleRepository.findById(all.get(0).getId());

        assertThat(byId).isPresent();
        assertThat(byId.get()).isEqualTo(role);
    }

    @Test
    void whenUpdateRoleById_thenRoleUpdated() {
        final List<Role> all = roleRepository.findAll();

        assertThat(all).isNotEmpty();

        final Role changedRole = roleRepository.findById(all.get(0).getId()).get();
        changedRole.setName("test test");
        final Role saveRole = roleRepository.save(changedRole);

        assertThat(saveRole.getName()).isEqualTo(changedRole.getName());
    }

    @Test
    void whenDeleteById_thenRoleDeleted() {
        final List<Role> all = roleRepository.findAll();

        assertThat(all).isNotEmpty();

        roleRepository.deleteById(all.get(0).getId());

        final Optional<Role> byId = roleRepository.findById(all.get(0).getId());

        assertThat(byId).isEmpty();
    }
}