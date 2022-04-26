package net.thumbtack.school.buscompany.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(
        validatedBy = IdValidator.class
)
public @interface Id {
    String message() default "Неверно указан ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
