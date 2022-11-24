package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.Role;
import com.modsen.cardissuer.model.Status;
import com.modsen.cardissuer.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


class UserRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    UserRepository userRepository;

    User user = new User();
    Company company = new Company();
    Role role = new Role();

    @BeforeEach
    void setUp() {
        company.setId(1L);
        role.setId(1L);
        user.setKeycloakUserId("test");
        user.setName("test");
        user.setPassword("test");
        user.setStatus(Status.ACTIVE);
        user.setCompany(company);
        user.setRole(role);
        userRepository.save(user);
    }

    @Test
    void should_create_user() {
        User userNew = new User();
        userNew.setKeycloakUserId("test");
        userNew.setName("test name");
        userNew.setPassword("test pass");
        userNew.setStatus(Status.ACTIVE);
        userNew.setCompany(company);
        userNew.setRole(role);
        final User savedUser = userRepository.save(user);

        assertThat(savedUser.getName()).isEqualTo("test name");
    }

//    @Test
//    void should_find_user_by_id() {
//        final User foundedUser = userRepository.findById(1L).get();
//        assertEquals("admin", foundedUser.getName());
//    }
//
//    @Test
//    void should_find_user_by_name() {
//        final User user = userRepository.findByName("admin").get();
//        assertEquals(1L, user.getId());
//    }
//
//    @Test
//    void should_find_user_by_role() {
//        Role role = new Role();
//        role.setId(1L);
//        final User user = userRepository.findByRole(role).get(0);
//        assertEquals("admin", user.getName());
//    }
//
//    @Test
//    void should_update_user_status_by_id() {
//        userRepository.updateStatus(Status.BANNED, 1L);
//        final User user = userRepository.findById(1L).get();
//        assertEquals(Status.BANNED, user.getStatus());
//    }
//
//    @Test
//    void should_update_user_password_by_id() {
//        userRepository.updatePassword("test pass", 1L);
//        final User user = userRepository.findById(1L).get();
//        assertEquals("test pass", user.getPassword());
//    }
//
//    @Test
//    void should_delete_user_by_id() {
//        userRepository.deleteById(1L);
//        final Optional<User> userById = userRepository.findById(1L);
//        assertTrue(userById.isEmpty());
//    }
}