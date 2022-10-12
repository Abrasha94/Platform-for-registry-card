package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void should_create_user() {
        Company company = new Company();
        company.setId(1L);
        Role role = new Role();
        role.setId(1L);
        User user = new User();
        user.setName("test name");
        user.setPassword("test pass");
        user.setStatus(Status.ACTIVE);
        user.setCompany(company);
        user.setRole(role);
        final User savedUser = userRepository.save(user);
        assertEquals("test name", savedUser.getName());
        assertEquals("test pass", savedUser.getPassword());
        assertEquals(Status.ACTIVE, savedUser.getStatus());
        assertEquals(company.getId(), savedUser.getCompany().getId());
        assertEquals(role.getId(), savedUser.getRole().getId());
    }

    @Test
    void should_find_user_by_id() {
        final User foundedUser = userRepository.findById(1L).get();
        assertEquals("admin", foundedUser.getName());
    }

    @Test
    void should_find_user_by_name() {
        final User user = userRepository.findByName("admin").get();
        assertEquals(1L, user.getId());
    }

    @Test
    void should_find_user_by_role() {
        Role role = new Role();
        role.setId(1L);
        final User user = userRepository.findByRole(role).get(0);
        assertEquals("admin", user.getName());
    }

    @Test
    void should_update_user_status_by_id() {
        userRepository.updateStatus(Status.BANNED, 1L);
        final User user = userRepository.findById(1L).get();
        assertEquals(Status.BANNED, user.getStatus());
    }

    @Test
    void should_update_user_password_by_id() {
        userRepository.updatePassword("test pass", 1L);
        final User user = userRepository.findById(1L).get();
        assertEquals("test pass", user.getPassword());
    }

    @Test
    void should_delete_user_by_id() {
        userRepository.deleteById(1L);
        final Optional<User> userById = userRepository.findById(1L);
        assertTrue(userById.isEmpty());
    }
}