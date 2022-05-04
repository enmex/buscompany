package net.thumbtack.school.buscompany.validation;

import net.thumbtack.school.buscompany.dto.request.admin.trip.ScheduleDtoRequest;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class ScheduleValidator extends BaseValidator implements ConstraintValidator<Schedule, ScheduleDtoRequest> {

    @Override
    public boolean isValid(ScheduleDtoRequest value, ConstraintValidatorContext context) {
        if(value == null){
            return true;
        }

        try {
            LocalDate from = LocalDate.parse(value.getFromDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate to = LocalDate.parse(value.getToDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            return to.isAfter(from);
        }
        catch (DateTimeParseException ex){
            return true;
        }
    }
}
