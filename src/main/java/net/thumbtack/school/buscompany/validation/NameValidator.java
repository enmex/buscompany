package net.thumbtack.school.buscompany.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class NameValidator extends BaseValidator implements ConstraintValidator<Name, String> {

    @Value("${buscompany.max_name_length}")
    private int maxNameLength;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s == null){
            return true;
        }

        if(s.equals("")){
            customMessageForValidation(constraintValidatorContext, "Имя не может быть пустым");
            return false;
        }

        if(s.matches("^[^А-Яа-я]*$")){
            customMessageForValidation(constraintValidatorContext, "Имя не должно содержать латинские буквы и прочие символы");
            return false;
        }

        if(s.length() > maxNameLength){
            customMessageForValidation(constraintValidatorContext, "Имя не должно превышать " + maxNameLength + " символов");
            return false;
        }

        return true;
    }

}
