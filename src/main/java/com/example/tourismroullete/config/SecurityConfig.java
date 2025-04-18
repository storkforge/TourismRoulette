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
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**", "/graphql", "/webauthn/**"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/locations/**").permitAll()
                        // Static resources
                        .requestMatchers("/", "/events/**", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        // Categories and activities
                        .requestMatchers("/categories", "/categories/view/**").permitAll()
                        .requestMatchers("/activities", "/activities/category/**", "/activities/view/**").permitAll()
                        .requestMatchers("/api/categories/**", "/api/activities/**").permitAll()
                        .requestMatchers("/access-denied").permitAll()
                        // WebAuthn endpoints
                        .requestMatchers("/webauthn/register").authenticated()
                        .requestMatchers("/webauthn/register/options").authenticated()
                        .requestMatchers("/webauthn/login").permitAll()
                        .requestMatchers("/webauthn/login/options").permitAll()
                        .requestMatchers("/webauthn/**", "/login/webauthn", "/js/webauthn.js", "/default-ui.css").permitAll()
                        // Admin routes
                        .requestMatchers("/dashboard").hasRole("ADMIN")
                        .requestMatchers("/categories/new", "/categories/edit/**", "/categories/delete/**").hasRole("ADMIN")
                        .requestMatchers("/categories/manage").hasRole("ADMIN")
                        .requestMatchers("/activities/new", "/activities/edit/**", "/activities/delete/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/dashboard")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                        .successHandler(successHandler)
                )
                .webAuthn(webAuthn -> webAuthn
                        .rpName("Tourism Roulette")
                        .rpId("localhost")
                        .allowedOrigins("http://localhost:8080")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
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