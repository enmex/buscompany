package net.thumbtack.school.buscompany.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IdValidator extends BaseValidator implements ConstraintValidator<Id, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        try{
            int value = Integer.parseInt(s);
            return value > 0;
        }
        catch (NumberFormatException ex){
            return false;
        }
    }
}
