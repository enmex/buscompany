package net.thumbtack.school.buscompany.dao;

import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.model.User;
import org.springframework.stereotype.Component;

@Component
public interface UserDao {
    String register(User user) throws BusCompanyException;
    void unregister(User user) throws BusCompanyException;
    User getByLogin(String login) throws BusCompanyException;
    User getBySession(String uuid) throws BusCompanyException;
    String openSession(User user) throws BusCompanyException;
    void closeSession(String uuid) throws BusCompanyException;
    boolean isOnline(User user) throws BusCompanyException;
    String getUserType(User user) throws BusCompanyException;
    void updateUser(User user) throws BusCompanyException;
}
