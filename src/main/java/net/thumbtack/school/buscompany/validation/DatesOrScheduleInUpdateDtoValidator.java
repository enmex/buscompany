package net.thumbtack.school.buscompany.validation;

import net.thumbtack.school.buscompany.dto.request.admin.trip.UpdateTripDtoRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DatesOrScheduleInUpdateDtoValidator extends BaseValidator implements ConstraintValidator<DatesOrSchedule, UpdateTripDtoRequest> {

    @Override
    public boolean isValid(UpdateTripDtoRequest value, ConstraintValidatorContext context) {
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
