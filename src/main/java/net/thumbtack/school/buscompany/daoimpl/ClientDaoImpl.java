package net.thumbtack.school.buscompany.daoimpl;

import net.thumbtack.school.buscompany.dao.ClientDao;
import net.thumbtack.school.buscompany.exception.CheckedException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.model.Order;
import net.thumbtack.school.buscompany.model.Passenger;
import net.thumbtack.school.buscompany.model.Place;
import net.thumbtack.school.buscompany.model.User;
import org.apache.ibatis.session.SqlSession;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class ClientDaoImpl extends BaseDaoImpl implements ClientDao {

    @Override
    public void insertOrder(Order order) {
        try(SqlSession session = getSession()){
            try{
                int idTripDate = getTripMapper(session).getIdTripDateUsingOrder(order);

                getOrderMapper(session).insertOrder(idTripDate, order);

                for(Passenger passenger : order.getPassengers()){
                    getOrderMapper(session).insertPassenger(order, passenger);
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    if(ex.getMessage().contains("Duplicate entry")){
                        throw new CheckedException(ErrorCode.PASSENGER_ALREADY_EXISTS);
                    }
                    throw new CheckedException(ErrorCode.TRIP_NOT_EXISTS);
                }
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public List<Integer> getFreeSeats(Order order) {
        List<Integer> seats;
        try(SqlSession session = getSession()){
            try{
                int idTripDate = getTripMapper(session).getIdTripDateUsingOrder(order);
                seats = getOrderMapper(session).getFreeSeats(idTripDate);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return seats;
    }

    @Override
    public void changeSeat(Place place) {
        try(SqlSession session = getSession()){
            try{
                int idTripDate = getTripMapper(session).getIdTripDateUsingOrder(place.getOrder());

                getOrderMapper(session).freeSeat(place, idTripDate);

                int updatedRows = getOrderMapper(session).changeSeat(place, idTripDate);

                if(updatedRows == 0){
                    throw new RuntimeException("Место занято");
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    throw new CheckedException(ErrorCode.PLACE_IS_ALREADY_TAKEN_BY_THIS_PASSENGER);
                }
                if(ex.getMessage().equals("Место занято")){
                    throw new CheckedException(ErrorCode.PLACE_IS_OCCUPIED);
                }
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public void cancelOrder(int orderId) {
        try(SqlSession session = getSession()){
            try{
                getOrderMapper(session).deleteOrder(orderId);
            } catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public boolean isClientOrder(User user, Order order) {
        boolean isClientOrder;
        try(SqlSession session = getSession()){
            try{
                Integer id = getOrderMapper(session).getClientId(order);
                isClientOrder = id != null && id == user.getId();
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return isClientOrder;
    }

    @Override
    public int takeSeat(Place place) {
        int updatedRows = 0;
        try(SqlSession session = getSession()){
            try{
                int idTripDate = getTripMapper(session).getIdTripDateUsingOrder(place.getOrder());

                updatedRows = getOrderMapper(session).takeSeat(idTripDate, place);
            }
            catch (RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    if(ex.getMessage().contains("Duplicate entry")){
                        throw new CheckedException(ErrorCode.PASSENGER_ALREADY_EXISTS);
                    }
                    throw new CheckedException(ErrorCode.TRIP_NOT_EXISTS);
                }
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return updatedRows;
    }

    @Override
    public void insertPassenger(Order order, Passenger passenger) {
        try(SqlSession session = getSession()){
            try{
                getTripMapper(session).insertPassenger(order, passenger);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public int takeSeats(Order order) {
        int updatedRows;
        try(SqlSession session = getSession()){
            try {
                int passengersNumber = order.getPassengersNumber();
                updatedRows = getOrderMapper(session).takeSeats(passengersNumber, order);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return updatedRows;
    }

    @Override
    public int getIdTripDate(Order order) {
        int id;
        try(SqlSession session = getSession()){
            try {
                id = getTripMapper(session).getIdTripDateUsingOrder(order);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return id;
    }
}
