package com.example.tourismroullete.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        logger.info("Authentication succesful for user: {}", authentication.getName());
        logger.info("Authentication type: {}", authentication.getClass().getName());
        logger.info("User has the following authorities: {}", authentication.getAuthorities());

        // Log additional details for OAuth2 authentication
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2User oauth2User = ((OAuth2AuthenticationToken) authentication).getPrincipal();
            logger.info("OAuth2 user attributes: {}", oauth2User.getAttributes());
        }

        if (roles.contains("ROLE_ADMIN")) {
            logger.info("Redirecting admin user to dashboard");
            response.sendRedirect("/dashboard");
        } else if (roles.contains("ROLE_USER")) {
            logger.info("Redirecting regular user to user panel");
            response.sendRedirect("/user-panel");
        } else {
            logger.warn("User has no recognized roles, redirecting to access-denied");
            logger.warn("Available roles: {}", roles);
            response.sendRedirect("/access-denied");
        }
    }
}
