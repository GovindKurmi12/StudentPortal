package com.gk.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.ObjectProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private ObjectProvider<CustomAuthenticationSuccessHandler> successHandlerProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Static resources
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()

                // Public pages
                .requestMatchers("/login", "/error", "/access-denied").permitAll()

                // Admin only pages
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/students/*/edit", "/students/*/delete").hasRole("ADMIN")
                .requestMatchers("/courses/new", "/courses/*/edit", "/courses/*/delete").hasRole("ADMIN")

                // Teacher and Admin pages
                .requestMatchers("/students/dashboard", "/students/list").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers("/students/marks/**").hasAnyRole("TEACHER", "ADMIN")
                .requestMatchers("/students/attendance/**").hasAnyRole("TEACHER", "ADMIN")

                // Finance related pages
                .requestMatchers("/students/fees/**").hasAnyRole("ADMIN", "ACCOUNTANT")

                // Parent pages
                .requestMatchers("/students/parent-dashboard/**").hasRole("PARENT")

                // Require authentication for everything else
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandlerProvider.getObject())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
