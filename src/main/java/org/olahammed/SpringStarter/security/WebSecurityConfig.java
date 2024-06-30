package org.olahammed.SpringStarter.security;

import org.olahammed.SpringStarter.util.constants.Priviledges;
import org.olahammed.SpringStarter.util.constants.Roles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// (prePostEnabled = true, securedEnabled = true) (from enableMethod decorator)
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    private static final String[] WHITELIST = {
        "/",
        "/login",
        "/register",
        "/db-console/**",
        "/resources/**",
        "/posts/**"
    };

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    String ADMIN = Roles.ADMIN.getRole();
    String EDITOR = Roles.EDITOR.getRole();

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .headers(
            headers -> 
            headers.frameOptions().sameOrigin()
        )
            .authorizeHttpRequests(authorizeRequests -> 
                authorizeRequests
                    .requestMatchers(WHITELIST).permitAll()
                    .requestMatchers("/profile/**").authenticated()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/editor/**").hasAnyRole("ADMIN", "EDITOR")
                    .requestMatchers("/test").hasAuthority(Priviledges.SERVE_AS_SUPER_USER.getPriviledge())
                    .requestMatchers("/post/**").hasAnyRole("USER", "ADMIN")
                    .requestMatchers("/logout/**").authenticated()
                    .requestMatchers("/updatephoto").hasAnyRole("USER", "ADMIN")
                    .anyRequest().authenticated() // Ensure this is uncommented to catch any other requests
            )
            .formLogin(form -> 
                form
                    .loginPage("/login") // Ensure this matches your Thymeleaf template
                    .loginProcessingUrl("/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login?error")
                    .permitAll()
            )
            .logout(logout -> 
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")
            )
            .rememberMe(t ->
            t.rememberMeParameter("remember-me")
             )
            .httpBasic();

        // TODO: remove these after upgrading from H2 infile DB
        http.csrf(csrf -> 
        csrf
        .ignoringRequestMatchers("/resources/static/**")
        
        // csrf.disable()
        
        )
            .headers(headers -> headers.frameOptions().disable());

        return http.build();
    }
}
