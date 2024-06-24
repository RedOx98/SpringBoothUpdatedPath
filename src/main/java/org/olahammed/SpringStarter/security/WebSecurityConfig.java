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
        "src/main/resources/static/**",
        "/webjars/**",
    };

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    String ADMIN = Roles.ADMIN.getRole();
    String EDITOR = Roles.EDITOR.getRole();

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests -> 
                authorizeRequests
                    .requestMatchers(WHITELIST).permitAll()
                    .requestMatchers("/profile/**").authenticated()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    //hasAnyAuthority(Priviledges.SERVE_AS_ADMIN.getPriviledge(), Priviledges.SERVE_AS_EDITOR.getPriviledge(),Priviledges.SERVE_AS_SUPER_USER.getPriviledge())
                    .requestMatchers("/editor/**").hasAnyRole("ADMIN","EDITOR")
                    .requestMatchers("/test").hasAuthority(Priviledges.SERVE_AS_SUPER_USER.getPriviledge())
                    .requestMatchers("/post/**").hasAnyRole("USER","ADMIN")
                    .requestMatchers("/posts/**").authenticated()
                    //.anyRequest().authenticated()
            )
            .formLogin(form -> 
                form
                    .loginPage("/login")
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
            .httpBasic();

        // TODO: remove these after upgrading from H2 infile DB
        http.csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions().disable());

        return http.build();
    }
}
