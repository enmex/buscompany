package net.thumbtack.school.buscompany.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(
        validatedBy = ScheduleValidator.class
)
public @interface Schedule {
    String message() default "Неверный формат записи расписания";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
