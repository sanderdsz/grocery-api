package com.sanderdsz.security.infrastructure.security;

import com.sanderdsz.security.infrastructure.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionManagementFilter;

/**
 * The SecurityConfiguration class configures the routing of the API.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    /**
     * Injecting the service provider for the Users so our,
     * security can access via JPA the information.
     */
    private final UserService userService;

    public SecurityConfiguration(UserService userService) {
        this.userService = userService;
    }

    /**
     * We need to provide a method for encryption to Spring Security.
     * @return encryption password with BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessTokenFilter accessTokenFilter() {
        return new AccessTokenFilter();
    }

    @Bean
    CorsFilter corsFilter() {
        CorsFilter filter = new CorsFilter();
        return filter;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .addFilterBefore(corsFilter(), SessionManagementFilter.class)
                .authorizeRequests(auth -> {
                    auth.antMatchers("/auth/signup").permitAll();
                    auth.antMatchers("/auth/login").permitAll();
                    auth.antMatchers("/auth/logout").permitAll();
                    auth.antMatchers("/auth/alive").authenticated();
                    auth.antMatchers("/recovery").permitAll();
                    auth.antMatchers("/users/me").authenticated();
                })
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilterAfter(accessTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
