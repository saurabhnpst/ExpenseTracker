package com.saurabh.ExpenseTracker.config;

import com.saurabh.ExpenseTracker.security.JwtAuthenticationFilter;
import com.saurabh.ExpenseTracker.security.OAuth2SuccessHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            OAuth2SuccessHandler oAuth2SuccessHandler
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .cors(cors -> {})

                // OAuth2 needs a session during the Google login handshake.
                // JWT is still used for API authentication.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Register + Login
                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        // OAuth2 endpoints
                        .requestMatchers(
                                "/oauth2/**",
                                "/login/oauth2/**"
                        )
                        .permitAll()

                        // GET APIs
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/categories",
                                "/api/categories/**",
                                "/api/expenses",
                                "/api/expenses/**"
                        )
                        .permitAll()

                        // POST APIs
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/categories/**",
                                "/api/expenses/**"
                        )
                        .authenticated()

                        // PUT APIs
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/categories/**",
                                "/api/expenses/**"
                        )
                        .authenticated()

                        // DELETE APIs
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/categories/**",
                                "/api/expenses/**"
                        )
                        .authenticated()

                        // Everything else requires authentication
                        .anyRequest()
                        .authenticated()
                )

                // JWT authentication filter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                // Disable normal form login
                .formLogin(form -> form.disable())

                // Disable Basic Authentication
                .httpBasic(basic -> basic.disable())

                // Google OAuth2 Login
                .oauth2Login(oauth2 ->
                        oauth2.successHandler(oAuth2SuccessHandler)
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of("*")
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}