package net.thumbtack.school.buscompany.daoimpl;

import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.exception.CheckedException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.model.*;
import org.apache.ibatis.session.SqlSession;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.*;

public class UserDaoImpl extends BaseDaoImpl implements UserDao {
    @Override
    public String register(User user) throws CheckedException {
        try(SqlSession session = getSession()){
            String uuid;
            try{
                getUserMapper(session).insert(user);

                uuid = UUID.randomUUID().toString();
                getUserMapper(session).insertSession(user, uuid);

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
                    throw new CheckedException(ErrorCode.USER_ALREADY_EXISTS);
                }
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
            return uuid;
        }
    }

    @Override
    public void unregister(User user) throws CheckedException {
        try(SqlSession session = getSession()){
            try{
                getUserMapper(session).deleteUser(user);
            }
            catch(RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    throw new CheckedException(ErrorCode.USER_NOT_EXISTS);
                }
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public User getByLogin(String login) throws CheckedException {
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
                    throw new CheckedException(ErrorCode.INVALID_LOGIN_OR_PASSWORD);
                }
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return user;
    }

    @Override
    public User getBySession(String uuid) throws CheckedException {
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
                    throw new CheckedException(ErrorCode.USER_IS_OFFLINE);
                }
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return user;
    }

    @Override
    public String insertSession(User user) throws CheckedException {
        String uuid;
        try(SqlSession session = getSession()){
            try{
                uuid = UUID.randomUUID().toString();
                getUserMapper(session).insertSession(user, uuid);
            }
            catch(RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    throw new CheckedException(ErrorCode.UNABLE_TO_OPEN_SESSION);
                }
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return uuid;
    }

    @Override
    public void closeSession(String uuid) throws CheckedException {
        try(SqlSession session = getSession()){
            try{
                getUserMapper(session).closeSession(uuid);
            }
            catch(RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    throw new CheckedException(ErrorCode.UNABLE_TO_CLOSE_SESSION);
                }
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public String getUserType(User user) throws CheckedException {
        String userType;
        try(SqlSession session = getSession()){
            try{
                userType = getUserMapper(session).getUserType(user);
            }
            catch(RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    throw new CheckedException(ErrorCode.USER_NOT_EXISTS);
                }
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return userType;
    }

    @Override
    public void updateUser(User user) throws CheckedException {
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
                    throw new CheckedException(ErrorCode.USER_NOT_EXISTS);
                }
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public List<Trip> getTripsFromStation(String fromStation) {
        List<Trip> trips;
        try(SqlSession session = getSession()){
            try{
                trips = new ArrayList<>(session.selectList(path + "TripMybatisMapper.getTripsByFromStation", fromStation));
            }
            catch (RuntimeException ex){
                session.rollback();
                ex.printStackTrace();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
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
                trips = new ArrayList<>(session.selectList(path + "TripMybatisMapper.getTripsByToStation", toStation));
            }
            catch (RuntimeException ex){
                session.rollback();
                ex.printStackTrace();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return trips;
    }

    @Override
    public Trip getTripById(int tripId) throws CheckedException {
        Trip trip;
        try(SqlSession session = getSession()){
            try{
                trip = session.selectOne(path + "TripMybatisMapper.getTripById", tripId);
                if(trip == null){
                    throw new RuntimeException("Unable to find trip");
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                if(ex.getMessage().contains("Unable to find trip")){
                    throw new CheckedException(ErrorCode.TRIP_NOT_EXISTS);
                }
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
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
                trips = new ArrayList<>(session.selectList(path + "TripMybatisMapper.getTripsByBus", busName));
            }
            catch (RuntimeException ex){
                session.rollback();
                ex.printStackTrace();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return trips;
    }

    @Override
    public List<Trip> getTripsFromDate(LocalDate fromDate) {
        List<Trip> trips;
        try(SqlSession session = getSession()){
            try{
                trips = new ArrayList<>(session.selectList(path + "TripMybatisMapper.getTripsByFromDate", fromDate));
            }
            catch (RuntimeException ex){
                session.rollback();
                ex.printStackTrace();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return trips;
    }

    @Override
    public List<Trip> getTripsToDate(LocalDate toDate) {
        List<Trip> trips;
        try(SqlSession session = getSession()){
            try{
                trips = new ArrayList<>(session.selectList(path + "TripMybatisMapper.getTripsByToDate", toDate));
            }
            catch (RuntimeException ex){
                session.rollback();
                ex.printStackTrace();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
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
                trips = new ArrayList<>(session.selectList(path + "TripMybatisMapper.getAllTrips"));
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
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
                for(Order order : orders){
                    order.getTrip().setTripDates(getTripMapper(session).getTripDatesList(order.getTrip()));
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                ex.printStackTrace();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
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
                for(Order order : orders){
                    order.getTrip().setTripDates(getTripMapper(session).getTripDatesList(order.getTrip()));
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
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
                for(Order order : orders){
                    order.getTrip().setTripDates(getTripMapper(session).getTripDatesList(order.getTrip()));
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
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
                for(Order order : orders){
                    order.getTrip().setTripDates(getTripMapper(session).getTripDatesList(order.getTrip()));
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersFromDate(LocalDate fromDate) {
        List<Order> orders;
        try(SqlSession session = getSession()){
            try{
                orders = session.selectList(path + "OrderMybatisMapper.getOrdersFromDate", fromDate);
                for(Order order : orders){
                    order.getTrip().setTripDates(getTripMapper(session).getTripDatesList(order.getTrip()));
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return orders;
    }

    @Override
    public List<Order> getOrdersToDate(LocalDate toDate) {
        List<Order> orders;
        try(SqlSession session = getSession()){
            try{
                orders = session.selectList(path + "OrderMybatisMapper.getOrdersToDate", toDate);
                for(Order order : orders){
                    order.getTrip().setTripDates(getTripMapper(session).getTripDatesList(order.getTrip()));
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
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
                for(Order order : orders){
                    order.getTrip().setTripDates(getTripMapper(session).getTripDatesList(order.getTrip()));
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
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
                if(order != null) {
                    order.getTrip().setTripDates(getTripMapper(session).getTripDatesList(order.getTrip()));
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return order;
    }

    @Override
    public Bus getBus(String busName) throws CheckedException {
        Bus bus;
        try(SqlSession session = getSession()){
            try{
                bus = getBusMapper(session).getBus(busName);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
        }
        return bus;
    }

    @Override
    public void clearSessions(int userIdleTimeout) {
        try(SqlSession session = getSession()){
            try {
                getUserMapper(session).clearSessions(userIdleTimeout);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public void updateSession(String uuid) {
        try(SqlSession session = getSession()){
            try {
                getUserMapper(session).updateSession(uuid);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }
}
