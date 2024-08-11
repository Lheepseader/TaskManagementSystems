package com.hh.TaskManagementSystems.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

/**
 * Валидатор для проверки, что строковое значение соответствует одному из значений перечисления (enum).
 *
 * <p>Этот валидатор реализует интерфейс {@link ConstraintValidator} и используется вместе с аннотацией {@link EnumValid}.
 * Он проверяет, что значение строки является допустимым значением перечисления, указанного в аннотации {@link EnumValid}.</p>
 *
 * @param <T> тип перечисления, который используется для валидации
 */
public class EnumValidator implements ConstraintValidator<EnumValid, String> {
    /**
     * Класс перечисления, который используется для валидации.
     */
    private Class<? extends Enum<?>> enumClass;

    /**
     * Инициализация валидатора с использованием аннотации {@link EnumValid}.
     *
     * @param constraintAnnotation аннотация {@link EnumValid}, содержащая информацию о перечислении для валидации
     */
    @Override
    public void initialize(EnumValid constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    /**
     * Проверяет, что значение строки соответствует одному из значений перечисления.
     *
     * <p>Если значение равно {@code null}, то метод возвращает {@code true}. В противном случае, он проверяет,
     * присутствует ли значение строки в перечислении, указанном в {@code enumClass}.</p>
     *
     * @param value   значение строки, которое нужно проверить
     * @param context контекст валидации, который может быть использован для получения дополнительной информации
     * @return {@code true}, если значение соответствует одному из значений перечисления, иначе {@code false}
     */

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Enum<?>[] enumConstants = enumClass.getEnumConstants();
        return Arrays.stream(enumConstants)
                .anyMatch(e -> e.name().equals(value));
    }
}