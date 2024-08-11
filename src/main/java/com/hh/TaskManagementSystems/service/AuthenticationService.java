package com.hh.TaskManagementSystems.service;

import com.hh.TaskManagementSystems.dto.AuthRequestDto;
import com.hh.TaskManagementSystems.dto.AuthResponseDto;
import com.hh.TaskManagementSystems.dto.RegistrationRequestDto;
import com.hh.TaskManagementSystems.model.Role;
import com.hh.TaskManagementSystems.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Сервис для обработки аутентификации и авторизации пользователей.
 *
 * <p>Этот сервис управляет процессами регистрации пользователей и их авторизации,
 * а также генерирует JWT токены для аутентифицированных пользователей.</p>
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Регистрирует нового пользователя и возвращает JWT токен.
     *
     * <p>Создает нового пользователя на основе предоставленных данных, шифрует его пароль,
     * сохраняет пользователя в базе данных и возвращает JWT токен для авторизованного доступа.</p>
     *
     * @param request данные регистрации пользователя
     * @return объект {@link AuthResponseDto} с JWT токеном
     */
    public AuthResponseDto registration(RegistrationRequestDto request) {
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build();

        user = userService.createUser(user);

        String jwt = jwtService.generateToken(user);
        return new AuthResponseDto(jwt);
    }

    /**
     * Выполняет авторизацию пользователя и возвращает JWT токен.
     *
     * <p>Аутентифицирует пользователя на основе предоставленных данных и возвращает JWT токен,
     * если аутентификация успешна.</p>
     *
     * @param request данные для авторизации пользователя
     * @return объект {@link AuthResponseDto} с JWT токеном
     */
    public AuthResponseDto authorization(AuthRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.email(),
                request.password()
        ));

        User user = (User) userService
                .userDetailsService()
                .loadUserByUsername(request.email());

        String jwt = jwtService.generateToken(user);
        return new AuthResponseDto(jwt);
    }
}
