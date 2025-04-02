package com.example.tourismroullete.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.security.default-username:admin}")
    private String defaultUsername;

    @Value("${app.security.default-password:admin123}")
    private String defaultPassword;

    @Value("${app.security.default-role:ADMIN}")
    private String defaultRole;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity during development
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/locations/**").permitAll()
                        // Explicitly permit access to login page, registration page, and static resources
                        .requestMatchers("/login", "/login?error", "/login?logout", "/register", "/api/register").permitAll()
                        .requestMatchers("/", "/home", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        // Allow public access to categories and activities browsing
                        .requestMatchers("/categories", "/categories/view/**").permitAll()
                        .requestMatchers("/activities", "/activities/category/**", "/activities/view/**").permitAll()
                        .requestMatchers("/api/categories/**", "/api/activities/**").permitAll()
                        // Admin-only access to category and activity management
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
                        .loginProcessingUrl("/perform_login") // Specify a different URL for the login form POST
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
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
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        // Create and add the default user from the properties.
        UserDetails defaultUser = User.builder()
                .username(defaultUsername)
                .password(passwordEncoder.encode(defaultPassword))
                .roles(defaultRole)
                .build();
        manager.createUser(defaultUser);

        // Only add a second "admin" user if the default username is not "admin"
        if (!"admin".equalsIgnoreCase(defaultUsername)) {
            UserDetails adminUser = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .roles("ADMIN")
                    .build();
            manager.createUser(adminUser);
        }
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
