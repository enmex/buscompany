package net.thumbtack.school.buscompany.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class DateListValidator extends BaseValidator implements ConstraintValidator<Date, List<String>> {
    private String style;

    @Override
    public void initialize(Date constraintAnnotation) {
        this.style = constraintAnnotation.style();
    }

    @Override
    public boolean isValid(List<String> strings, ConstraintValidatorContext constraintValidatorContext) {
        SimpleDateFormat format = new SimpleDateFormat(style);

        try {
            for(String s : strings) {
                format.parse(s);
            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
