package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;

public abstract class BaseController {
    @Value("${buscompany.user_idle_timeout}")
    protected long cookieMaxAge;

    protected ResponseCookie createJavaSessionIdCookie(String cookieValue){
        return ResponseCookie.from(BusCompanyCookies.JAVASESSIONID, cookieValue).maxAge(cookieMaxAge).build();
    }

    protected ResponseCookie deleteJavaSessionCookie(String cookieValue){
        return ResponseCookie.from(BusCompanyCookies.JAVASESSIONID, cookieValue).maxAge(0L).build();
    }
}
