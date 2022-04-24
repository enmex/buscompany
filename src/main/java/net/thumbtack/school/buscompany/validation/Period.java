package net.thumbtack.school.buscompany.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(
        validatedBy = PeriodValidator.class
)
public @interface Period {
    String message() default "Неверный формат указанного периода";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
