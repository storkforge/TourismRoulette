package com.example.tourismroullete.config;

import com.example.tourismroullete.service.CustomOAuth2UserService;
import com.example.tourismroullete.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomOAuth2UserService oAuth2UserService;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(
            @Lazy CustomUserDetailsService customUserDetailsService,
            CustomAuthenticationSuccessHandler successHandler,
            CustomOAuth2UserService oAuth2UserService
    ) {
        this.customUserDetailsService = customUserDetailsService;
        this.successHandler = successHandler;
        this.oAuth2UserService = oAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity during development
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/locations/**").permitAll()
                        // Explicitly permit access to login page, registration page, and static resources
                        .requestMatchers("/login", "/login?error", "/login?logout", "/register", "/api/register", "/oauth2/**").permitAll()
                        .requestMatchers("/", "/events/**", "/home", "/css/**", "/js/**", "/perform_login", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/", "/home", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        // Allow public access to categories and activities browsing
                        .requestMatchers("/categories", "/categories/view/**").permitAll()
                        .requestMatchers("/activities", "/activities/category/**", "/activities/view/**").permitAll()
                        .requestMatchers("/api/categories/**", "/api/activities/**").permitAll()
                        .requestMatchers("/access-denied").permitAll()
                        // Admin-only access to category and activity management
                        .requestMatchers("/dashboard").hasRole("ADMIN")
                        .requestMatchers("/categories/new", "/categories/edit/**", "/categories/delete/**").hasRole("ADMIN")
                        .requestMatchers("/categories/manage").hasRole("ADMIN")
                        .requestMatchers("/activities/new", "/activities/edit/**", "/activities/delete/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // All other requests need authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                        .successHandler(successHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/perform_logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
