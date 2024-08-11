package com.hh.TaskManagementSystems.config;

import com.hh.TaskManagementSystems.service.JwtService;
import com.hh.TaskManagementSystems.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр аутентификации JWT, который обрабатывает запросы для проверки JWT токенов.
 *
 * <p>Этот фильтр проверяет наличие JWT токена в заголовке `Authorization` каждого запроса.
 * Если токен присутствует и действителен, фильтр аутентифицирует пользователя и устанавливает
 * контекст безопасности для текущего запроса.</p>
 *
 * <p>Фильтр выполняет следующие действия:
 * <ul>
 *   <li>Извлекает JWT токен из заголовка `Authorization`.</li>
 *   <li>Извлекает email из JWT токена и проверяет его действительность.</li>
 *   <li>Если токен действителен, загружает детали пользователя и устанавливает аутентификацию
 *       в контексте безопасности.</li>
 * </ul>
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String HEADER_NAME = "Authorization";
    private final JwtService jwtService;
    private final UserService userService;

    /**
     * Фильтрует запросы для проверки JWT токенов и установки аутентификации.
     *
     * <p>Этот метод извлекает токен из заголовка `Authorization`, проверяет его действительность и
     * при необходимости устанавливает аутентификацию в {@link SecurityContext}. Затем передает
     * запрос следующему фильтру в цепочке.</p>
     *
     * @param request     HTTP-запрос, который нужно обработать
     * @param response    HTTP-ответ
     * @param filterChain цепочка фильтров для обработки запроса
     * @throws ServletException если возникает ошибка при обработке запроса
     * @throws IOException      если возникает ошибка при чтении или записи данных
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(BEARER_PREFIX.length());
        String email = jwtService.extractEmail(jwt);

        if (StringUtils.isNotEmpty(email) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService
                    .userDetailsService()
                    .loadUserByUsername(email);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }
}
