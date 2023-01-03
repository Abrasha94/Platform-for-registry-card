package com.modsen.cardissuer.service;

import com.modsen.cardissuer.model.Access;
import com.modsen.cardissuer.repository.AccessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
class AccessServiceIT {

    @Autowired
    AccessService accessService;

    @MockBean
    AccessRepository accessRepository;

    Access access1 = new Access();
    Access access2 = new Access();

    @BeforeEach
    void setUp() {
        access1.setId(1L);
        access1.setPermission("test1");
        access2.setId(2L);
        access2.setPermission("test2");
    }

    @Test
    void should_return_all_access() {
        doReturn(Arrays.asList(access1, access2)).when(accessRepository).findAll();
        final List<Access> allAccess = accessService.getAllAccess();
        assertEquals(2, allAccess.size());
    }
}