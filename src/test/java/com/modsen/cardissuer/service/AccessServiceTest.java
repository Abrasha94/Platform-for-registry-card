package com.modsen.cardissuer.service;

import com.modsen.cardissuer.model.Access;
import com.modsen.cardissuer.repository.AccessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessServiceTest {

    @InjectMocks
    AccessService accessService;

    @Mock
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
    void whenGetAllAccess_thenReturnAllAccess() {
        when(accessRepository.findAll()).thenReturn(List.of(access1, access2));

        final List<Access> allAccess = accessService.getAllAccess();

        verify(accessRepository, times(1)).findAll();
        assertThat(allAccess.size()).isEqualTo(2);
    }
}