package com.hh.TaskManagementSystems.service;

import com.hh.TaskManagementSystems.exception.NotFoundException;
import com.hh.TaskManagementSystems.exception.WrongJwtException;
import com.hh.TaskManagementSystems.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Сервис для работы с JSON Web Token (JWT).
 *
 * <p>Этот сервис предоставляет методы для создания, проверки и извлечения информации из JWT токенов.</p>
 */
@Service
public class JwtService {
    @Value("${token.signing.key}")
    private String jwtSigningKey;

    /**
     * Извлекает email из JWT токена.
     *
     * @param token JWT токен
     * @return email, извлеченный из токена
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Генерирует новый JWT токен для указанного пользователя.
     *
     * <p>Создает токен, включая дополнительные данные пользователя, такие как идентификатор, email и роль.</p>
     *
     * @param userDetails детали пользователя
     * @return сгенерированный JWT токен
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("email", customUserDetails.getEmail());
            claims.put("role", customUserDetails.getRole());
        }
        return generateToken(claims, userDetails);
    }

    /**
     * Проверяет, действителен ли JWT токен для указанного пользователя.
     *
     * <p>Проверяет соответствие email в токене имени пользователя и истек ли токен.</p>
     *
     * @param token       JWT токен
     * @param userDetails детали пользователя
     * @return {@code true}, если токен действителен и не истек, иначе {@code false}
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Извлекает указанное требование из JWT токена.
     *
     * @param token           JWT токен
     * @param claimsResolvers функция для извлечения данных из требований токена
     * @param <T>             тип данных, который нужно извлечь
     * @return извлеченное требование
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    /**
     * Генерирует JWT токен с дополнительными данными и деталями пользователя.
     *
     * @param extraClaims дополнительные данные для включения в токен
     * @param userDetails детали пользователя
     * @return сгенерированный JWT токен
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder().claims(extraClaims).subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(getSigningKey(), Jwts.SIG.HS256).compact();
    }

    /**
     * Проверяет, истек ли JWT токен.
     *
     * @param token JWT токен
     * @return {@code true}, если токен истек, иначе {@code false}
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлекает дату истечения срока действия токена.
     *
     * @param token JWT токен
     * @return дата истечения токена
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлекает все требования из JWT токена.
     *
     * @param token JWT токен
     * @return все требования токена
     * @throws NotFoundException если Jwt токен неверный
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token)
                    .getPayload();

        } catch (Exception exception) {
            throw new WrongJwtException();
        }
    }

    /**
     * Получает ключ для подписывания JWT токенов.
     *
     * @return ключ для подписывания
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
