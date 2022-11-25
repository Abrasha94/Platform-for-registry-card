package com.modsen.cardissuer.service;

import com.modsen.cardissuer.dto.request.AccountantRegisterUserDto;
import com.modsen.cardissuer.dto.request.AdminRegisterUserDto;
import com.modsen.cardissuer.dto.request.ChangeUserPermissionDto;
import com.modsen.cardissuer.dto.request.ChangeUserStatusDto;
import com.modsen.cardissuer.dto.response.UserResponseDto;
import com.modsen.cardissuer.model.Access;
import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.Role;
import com.modsen.cardissuer.model.Status;
import com.modsen.cardissuer.model.User;
import com.modsen.cardissuer.repository.AccessRepository;
import com.modsen.cardissuer.repository.CompanyRepository;
import com.modsen.cardissuer.repository.RoleRepository;
import com.modsen.cardissuer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    CompanyRepository companyRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    AccessRepository accessRepository;

    @Mock
    BCryptPasswordEncoder encoder;

    User user = new User();
    Access access = new Access();
    HttpServletRequest request = new MockHttpServletRequest();

    @BeforeEach
    void setUp() {
        access.setPermission("test");
        user.setId(1L);
        user.setAccessSet(Set.of(access));
        user.setStatus(Status.ACTIVE);
        user.setKeycloakUserId("test");
        user.setName("test");
        user.setPassword("test");
        user.setCompany(new Company());
        user.setRole(new Role());
    }

    @Test
    void WhenSaveUser_thenReturnRightUser() {
        when(companyRepository.findById(1L)).thenReturn(Optional.of(new Company()));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(new Role()));
        when(accessRepository.findById(1L)).thenReturn(Optional.of(new Access()));
        when(userRepository.save(any(User.class))).thenReturn(user);

        final User savedUser = userService.save(
                new AdminRegisterUserDto("test", "test", 1L, 1L, Set.of(1L)));

        verify(companyRepository, times(1)).findById(1L);
        verify(roleRepository, times(1)).findById(1L);
        verify(accessRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
        assertThat(savedUser).isEqualTo(user);
    }

    @Test
    void whenSaveInCompany_thenReturnRightUser() {
        when(userRepository.findByKeycloakUserId(any())).thenReturn(Optional.of(user));
        when(encoder.encode("test")).thenReturn("test");
        when(roleRepository.findById(4L)).thenReturn(Optional.of(new Role()));
        when(accessRepository.findById(4L)).thenReturn(Optional.of(new Access()));
        when(userRepository.save(any(User.class))).thenReturn(user);

        final User savedUser =
                userService.saveInCompany(new AccountantRegisterUserDto("test", "test"), request);

        verify(userRepository, times(1)).findByKeycloakUserId(any());
        verify(encoder, times(1)).encode("test");
        verify(roleRepository, times(1)).findById(4L);
        verify(accessRepository, times(1)).findById(4L);
        verify(userRepository, times(1)).save(any(User.class));
        assertThat(savedUser).isEqualTo(user);
    }

    @Test
    void whenChangeStatus_thenReturnUpdatedUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        final User changedUser = userService.changeStatus(1L, new ChangeUserStatusDto(Status.BANNED));

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
        assertThat(changedUser).isEqualTo(user);
    }

    @Test
    void whenChangePermission_thenReturnUpdatedUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(accessRepository.findById(5L)).thenReturn(Optional.of(new Access()));
        when(userRepository.save(any(User.class))).thenReturn(user);

        final User updatedUser = userService.changePermission(1L, new ChangeUserPermissionDto(Set.of(5L)));

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
        verify(accessRepository, times(1)).findById(5L);
        assertThat(updatedUser).isEqualTo(user);
    }

    @Test
    void whenFindByName_thenReturnFoundedUser() {
        when(userRepository.findByName("test")).thenReturn(Optional.of(user));

        final User foundedUser = userService.findByName("test");

        verify(userRepository, times(1)).findByName("test");
        assertThat(foundedUser).isEqualTo(user);
    }

    @Test
    void whenFindById_thenReturnFoundedUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        final User foundedUser = userService.findById(1L);

        verify(userRepository, times(1)).findById(1L);
        assertThat(foundedUser).isEqualTo(user);
    }

    @Test
    void whenFindAllAccountants_thenReturnRightDto() {
        when(roleRepository.findById(3L)).thenReturn(Optional.of(new Role()));
        when(userRepository.findByRole(any(Role.class))).thenReturn(List.of(user));

        final List<UserResponseDto> allAccountants = userService.findAllAccountants();

        verify(roleRepository, times(1)).findById(3L);
        verify(userRepository, times(1)).findByRole(any(Role.class));
        assertThat(allAccountants.size()).isEqualTo(1);
        assertThat(allAccountants.get(0).getId()).isEqualTo(1L);
    }
}