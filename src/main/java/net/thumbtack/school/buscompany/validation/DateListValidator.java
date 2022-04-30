package net.thumbtack.school.buscompany.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DateListValidator extends BaseValidator implements ConstraintValidator<Date, List<String>> {
    private String style;

    @Override
    public void initialize(Date constraintAnnotation) {
        this.style = constraintAnnotation.style();
    }

    @Override
    public boolean isValid(List<String> strings, ConstraintValidatorContext constraintValidatorContext) {
        if(strings == null){
            return true;
        }

        try {
            for(String s : strings) {
                LocalDate.parse(s, DateTimeFormatter.ofPattern(style));
            }
        }
        catch (DateTimeParseException ex){
            return false;
        }
        return true;
    }
}
