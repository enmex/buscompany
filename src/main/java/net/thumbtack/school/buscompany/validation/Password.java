package net.thumbtack.school.buscompany.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(
        validatedBy = PasswordValidator.class
)
public @interface Password {
    String message() default "Неверный формат записи пароля";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
