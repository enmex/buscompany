package net.thumbtack.school.buscompany.controller;

import org.springframework.http.ResponseCookie;

public abstract class BaseController {
    protected ResponseCookie createJavaSessionIdCookie(String cookieValue){
        return ResponseCookie.from("JAVASESSIONID", cookieValue).build();
    }
}
