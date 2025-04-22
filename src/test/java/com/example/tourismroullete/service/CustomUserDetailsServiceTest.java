package com.example.tourismroullete.service;

import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private PasswordEncoder mockPasswordEncoder;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;
    private final String testUsername = "testuser";
    private final String testPasswordHash = "$2a$10$encodedPasswordExample";
    private final String testRole = "ROLE_USER";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername(testUsername);
        testUser.setPassword(testPasswordHash);
        testUser.setRole(testRole);
        testUser.setEnabled(true);
    }

    @Test
    @DisplayName("Should return UserDetails when user is found")
    void loadUserByUsername_whenUserFound_shouldReturnUserDetails() {
        // Arrange
        when(mockUserRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        // Act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(testUsername);

        // Assert
        assertNotNull(userDetails);
        assertEquals(testUsername, userDetails.getUsername());
        assertEquals(testPasswordHash, userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertNotNull(userDetails.getAuthorities());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority(testRole)));
        verify(mockUserRepository, times(1)).findByUsername(testUsername);
        verifyNoInteractions(mockPasswordEncoder);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user is not found")
    void loadUserByUsername_whenUserNotFound_shouldThrowException() {
        // Arrange
        when(mockUserRepository.findByUsername(testUsername)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(testUsername)
        );

        assertTrue(exception.getMessage().contains(testUsername));
        verify(mockUserRepository, times(1)).findByUsername(testUsername);
        verifyNoInteractions(mockPasswordEncoder);
    }
}
