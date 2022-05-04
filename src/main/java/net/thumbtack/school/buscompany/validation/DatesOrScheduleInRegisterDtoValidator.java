package net.thumbtack.school.buscompany.validation;

import net.thumbtack.school.buscompany.dto.request.admin.trip.RegisterTripDtoRequest;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class DatesOrScheduleInRegisterDtoValidator extends BaseValidator implements ConstraintValidator<DatesOrSchedule, RegisterTripDtoRequest> {

    @Override
    public boolean isValid(RegisterTripDtoRequest value, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        if(value.getSchedule() == null && value.getDates() == null){
            customMessageForValidation(context, "Расписание или список дат отсутствуют");
            return false;
        }
        if(value.getSchedule() != null && value.getDates() != null) {
            customMessageForValidation(context, "Расписание и даты не могут быть включены в запрос одновременно");
            return false;
        }
        return true;
    }
}
