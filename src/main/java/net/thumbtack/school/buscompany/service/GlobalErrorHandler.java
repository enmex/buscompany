package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.dto.response.error.ErrorDtoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalErrorHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDtoResponse handleValidation(MethodArgumentNotValidException ex){
        List<Error> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().stream().forEach(
                fieldError -> errors.add(new Error(fieldError.getCode(), fieldError.getField(), fieldError.getDefaultMessage()))
        );
        return new ErrorDtoResponse(errors);
    }
}
