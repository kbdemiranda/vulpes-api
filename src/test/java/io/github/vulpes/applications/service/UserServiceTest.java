package io.github.vulpes.applications.service;

import io.github.vulpes.applications.dto.UserDTO;
import io.github.vulpes.applications.service.impl.UserServiceImpl;
import io.github.vulpes.domain.models.Profile;
import io.github.vulpes.domain.models.User;
import io.github.vulpes.infrastructure.jpa.ProfileRepository;
import io.github.vulpes.infrastructure.jpa.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, profileRepository, passwordEncoder);
    }

    @Test
    void testRegisterUser() {
        UserDTO dto = new UserDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john@doe.com");
        dto.setPassword("123");
        dto.setProfileIds(Collections.singletonList(1L));

        Profile profile = new Profile();
        profile.setId(1L);

        when(profileRepository.findAllById(dto.getProfileIds())).thenReturn(Collections.singletonList(profile));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserDTO result = userService.registerUser(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertTrue(passwordEncoder.matches("123", saved.getPassword()));
        assertEquals(dto.getFirstName(), result.getFirstName());
    }

    @Test
    void testFindUserById() {
        User user = User.builder().id(1L).firstName("John").lastName("Doe").email("john@doe.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO dto = userService.findUserById(1L);
        assertEquals(user.getFirstName(), dto.getFirstName());
    }

    @Test
    void testUpdateUser() {
        User user = User.builder().id(1L).firstName("John").lastName("Doe").email("old@doe.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO dto = new UserDTO();
        dto.setFirstName("Novo");
        dto.setLastName("Nome");
        dto.setEmail("novo@teste.com");
        dto.setProfileIds(Collections.singletonList(1L));
        dto.setPassword("123");

        UserDTO result = userService.updateUser(1L, dto);

        assertEquals(dto.getFirstName(), result.getFirstName());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdatePassword() {
        User user = User.builder().id(1L).password("antiga").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.updatePassword(1L, "nova");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertTrue(passwordEncoder.matches("nova", saved.getPassword()));
    }

    @Test
    void testUpdateProfiles() {
        User user = new User();
        user.setId(1L);
        Profile profile = new Profile();
        profile.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(profileRepository.findAllById(Arrays.asList(1L))).thenReturn(Arrays.asList(profile));

        userService.updateProfiles(1L, Arrays.asList(1L));

        verify(userRepository).save(user);
        assertEquals(1, user.getProfiles().size());
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).deleteUser(1L);
    }
}
