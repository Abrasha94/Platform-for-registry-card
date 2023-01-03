package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {

    @Autowired
    RoleRepository roleRepository;

    @Test
    void should_create_role() {
        Role role = new Role();
        role.setName("test role");
        final Role savedRole = roleRepository.save(role);
        assertEquals("test role", savedRole.getName());
    }

    @Test
    void should_find_role_by_id() {
        final Role foundedRole = roleRepository.findById(2L).get();
        assertEquals("moderator",foundedRole.getName());
    }

    @Test
    void should_update_role_by_id() {
        final Role foundedRole = roleRepository.findById(3L).get();
        foundedRole.setName("test name");
        roleRepository.save(foundedRole);
        final Role changedRole = roleRepository.findById(3L).get();
        assertEquals(foundedRole.getId(), changedRole.getId());
        assertEquals("test name", changedRole.getName());
    }

    @Test
    void should_delete_role_by_id() {
        roleRepository.deleteById(4L);
        final List<Role> allRoles = roleRepository.findAll();
        assertEquals(3, allRoles.size());
    }
}