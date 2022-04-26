package net.thumbtack.school.buscompany.validation;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class PhoneValidator extends BaseValidator implements ConstraintValidator<Phone, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null){
            return false;
        }

        String number = s.replaceAll("-", "");
        if(number.startsWith("+")){
            number = number.substring(1);
        }

        if(number.charAt(0) < '7' || number.charAt(0) > '8'){
            customMessageForValidation(constraintValidatorContext, "Номер телефона должен начинаться с цифры 7 или 8");
            return false;
        }

        if(number.matches("[^0-9]") && !number.startsWith("+")){
            customMessageForValidation(constraintValidatorContext, "Номер телефона содержит посторонние символы");
            return false;
        }

        if(number.length() != 11){
            customMessageForValidation(constraintValidatorContext, "Неверный формат записи номера телефона");
            return false;
        }

        return true;
    }
}
