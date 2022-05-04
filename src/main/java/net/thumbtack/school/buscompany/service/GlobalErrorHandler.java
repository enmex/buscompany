package net.thumbtack.school.buscompany.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.thumbtack.school.buscompany.dto.response.error.ErrorDtoResponse;
import net.thumbtack.school.buscompany.exception.CheckedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.List;

@EnableWebMvc
@RestControllerAdvice
public class GlobalErrorHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalErrorHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDtoResponse handleValidation(MethodArgumentNotValidException ex){
        List<Error> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().stream().forEach(
                error -> errors.add(new Error(error.getCode(), error.getObjectName(), error.getDefaultMessage()))
        );
        return new ErrorDtoResponse(errors);
    }

    @ExceptionHandler(CheckedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDtoResponse handleBadClientRequests(CheckedException ex){
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ex.getErrorCode().toString(), ex.getField(), ex.getMessage()));
        return new ErrorDtoResponse(errors);
    }

    @AllArgsConstructor
    @Getter
    public static class Error {
        private String errorCode;
        private String field;
        private String message;
    }
}
