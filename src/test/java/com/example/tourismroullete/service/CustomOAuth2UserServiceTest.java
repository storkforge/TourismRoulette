package com.example.tourismroullete.service;

import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;

    @Mock
    private OAuth2UserRequest userRequest;

    @Mock
    private ClientRegistration clientRegistration;

    @Mock
    private ClientRegistration.ProviderDetails providerDetails;

    @Mock
    private ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint;

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customOAuth2UserService = new CustomOAuth2UserService(userRepository);
        // Replace internal delegate with mocked one
        var delegateField = Arrays.stream(CustomOAuth2UserService.class.getDeclaredFields())
                .filter(f -> f.getName().equals("delegate"))
                .findFirst().orElseThrow();
        delegateField.setAccessible(true);
        try {
            delegateField.set(customOAuth2UserService, delegate);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testLoadUser_NewUserWithMissingAttributes_CreatesUserWithDefaults() {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "123456");  // Fallback for missing email
        // No "email" or "name" attributes

        OAuth2User mockOAuth2User = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "sub"
        );

        when(delegate.loadUser(any())).thenReturn(mockOAuth2User);
        when(userRepository.findByUsername("123456@oauth.provider")).thenReturn(Optional.empty());

        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
        when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
        when(userInfoEndpoint.getUserNameAttributeName()).thenReturn("sub");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("123456@oauth.provider", savedUser.getUsername());
        assertEquals("123456", savedUser.getFirstName());  // Extracted from email
        assertEquals("", savedUser.getLastName());         // Only one name part
        assertEquals("ROLE_USER", savedUser.getRole());
        assertTrue(savedUser.isEnabled());

        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUser_ExistingUser_LogsAndReturnsUser() {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "existing@example.com");
        attributes.put("name", "Jane Doe");

        OAuth2User mockOAuth2User = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email"
        );

        User existingUser = new User();
        existingUser.setUsername("existing@example.com");
        existingUser.setFirstName("Jane");
        existingUser.setLastName("Doe");

        when(delegate.loadUser(any())).thenReturn(mockOAuth2User);
        when(userRepository.findByUsername("existing@example.com")).thenReturn(Optional.of(existingUser));

        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
        when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
        when(userInfoEndpoint.getUserNameAttributeName()).thenReturn("email");

        // Act
        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        // Assert
        verify(userRepository, never()).save(any());
        assertEquals("existing@example.com", result.getAttribute("email"));
        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUser_NewUserWithFullName_ParsesFirstAndLastNameCorrectly() {
        // Arrange
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "fullname@example.com");
        attributes.put("name", "Alice Wonderland");

        OAuth2User mockOAuth2User = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                "email"
        );

        when(delegate.loadUser(any())).thenReturn(mockOAuth2User);
        when(userRepository.findByUsername("fullname@example.com")).thenReturn(Optional.empty());

        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
        when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
        when(userInfoEndpoint.getUserNameAttributeName()).thenReturn("email");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        // Assert
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("fullname@example.com", savedUser.getUsername());
        assertEquals("Alice", savedUser.getFirstName());
        assertEquals("Wonderland", savedUser.getLastName());
        assertEquals("ROLE_USER", savedUser.getRole());
        assertTrue(savedUser.isEnabled());

        assertTrue(result.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

}
