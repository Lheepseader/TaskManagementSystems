package com.hh.TaskManagementSystems.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для проверки, что значение поля или параметра соответствует одному из значений перечисления.
 *
 * <p>Эта аннотация используется для валидации строковых значений с использованием перечислений.
 * Аннотация применяется к полям, методам и параметрам, и требует, чтобы значение соответствовало одному из значений
 * перечисления, указанного в {@code enumClass}.</p>
 *
 * <p>Для выполнения валидации используется {@link EnumValidator}.</p>
 *
 * @see EnumValidator
 */
@Constraint(validatedBy = EnumValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValid {

    /**
     * Сообщение, которое будет отображаться при ошибке валидации.
     *
     * @return строка с сообщением об ошибке
     */
    String message() default "Недопустимое значение. Это не разрешено.";

    /**
     * Группы валидации, к которым принадлежит эта аннотация.
     *
     * @return массив групп валидации
     */
    Class<?>[] groups() default {};

    /**
     * Полезная нагрузка (payload), которую можно ассоциировать с аннотацией.
     * Это может быть использовано для предоставления дополнительных данных при валидации.
     *
     * @return массив полезной нагрузки
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Класс перечисления, значения которого должны быть допустимыми.
     *
     * @return класс перечисления, который используется для валидации значения
     */
    Class<? extends Enum<?>> enumClass();
}