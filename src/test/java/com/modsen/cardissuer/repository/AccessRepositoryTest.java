package com.modsen.cardissuer.repository;

import com.modsen.cardissuer.model.Access;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccessRepositoryTest {

    @Autowired
    AccessRepository accessRepository;

    @Test
    void should_create_access() {
        Access access = new Access();
        access.setPermission("test permission");
        final Access savedAccess = accessRepository.save(access);
        assertEquals(access.getPermission(), savedAccess.getPermission());
    }

    @Test
    void should_find_access_by_permission() {
        Access access = accessRepository.findByPermission("admin permission");
        assertEquals(1L, access.getId());
    }

    @Test
    void should_find_access_by_id() {
        Access access = accessRepository.findById(1L).get();
        assertEquals("admin permission", access.getPermission());
    }

    @Test
    void should_find_all_access() {
        List<Access> accessList = accessRepository.findAll();
        assertEquals(4, accessList.size());
    }


    @Test
    void should_update_access_by_id() {
        final Access changedAccess = accessRepository.findById(1L).get();
        changedAccess.setPermission("test permission");
        accessRepository.save(changedAccess);
        final Access checkAccess = accessRepository.findById(1L).get();
        assertEquals("test permission", checkAccess.getPermission());
    }

    @Test
    void should_delete_access_by_id() {
        accessRepository.deleteById(1L);
        final Optional<Access> byId = accessRepository.findById(1L);
        assertTrue(byId.isEmpty());
    }
}