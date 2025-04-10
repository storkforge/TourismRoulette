package com.example.tourismroullete.service;

import com.example.tourismroullete.entities.User;
import com.example.tourismroullete.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UserRepository userRepository;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.delegate = new org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService();
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Get the user from the default service
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Extract user information
        Map<String, Object> attributes = oAuth2User.getAttributes();
        logger.info("OAuth2 user attributes: {}", attributes);

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        if (email == null) {
            // Some providers might use different attribute names
            email = (String) attributes.get("sub") + "@oauth.provider";
        }

        if (name == null) {
            name = email.split("@")[0]; // Use part of email as name if not provided
        }

        logger.info("OAuth2 login attempt for email: {}", email);

        // Check if user already exists in our database
        User user = userRepository.findByUsername(email).orElse(null);

        if (user == null) {
            // Create a new user with ROLE_USER
            user = new User();
            user.setUsername(email);
            user.setEmail(email);

            // Split the name into first and last name
            String[] nameParts = name.split(" ", 2);
            user.setFirstName(nameParts[0]);
            if (nameParts.length > 1) {
                user.setLastName(nameParts[1]);
            } else {
                user.setLastName(""); // Empty last name if only one name part is available
            }

            // Generate a random password since OAuth users don't use password login
            user.setPassword(UUID.randomUUID().toString());
            user.setRole("ROLE_USER");
            user.setEnabled(true);

            userRepository.save(user);
            logger.info("Created new user from OAuth2 login: {}", email);
        } else {
            logger.info("Found existing user in database: {}", email);
        }

        // Create a collection of authorities
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        authorities.add(new OAuth2UserAuthority(attributes));

        // Return a custom OAuth2User with ROLE_USER authority
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        return new DefaultOAuth2User(authorities, attributes, userNameAttributeName);
    }
}
