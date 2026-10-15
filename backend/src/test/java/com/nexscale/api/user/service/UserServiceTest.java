package com.nexscale.api.user.service;

import com.nexscale.api.common.exception.ResourceNotFoundException;
import com.nexscale.api.common.exception.RoleNotAllowedException;
import com.nexscale.api.user.dto.RegisterRequest;
import com.nexscale.api.user.dto.UserResponse;
import com.nexscale.api.user.entity.Role;
import com.nexscale.api.user.entity.User;
import com.nexscale.api.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Registrasi dengan role ADMIN harus melempar RoleNotAllowedException dan save tidak dipanggil")
    void registerUser_roleAdmin_throwsRoleNotAllowedException() {
        // Arrange
        RegisterRequest request = new RegisterRequest(
                "admin@example.com",
                "password123",
                Role.ADMIN,
                "Admin User"
        );

        // Act & Assert
        RoleNotAllowedException exception = assertThrows(
                RoleNotAllowedException.class,
                () -> userService.registerUser(request)
        );

        assertEquals("Registrasi publik untuk role ADMIN tidak diizinkan", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Registrasi dengan role CLIENT berhasil menyimpan user dan mengembalikan UserResponse")
    void registerUser_roleClient_success() {
        RegisterRequest request = new RegisterRequest(
                "client@example.com",
                "password123",
                Role.CLIENT,
                "Client User"
        );

        when(userRepository.existsByEmail("client@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedPasswordContoh");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(java.util.UUID.randomUUID());
            return user;
        });

        var response = userService.registerUser(request);

        assertEquals("client@example.com", response.getEmail());
        assertEquals(Role.CLIENT, response.getRole());
        assertEquals("Client User", response.getFullName());
        
        // Verifikasi bahwa password yang disimpan ke database adalah hash BCrypt, bukan plain text
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("$2a$10$hashedPasswordContoh", userCaptor.getValue().getPasswordHash());
    }

    @Test
    @DisplayName("Registrasi dengan email duplikat harus melempar DuplicateResourceException dan save tidak dipanggil")
    void registerUser_duplicateEmail_throwsDuplicateResourceException() {
        RegisterRequest request = new RegisterRequest(
                "existing@example.com",
                "password123",
                Role.CLIENT,
                "Existing User"
        );

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(
                com.nexscale.api.common.exception.DuplicateResourceException.class,
                () -> userService.registerUser(request)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("getUserById dengan UUID yang terdaftar berhasil mengembalikan UserResponse yang sesuai tanpa field password")
    void getUserById_userFound_returnsUserResponse() {
        UUID userId = UUID.randomUUID();
        User dummyUser = new User("client@example.com", "$2a$10$hashedPasswordContoh", Role.CLIENT, "Client User");
        dummyUser.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(dummyUser));

        UserResponse response = userService.getUserById(userId);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals("client@example.com", response.getEmail());
        assertEquals(Role.CLIENT, response.getRole());
        assertEquals("Client User", response.getFullName());
        assertFalse(response.isVerified());

        // Verifikasi bahwa objek DTO UserResponse tidak memiliki field password atau passwordHash
        boolean hasPasswordField = Arrays.stream(UserResponse.class.getDeclaredFields())
                .anyMatch(field -> field.getName().toLowerCase().contains("password"));
        assertFalse(hasPasswordField, "UserResponse tidak boleh memiliki field password atau passwordHash");
    }

    @Test
    @DisplayName("getUserById dengan UUID tidak terdaftar harus melempar ResourceNotFoundException dengan pesan Pengguna tidak ditemukan")
    void getUserById_userNotFound_throwsResourceNotFoundException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(userId)
        );

        assertEquals("Pengguna tidak ditemukan", exception.getMessage());
        verify(userRepository).findById(userId);
    }
}
