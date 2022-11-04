package com.sanderdsz.grocery.infrastructure.security;

import com.sanderdsz.grocery.infrastructure.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * The SecurityConfiguration class configures the routing of the API.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    /**
     * Injecting the service provider for the Users so our,
     * security can access via JPA the information.
     */
    private final UserService userService;

    public SecurityConfiguration(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeRequests(auth -> {
                    auth.antMatchers("/").permitAll();
                    auth.antMatchers("/users").authenticated();
                    auth.antMatchers("/users/register").permitAll();
                })
                .userDetailsService(userService)
                .headers(headers -> headers.frameOptions().sameOrigin())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    /**
     * We need to provide a method for encription to Spring Security.
     * @return encription password with BCrypt
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
