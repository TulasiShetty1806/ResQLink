package com.resqlink.config;

import com.resqlink.model.User;
import com.resqlink.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
            );
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        // Disable CSRF for H2 Console, WebSocket, and REST API testing
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/h2-console/**"),
                                new AntPathRequestMatcher("/ws/**"),
                                new AntPathRequestMatcher("/api/**")
                        )
                )
                .headers(headers -> headers
                        // Required for H2 Console in browser frames
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .authorizeHttpRequests(auth -> auth
                        // Public assets & landing & REST APIs
                        .requestMatchers(
                                "/", "/index", "/login", "/register",
                                "/tracking/**", "/track/**",
                                "/css/**", "/js/**", "/images/**", "/webjars/**",
                                "/h2-console/**",
                                "/ws/**",
                                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html",
                                "/api/**"
                        ).permitAll()

                        // Role-specific endpoints
                        .requestMatchers("/incidents/create", "/incidents/*/status").hasRole("COORDINATOR")
                        .requestMatchers("/allocations/**").hasRole("COORDINATOR")
                        .requestMatchers("/transport/dispatch/**").hasAnyRole("COORDINATOR", "LOGISTICS")
                        .requestMatchers("/resources/register", "/resources/restock").hasAnyRole("COORDINATOR", "ORGANIZATION")
                        .requestMatchers("/dashboard/**").authenticated()

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout=true")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .permitAll()
                );

        return http.build();
    }
}
