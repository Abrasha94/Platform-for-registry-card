package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


class UserRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    CompanyRepository companyRepository;

    User user = new User();
    Company company = new Company();
    Role role = new Role();

    @BeforeEach
    void setUp() {
        company.setName("test");
        company.setStatus(Status.ACTIVE);
        final Company savedCompany = companyRepository.save(company);

        role.setName("test");
        final Role savedRole = roleRepository.save(role);

        user.setKeycloakUserId("test");
        user.setName("test");
        user.setPassword("test");
        user.setStatus(Status.ACTIVE);
        user.setCompany(savedCompany);
        user.setRole(savedRole);
        userRepository.save(user);
    }

    @Test
    void whenCreateNewUser_thenReturnRightUser() {
        final User newUser = createNewUser();

        final User savedUser = userRepository.save(newUser);

        assertThat(savedUser.getName()).isEqualTo("test name");
    }


    @Test
    void whenFindUserById_thenReturnUser() {
        final List<User> all = userRepository.findAll();

        assertThat(all).isNotEmpty();

        final Optional<User> byId = userRepository.findById(all.get(0).getId());

        assertThat(byId).isPresent();
        assertThat(byId.get()).isEqualTo(user);
    }

    @Test
    void whenFindUserByKeycloakUserId_thenReturnUser() {
        final Optional<User> foundedUser = userRepository.findByKeycloakUserId("test");

        assertThat(foundedUser).isPresent();
        assertThat(foundedUser.get()).isEqualTo(user);
    }

    @Test
    void whenFindUserByName_thenReturnUser() {
        Optional<User> foundedUser = userRepository.findByName("test");

        assertThat(foundedUser).isPresent();
        assertThat(foundedUser.get()).isEqualTo(user);
    }

    @Test
    void whenFindUserByRole_thenReturnListOfUsers() {
        List<User> userList = userRepository.findByRole(role);

        assertThat(userList).isNotEmpty();
        assertThat(userList.get(0)).isEqualTo(user);
    }

    @Test
    void whenUpdateUserById_thenUserUpdated() {
        final List<User> all = userRepository.findAll();

        assertThat(all).isNotEmpty();

        final User changedUser = userRepository.findById(all.get(0).getId()).get();
        changedUser.setName("test test");
        final User saveUser = userRepository.save(changedUser);

        assertThat(saveUser.getName()).isEqualTo(changedUser.getName());
    }

    @Test
    void whenDeleteById_thenUserDeleted() {
        final List<User> all = userRepository.findAll();

        assertThat(all).isNotEmpty();

        userRepository.deleteById(all.get(0).getId());

        final Optional<User> byId = userRepository.findById(all.get(0).getId());

        assertThat(byId).isEmpty();
    }

    private User createNewUser() {
        User userNew = new User();
        userNew.setKeycloakUserId("test Id");
        userNew.setName("test name");
        userNew.setPassword("test pass");
        userNew.setStatus(Status.ACTIVE);
        userNew.setCompany(companyRepository.save(company));
        userNew.setRole(roleRepository.save(role));
        return userNew;
    }
}
