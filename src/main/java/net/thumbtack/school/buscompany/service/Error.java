package net.thumbtack.school.buscompany.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Error {
    private String errorCode;
    private String field;
    private String message;
}
