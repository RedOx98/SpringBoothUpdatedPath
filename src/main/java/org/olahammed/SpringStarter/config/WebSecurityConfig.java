package org.olahammed.SpringStarter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {
    private static final String[] WHITELIST = {
        "/",
        "/login",
        "/register",
        "/db-console/**",
        "/css/**",
        "/fonts/**",
        "/images/**",
        "/js/**",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests -> 
                authorizeRequests
                    .requestMatchers(WHITELIST).permitAll()
                    .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions().disable());

        return http.build();
    }
}