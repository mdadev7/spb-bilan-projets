package com.bilanprojets.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                // Autorise toutes les requêtes directes vers l'API REST et console H2
                .ignoringRequestMatchers(
                        AntPathRequestMatcher.antMatcher("/api/**"),
                        AntPathRequestMatcher.antMatcher("/h2-console/**")
                )
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())
            )
            .authorizeHttpRequests(auth -> auth
                // Ressources statiques publiques
                .requestMatchers(
                        AntPathRequestMatcher.antMatcher("/css/**"),
                        AntPathRequestMatcher.antMatcher("/js/**"),
                        AntPathRequestMatcher.antMatcher("/images/**"),
                        AntPathRequestMatcher.antMatcher("/webjars/**"),
                        AntPathRequestMatcher.antMatcher("/favicon.ico")
                ).permitAll()
                // Documentation Swagger / OpenAPI & console H2
                .requestMatchers(
                        AntPathRequestMatcher.antMatcher("/swagger-ui/**"),
                        AntPathRequestMatcher.antMatcher("/swagger-ui.html"),
                        AntPathRequestMatcher.antMatcher("/v3/api-docs/**"),
                        AntPathRequestMatcher.antMatcher("/h2-console/**")
                ).permitAll()
                // Page de connexion & erreurs
                .requestMatchers(
                        AntPathRequestMatcher.antMatcher("/login"),
                        AntPathRequestMatcher.antMatcher("/error")
                ).permitAll()
                // API REST : accessible à toutes les requêtes
                .requestMatchers(
                        AntPathRequestMatcher.antMatcher("/api/**")
                ).permitAll()
                // Administration des utilisateurs et création de projets : ADMIN uniquement
                .requestMatchers(
                        AntPathRequestMatcher.antMatcher("/utilisateurs/**"),
                        AntPathRequestMatcher.antMatcher("/projets/nouveau"),
                        AntPathRequestMatcher.antMatcher("/projets/creer"),
                        AntPathRequestMatcher.antMatcher("/projets/*/affecter"),
                        AntPathRequestMatcher.antMatcher("/projets/*/supprimer")
                ).hasAuthority("ROLE_ADMIN")
                // Tout le reste requiert une authentification
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .userDetailsService(userDetailsService);

        return http.build();
    }
}
