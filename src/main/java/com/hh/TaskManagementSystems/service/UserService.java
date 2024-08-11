package com.hh.TaskManagementSystems.service;

import com.hh.TaskManagementSystems.exception.NotFoundException;
import com.hh.TaskManagementSystems.model.User;
import com.hh.TaskManagementSystems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Сервис для управления пользователями.
 *
 * <p>Этот сервис предоставляет методы для создания, удаления и проверки существования пользователей,
 * а также для получения текущего пользователя из контекста безопасности.</p>
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Создает нового пользователя.
     *
     * <p>Проверяет, существует ли уже пользователь с указанным email. Если да, то выбрасывается исключение.
     * В противном случае, пользователь сохраняется в базе данных.</p>
     *
     * @param user объект пользователя, который нужно создать
     * @return созданный пользователь
     * @throws RuntimeException если пользователь с таким email уже существует
     */
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        return userRepository.save(user);
    }

    /**
     * Удаляет пользователя по email.
     *
     * <p>Проверяет, существует ли пользователь с указанным email. Если существует, удаляет его.
     * В противном случае выбрасывается исключение.</p>
     *
     * @param id    идентификатор пользователя (не используется в текущей реализации)
     * @param email email пользователя, которого нужно удалить
     * @throws NotFoundException если пользователь с таким email не найден
     */
    public void deleteUser(String email) {
        if (userRepository.existsByEmail(email)) {
            userRepository.deleteByEmail(email);
        } else {
            throw new NotFoundException("Email");
        }
    }

    /**
     * Проверяет существование пользователя по email.
     *
     * @param email email пользователя
     * @return {@code true}, если пользователь с таким email существует, иначе {@code false}
     */
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Возвращает {@link UserDetailsService}, который использует метод {@link #getByEmail(String)}.
     *
     * @return {@link UserDetailsService}, который позволяет получать пользователя по email
     */
    public UserDetailsService userDetailsService() {
        return this::getByEmail;
    }

    /**
     * Получает пользователя по email.
     *
     * <p>Ищет пользователя в базе данных по указанному email. Если пользователь не найден, выбрасывается исключение.</p>
     *
     * @param email email пользователя
     * @return пользователь с указанным email
     * @throws NotFoundException если пользователь с таким email не найден
     */
    private User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Пользователь"));
    }

    /**
     * Получает текущего аутентифицированного пользователя.
     *
     * <p>Получает email текущего пользователя из контекста безопасности и возвращает соответствующего пользователя из базы данных.</p>
     *
     * @return текущий аутентифицированный пользователь
     * @throws NotFoundException если текущий пользователь не найден
     */
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByEmail(email);
    }
}
