package net.thumbtack.school.buscompany.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateValidator extends BaseValidator implements ConstraintValidator<Date, String>{
    private String style;

    @Override
    public void initialize(Date constraintAnnotation) {
        this.style = constraintAnnotation.style();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        SimpleDateFormat format = new SimpleDateFormat(style);
        try {
            format.parse(s);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
