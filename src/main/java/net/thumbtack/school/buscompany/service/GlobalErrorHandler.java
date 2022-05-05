package net.thumbtack.school.buscompany.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.thumbtack.school.buscompany.dto.response.error.ErrorDtoResponse;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.exception.ServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;
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

    @ExceptionHandler(ServerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDtoResponse handleBadClientRequests(ServerException ex){
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ex.getErrorCode().toString(), ex.getField(), ex.getMessage()));
        return new ErrorDtoResponse(errors);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public ErrorDtoResponse handleMethodNotAllowedRequests(HttpRequestMethodNotSupportedException ex) {
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.METHOD_NOT_ALLOWED.toString(), ex.getMethod(), ErrorCode.METHOD_NOT_ALLOWED.getMessage()));
        return new ErrorDtoResponse(errors);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDtoResponse handleNotFoundRequests(NoHandlerFoundException ex) {
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.NOT_FOUND.toString(), ex.getRequestURL(), ErrorCode.NOT_FOUND.getMessage()));

        return new ErrorDtoResponse(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDtoResponse handleMessageNotReadableRequests(HttpMessageNotReadableException ex) {
        List<Error> errors = new ArrayList<>();
        errors.add(new Error(ErrorCode.WRONG_JSON_FORMAT.toString(), ErrorCode.WRONG_JSON_FORMAT.getField(), ErrorCode.WRONG_JSON_FORMAT.getMessage()));
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
