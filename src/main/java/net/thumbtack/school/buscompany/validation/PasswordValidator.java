package net.thumbtack.school.buscompany.validation;

import com.mysql.cj.jdbc.util.BaseBugReport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class PasswordValidator extends BaseValidator implements ConstraintValidator<Password, String> {

    @Value("${buscompany.min_password_length}")
    private int minPasswordLength;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        constraintValidatorContext.disableDefaultConstraintViolation();

        if(s.length() < minPasswordLength){
            customMessageForValidation(constraintValidatorContext, "Длина пароля должна превышать " + minPasswordLength + " символов");
            return false;
        }
        return true;
    }
}
