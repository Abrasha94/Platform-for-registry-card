package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.Access;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


class AccessRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    AccessRepository accessRepository;

    Access access = new Access();

    @BeforeEach
    void setUp() {
        access.setPermission("test");
        accessRepository.save(access);
    }

    @Test
    void whenSaveAccess_thenReturnRightAccess() {
        Access accessNew = new Access();
        accessNew.setPermission("test permission");

        final Access savedAccess = accessRepository.save(accessNew);

        assertThat(savedAccess.getPermission()).isEqualTo(accessNew.getPermission());
    }

    @Test
    void whenFindAccessByPermission_thenReturnAccess() {
        Access foundedAccess = accessRepository.findByPermission("test");

        assertThat(foundedAccess).isEqualTo(access);
    }

    @Test
    void whenFindAccessById_thenReturnAccess() {
        final List<Access> all = accessRepository.findAll();

        assertThat(all).isNotEmpty();

        final Optional<Access> byId = accessRepository.findById(all.get(0).getId());

        assertThat(byId).isPresent();
        assertThat(byId.get()).isEqualTo(access);
    }

    @Test
    void whenUpdateAccessById_thenAccessUpdated() {
        final List<Access> all = accessRepository.findAll();

        assertThat(all).isNotEmpty();

        final Access changedAccess = accessRepository.findById(all.get(0).getId()).get();
        changedAccess.setPermission("test test");
        final Access saveAccess = accessRepository.save(changedAccess);

        assertThat(saveAccess.getPermission()).isEqualTo(changedAccess.getPermission());
    }

    @Test
    void whenDeleteById_thenAccessDeleted() {
        final List<Access> all = accessRepository.findAll();

        assertThat(all).isNotEmpty();

        accessRepository.deleteById(all.get(0).getId());

        final Optional<Access> byId = accessRepository.findById(all.get(0).getId());

        assertThat(byId).isEmpty();
    }
}