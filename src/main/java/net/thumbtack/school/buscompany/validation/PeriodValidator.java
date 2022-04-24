package net.thumbtack.school.buscompany.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class PeriodValidator extends BaseValidator implements ConstraintValidator<Period, String> {
    private final String daysOfWeek = "SunMonTueWenThuFriSat";

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s.equals("daily") || s.equals("odd") || s.equals("even")){
            return true;
        }

        String[] days = s.split(",");

        if(Arrays.stream(days).allMatch(str -> Integer.parseInt(str) >= 1 && Integer.parseInt(str) <= 31)){
            return true;
        }

        if(Arrays.stream(days).allMatch(daysOfWeek::contains)){
            return true;
        }

        return false;
    }
}
