package net.thumbtack.school.buscompany.daoimpl;

import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.model.*;
import org.apache.ibatis.session.SqlSession;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;

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

    @Override
    public List<Trip> getTripsFromStation(String fromStation) {
        List<Trip> trips;
        try(SqlSession session = getSession()){
            try{
                trips = new ArrayList<>(session.selectList(path + "TripMybatisMapper.getScheduleTripsByFromStation", fromStation));
                trips.addAll(session.selectList(path + "TripMybatisMapper.getDatesTripsByFromStation", fromStation));
            }
            catch (RuntimeException ex){
                session.rollback();
                ex.printStackTrace();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return trips;
    }

    @Override
    public List<Trip> getTripsToStation(String toStation) {
        List<Trip> trips;
        try(SqlSession session = getSession()){
            try{
                trips = new ArrayList<>(session.selectList(path + "TripMybatisMapper.getScheduleTripsByToStation", toStation));
                trips.addAll(session.selectList(path + "TripMybatisMapper.getDatesTripsByToStation", toStation));
            }
            catch (RuntimeException ex){
                session.rollback();
                ex.printStackTrace();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return trips;
    }

    @Override
    public Trip getTripById(int tripId) throws BusCompanyException {
        Trip trip;
        try(SqlSession session = getSession()){
            try{
                trip = session.selectOne(path + "TripMybatisMapper.getScheduleTripById", tripId);
                if(trip == null){
                    trip = session.selectOne(path + "TripMybatisMapper.getDatesTripById", tripId);
                    if(trip == null){
                        throw new RuntimeException("Unable to find trip");
                    }
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                ex.printStackTrace();
                if(ex.getMessage().contains("Unable to find trip")){
                    throw new BusCompanyException(ErrorCode.TRIP_NOT_EXISTS);
                }
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return trip;
    }

    @Override
    public List<Trip> getTripsByBus(String busName) {
        List<Trip> trips;
        try(SqlSession session = getSession()){
            try{
                trips = new ArrayList<>(session.selectList(path + "TripMybatisMapper.getScheduleTripsByBus", busName));
                trips.addAll(session.selectList(path + "TripMybatisMapper.getDatesTripsByBus", busName));
            }
            catch (RuntimeException ex){
                session.rollback();
                ex.printStackTrace();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return trips;
    }

    @Override
    public List<Trip> getTripsFromDate(Date fromDate) {
        List<Trip> trips;
        try(SqlSession session = getSession()){
            try{
                trips = new ArrayList<>(session.selectList(path + "TripMybatisMapper.getScheduleTripsByFromDate", fromDate));
                trips.addAll(session.selectList(path + "TripMybatisMapper.getDatesTripsByFromDate", fromDate));
            }
            catch (RuntimeException ex){
                session.rollback();
                ex.printStackTrace();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return trips;
    }

    @Override
    public List<Trip> getTripsToDate(Date toDate) {
        List<Trip> trips;
        try(SqlSession session = getSession()){
            try{
                trips = new ArrayList<>(session.selectList(path + "TripMybatisMapper.getScheduleTripsByToDate", toDate));
                trips.addAll(session.selectList(path + "TripMybatisMapper.getDatesTripsByToDate", toDate));
            }
            catch (RuntimeException ex){
                session.rollback();
                ex.printStackTrace();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return trips;
    }

    @Override
    public List<Trip> getAllTrips() {
        List<Trip> trips;
        try(SqlSession session = getSession()){
            try{
                trips = new ArrayList<>(session.selectList(path + "TripMybatisMapper.getAllScheduleTrips"));
                trips.addAll(session.selectList(path + "TripMybatisMapper.getAllDatesTrips"));
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return trips;
    }

    @Override
    public Set<Order> getAllOrders() {
        Set<Order> orders;
        try(SqlSession session = getSession()){
            try{
                orders = new HashSet<>(session.selectList(path + "OrderMybatisMapper.getAllOrders"));
            }
            catch (RuntimeException ex){
                session.rollback();
                ex.printStackTrace();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersFromStation(String fromStation) {
        List<Order> orders;
        try(SqlSession session = getSession()){
            try{
                orders = session.selectList(path + "OrderMybatisMapper.getOrdersFromStation", fromStation);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersToStation(String toStation) {
        List<Order> orders;
        try(SqlSession session = getSession()){
            try{
                orders = session.selectList(path + "OrderMybatisMapper.getOrdersToStation", toStation);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByBus(String busName) {
        List<Order> orders;
        try(SqlSession session = getSession()){
            try{
                orders = session.selectList(path + "OrderMybatisMapper.getOrdersByBus", busName);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersFromDate(Date fromDate) {
        List<Order> orders;
        try(SqlSession session = getSession()){
            try{
                orders = session.selectList(path + "OrderMybatisMapper.getOrdersFromDate", fromDate);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersToDate(Date toDate) {
        List<Order> orders;
        try(SqlSession session = getSession()){
            try{
                orders = session.selectList(path + "OrderMybatisMapper.getOrdersToDate", toDate);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersByClientId(int clientId) {
        List<Order> orders;
        try(SqlSession session = getSession()){
            try{
                orders = session.selectList(path + "OrderMybatisMapper.getOrdersByClientId", clientId);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return orders;
    }


    @Override
    public Order getOrderById(int orderId) {
        Order order;
        try(SqlSession session = getSession()){
            try{
                order = session.selectOne(path + "OrderMybatisMapper.getOrderById", orderId);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return order;
    }

    @Override
    public Bus getBus(String busName) throws BusCompanyException {
        Bus bus;
        try(SqlSession session = getSession()){
            try{
                bus = getBusMapper(session).getBus(busName);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
        }
        return bus;
    }
}
