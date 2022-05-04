package net.thumbtack.school.buscompany.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DatesOrScheduleInRegisterDtoValidator.class, DatesOrScheduleInUpdateDtoValidator.class})
public @interface DatesOrSchedule {
    String message() default "Ошибка в DTO классе";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
