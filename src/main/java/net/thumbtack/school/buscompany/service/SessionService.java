package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.dao.UserDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SessionService {
    @Value("${buscompany.user_idle_timeout}")
    private int userIdleTimeout;

    private final UserDao userDao;

    public SessionService(UserDao userDao){
        this.userDao = userDao;
    }

    public void clearSessions() {
        userDao.clearSessions(userIdleTimeout);
    }

}
