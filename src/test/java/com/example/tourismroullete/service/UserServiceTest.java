package com.example.tourismroullete.service;

import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private PasswordEncoder mockPasswordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("rawPassword");
    }

    @Test
    void encodePassword_shouldCallPasswordEncoder() {
        // Arrange
        String rawPass = "password123";
        String encodedPass = "encodedPassword123";
        when(mockPasswordEncoder.encode(rawPass)).thenReturn(encodedPass);

        // Act
        String result = userService.encodePassword(rawPass);

        // Assert
        assertEquals(encodedPass, result);
        verify(mockPasswordEncoder, times(1)).encode(rawPass);
    }

    @Test
    void isUsernameAvailable_shouldCallRepositoryExistsByUsername() {
        // Arrange
        String username = "existingUser";
        when(mockUserRepository.existsByUsername(username)).thenReturn(true);

        // Act
        boolean isAvailable = userService.isUsernameAvailable(username);

        // Assert
        assertTrue(isAvailable);
        verify(mockUserRepository, times(1)).existsByUsername(username);
    }

    @Test
    void isEmailAvailable_shouldCallRepositoryExistsByEmail() {
        // Arrange
        String email = "new@example.com";
        when(mockUserRepository.existsByEmail(email)).thenReturn(false);

        // Act
        boolean isAvailable = userService.isEmailAvailable(email);

        // Assert
        assertFalse(isAvailable);
        verify(mockUserRepository, times(1)).existsByEmail(email);
    }

    @Test
    void findByUsername_shouldCallRepositoryFindByUsername() {
        // Arrange
        String username = "findMe";
        when(mockUserRepository.findByUsername(username)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> foundUserOpt = userService.findByUsername(username);

        // Assert
        assertTrue(foundUserOpt.isPresent());
        assertEquals(testUser, foundUserOpt.get());
        verify(mockUserRepository, times(1)).findByUsername(username);
    }

    @Test
    void registerNewUser_shouldEncodePasswordAndSaveUser() {
        // Arrange
        String rawPassword = testUser.getPassword();
        String encodedPassword = "encodedPassword";
        when(mockPasswordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(mockUserRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User registeredUser = userService.registerNewUser(testUser);

        // Assert
        assertNotNull(registeredUser);
        assertEquals(encodedPassword, registeredUser.getPassword());
        verify(mockPasswordEncoder, times(1)).encode(rawPassword);
        verify(mockUserRepository, times(1)).save(testUser);
    }
}
