package net.thumbtack.school.buscompany.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValidator extends BaseValidator implements ConstraintValidator<Date, String>{
    private String style;

    @Override
    public void initialize(Date constraintAnnotation) {
        this.style = constraintAnnotation.style();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        try {
            if(style.equals("HH:mm")) {
                LocalTime.parse(s, DateTimeFormatter.ofPattern(style));
            }
            else{
                LocalDate.parse(s, DateTimeFormatter.ofPattern(style));
            }
        } catch (DateTimeParseException ex) {
            return false;
        }
        return true;
    }
}
