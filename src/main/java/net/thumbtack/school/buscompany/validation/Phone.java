package net.thumbtack.school.buscompany.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(
        validatedBy = PhoneValidator.class
)
public @interface Phone {
    String message() default "Неверный формат записи телефона";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
