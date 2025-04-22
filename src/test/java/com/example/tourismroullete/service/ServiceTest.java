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

@ExtendWith(MockitoExtension.class) // Enable Mockito annotations
class UserServiceTest {

    @Mock // Create a mock for UserRepository
    private UserRepository mockUserRepository;

    @Mock // Create a mock for PasswordEncoder
    private PasswordEncoder mockPasswordEncoder;

    @InjectMocks // Create an instance of UserService and inject the mocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Basic setup for a user object if needed for multiple tests
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("rawPassword"); // Set raw password for registration test
    }

    @Test
    void encodePassword_shouldCallPasswordEncoder() {
        // Arrange
        String rawPass = "password123";
        String encodedPass = "encodedPassword123";
        when(mockPasswordEncoder.encode(rawPass)).thenReturn(encodedPass); // Define mock behavior

        // Act
        String result = userService.encodePassword(rawPass);

        // Assert
        assertEquals(encodedPass, result); // Check if the result matches the mock's return value
        verify(mockPasswordEncoder, times(1)).encode(rawPass); // Verify encode was called once
    }

    @Test
    void isUsernameAvailable_shouldCallRepositoryExistsByUsername() {
        // Arrange
        String username = "existingUser";
        when(mockUserRepository.existsByUsername(username)).thenReturn(true); // Mock repo response

        // Act
        boolean isAvailable = userService.isUsernameAvailable(username);

        // Assert
        assertTrue(isAvailable); // Check the result
        verify(mockUserRepository, times(1)).existsByUsername(username); // Verify repo call
    }

    @Test
    void isEmailAvailable_shouldCallRepositoryExistsByEmail() {
        // Arrange
        String email = "new@example.com";
        when(mockUserRepository.existsByEmail(email)).thenReturn(false); // Mock repo response

        // Act
        boolean isAvailable = userService.isEmailAvailable(email);

        // Assert
        assertFalse(isAvailable); // Check the result
        verify(mockUserRepository, times(1)).existsByEmail(email); // Verify repo call
    }

    @Test
    void findByUsername_shouldCallRepositoryFindByUsername() {
        // Arrange
        String username = "findMe";
        when(mockUserRepository.findByUsername(username)).thenReturn(Optional.of(testUser)); // Mock repo response

        // Act
        Optional<User> foundUserOpt = userService.findByUsername(username);

        // Assert
        assertTrue(foundUserOpt.isPresent()); // Check if user was found
        assertEquals(testUser, foundUserOpt.get()); // Check if it's the correct user
        verify(mockUserRepository, times(1)).findByUsername(username); // Verify repo call
    }

    @Test
    void registerNewUser_shouldEncodePasswordAndSaveUser() {
        // Arrange
        String rawPassword = testUser.getPassword(); // "rawPassword"
        String encodedPassword = "encodedPassword";

        when(mockPasswordEncoder.encode(rawPassword)).thenReturn(encodedPassword); // Mock encoding
        // Mock save to just return the user passed to it
        when(mockUserRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User registeredUser = userService.registerNewUser(testUser);

        // Assert
        assertNotNull(registeredUser);
        assertEquals(encodedPassword, registeredUser.getPassword()); // IMPORTANT: Check if password was encoded
        verify(mockPasswordEncoder, times(1)).encode(rawPassword); // Verify encoding happened
        verify(mockUserRepository, times(1)).save(testUser); // Verify save happened
    }
}