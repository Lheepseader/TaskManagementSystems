package com.hh.TaskManagementSystems.config;

import com.hh.TaskManagementSystems.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * Конфигурация безопасности для приложения Spring Boot.
 *
 * <p>Этот класс настраивает безопасность веб-приложения, включая управление аутентификацией, авторизацией, CORS и настройками JWT.</p>
 *
 * <p>Конфигурация включает:
 * <ul>
 *   <li>Отключение CSRF защиты.</li>
 *   <li>Настройку CORS для разрешения запросов с любого источника.</li>
 *   <li>Настройку авторизации для различных URL-шаблонов и ролей.</li>
 *   <li>Настройку аутентификации с использованием JWT и BCrypt.</li>
 *   <li>Настройку фильтра JWT для проверки токенов.</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;

    /**
     * Создает {@link SecurityFilterChain} для настройки безопасности HTTP-запросов.
     *
     * <p>Этот метод настраивает параметры безопасности, такие как разрешение CORS, доступ к различным URL-адресам,
     * и добавляет фильтр JWT для аутентификации запросов.</p>
     *
     * @param http объект {@link HttpSecurity} для настройки безопасности
     * @return настроенный {@link SecurityFilterChain}
     * @throws Exception если возникает ошибка при конфигурации безопасности
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-resources/*", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Создает {@link PasswordEncoder} для хеширования паролей.
     *
     * <p>Этот метод настраивает {@link PasswordEncoder} для использования алгоритма BCrypt.</p>
     *
     * @return {@link PasswordEncoder} для хеширования паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Создает {@link AuthenticationProvider} для аутентификации пользователей.
     *
     * <p>Этот метод настраивает {@link DaoAuthenticationProvider} с {@link UserDetailsService} и {@link PasswordEncoder}.</p>
     *
     * @return {@link AuthenticationProvider} для аутентификации
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService.userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Создает {@link AuthenticationManager} для управления аутентификацией.
     *
     * <p>Этот метод создает {@link AuthenticationManager} на основе конфигурации аутентификации.</p>
     *
     * @param config объект {@link AuthenticationConfiguration} для получения менеджера аутентификации
     * @return {@link AuthenticationManager} для управления аутентификацией
     * @throws Exception если возникает ошибка при создании менеджера аутентификации
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}