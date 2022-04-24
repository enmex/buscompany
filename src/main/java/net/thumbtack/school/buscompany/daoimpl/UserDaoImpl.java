package net.thumbtack.school.buscompany.daoimpl;

import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.model.Admin;
import net.thumbtack.school.buscompany.model.Client;
import net.thumbtack.school.buscompany.model.User;
import org.apache.ibatis.session.SqlSession;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.UUID;

public class UserDaoImpl extends BaseDaoImpl implements UserDao {
    @Override
    public String register(User user) throws BusCompanyException {
        try(SqlSession session = getSession()){
            String uuid;
            try{
                getUserMapper(session).insert(user);

                uuid = UUID.randomUUID().toString();
                getUserMapper(session).openSession(user, uuid);

                if(user instanceof Admin){
                    Admin admin = (Admin) user;
                    getAdminMapper(session).insert(admin);
                    getAdminMapper(session).updateUserType(admin);
                }
                else{
                    Client client = (Client) user;
                    getClientMapper(session).insert(client);
                    getClientMapper(session).updateUserType(client);
                }
            }
            catch(RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    throw new BusCompanyException(ErrorCode.USER_ALREADY_EXISTS);
                }
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
            return uuid;
        }
    }

    @Override
    public void unregister(User user) throws BusCompanyException {
        try(SqlSession session = getSession()){
            try{
                getUserMapper(session).deleteUser(user);
            }
            catch(RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    throw new BusCompanyException(ErrorCode.USER_NOT_EXISTS);
                }
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public User getByLogin(String login) throws BusCompanyException {
        User user;
        try(SqlSession session = getSession()){
            try{
                user = session.selectOne(path + "AdminMybatisMapper.getByLogin", login);
                if(user == null) {
                    user = session.selectOne(path + "ClientMybatisMapper.getByLogin", login);
                    if(user == null){
                        throw new RuntimeException("Cannot find user");
                    }
                }

            }
            catch(RuntimeException ex){
                session.rollback();
                if(ex.getMessage().contains("Cannot find user")){
                    throw new BusCompanyException(ErrorCode.INVALID_LOGIN_OR_PASSWORD);
                }
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return user;
    }

    @Override
    public User getBySession(String uuid) throws BusCompanyException {
        User user;
        try(SqlSession session = getSession()){
            try{
                user = session.selectOne(path + "AdminMybatisMapper.getBySession", uuid);
                if(user == null) {
                    user = session.selectOne(path + "ClientMybatisMapper.getBySession", uuid);
                    if(user == null){
                        throw new RuntimeException("Cannot find user");
                    }
                }
            }
            catch(RuntimeException ex){
                session.rollback();
                if(ex.getMessage().contains("Cannot find user")){
                    throw new BusCompanyException(ErrorCode.USER_IS_OFFLINE);
                }
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return user;
    }

    @Override
    public boolean isOnline(User user) throws BusCompanyException {
        boolean isOnline;
        try(SqlSession session = getSession()){
            try{
                if(getUserMapper(session).findUserInSession(user) == null){
                    return false;
                }
                isOnline = getUserMapper(session).findUserInSession(user).equals(user.getId());
            }
            catch(RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return isOnline;
    }

    @Override
    public String openSession(User user) throws BusCompanyException {
        String uuid;
        try(SqlSession session = getSession()){
            try{
                uuid = UUID.randomUUID().toString();
                getUserMapper(session).openSession(user, uuid);
            }
            catch(RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    throw new BusCompanyException(ErrorCode.UNABLE_TO_OPEN_SESSION);
                }
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return uuid;
    }

    @Override
    public void closeSession(String uuid) throws BusCompanyException {
        try(SqlSession session = getSession()){
            try{
                getUserMapper(session).closeSession(uuid);
            }
            catch(RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    throw new BusCompanyException(ErrorCode.UNABLE_TO_CLOSE_SESSION);
                }
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public String getUserType(User user) throws BusCompanyException {
        String userType;
        try(SqlSession session = getSession()){
            try{
                userType = getUserMapper(session).getUserType(user);
            }
            catch(RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    throw new BusCompanyException(ErrorCode.USER_NOT_EXISTS);
                }
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return userType;
    }

    @Override
    public void updateUser(User user) throws BusCompanyException {
        try(SqlSession session = getSession()){
            try{
                getUserMapper(session).updateUser(user);

                if(user instanceof Admin){
                    Admin admin = (Admin) user;
                    getAdminMapper(session).updateAdmin(admin);
                }
                else{
                    Client client = (Client) user;
                    getClientMapper(session).updateClient(client);
                }
            }
            catch(RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    throw new BusCompanyException(ErrorCode.USER_NOT_EXISTS);
                }
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }
}
