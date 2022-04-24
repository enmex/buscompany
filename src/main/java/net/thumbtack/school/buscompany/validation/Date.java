package net.thumbtack.school.buscompany.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(
        validatedBy = {DateValidator.class, DateListValidator.class}
)
public @interface Date {
    String style() default "HH:MM";
    String message() default "Неверно указана дата";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
