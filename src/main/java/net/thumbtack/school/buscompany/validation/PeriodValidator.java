package net.thumbtack.school.buscompany.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class PeriodValidator extends BaseValidator implements ConstraintValidator<Period, String> {
    private final String daysOfWeek = "SunMonTueWenThuFriSat";

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s.equals("daily") || s.equals("odd") || s.equals("even")){
            return true;
        }

        List<String> days = List.of(s.split(",\\s+"));

        if(days.stream().allMatch(daysOfWeek::contains)){
            return days.stream().distinct().count() >= days.size();
        }

        try {
            if (days.stream().allMatch(str -> Integer.parseInt(str) >= 1 && Integer.parseInt(str) <= 31)) {
                return days.stream().distinct().count() >= days.size();
            }
        }
        catch (NumberFormatException ex){
            return false;
        }

        return false;
    }
}
