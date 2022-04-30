package net.thumbtack.school.buscompany.daoimpl;

import net.thumbtack.school.buscompany.dao.ClientDao;
import net.thumbtack.school.buscompany.exception.BusCompanyException;
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
                        throw new BusCompanyException(ErrorCode.PASSENGER_ALREADY_EXISTS);
                    }
                    throw new BusCompanyException(ErrorCode.TRIP_NOT_EXISTS);
                }
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
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
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return seats;
    }

    @Override
    public Passenger getByPassport(String passport) {
        Passenger passenger;
        try(SqlSession session = getSession()){
            try{
                passenger = getOrderMapper(session).getByPassport(passport);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return passenger;
    }

    @Override
    public void changeSeat(Passenger passenger, int placeNumber) {
        try(SqlSession session = getSession()){
            try{
                getOrderMapper(session).changeSeat(passenger, placeNumber);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
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
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
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
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return isClientOrder;
    }

    @Override
    public void takeSeat(Place place) {
        try(SqlSession session = getSession()){
            try{
                int idTripDate = getTripMapper(session).getIdTripDateUsingOrder(place.getOrder());

                getOrderMapper(session).takeSeat(idTripDate, place);
            }
            catch (RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    if(ex.getMessage().contains("Duplicate entry")){
                        throw new BusCompanyException(ErrorCode.PASSENGER_ALREADY_EXISTS);
                    }
                    throw new BusCompanyException(ErrorCode.TRIP_NOT_EXISTS);
                }
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public void insertPassenger(Order order, Passenger passenger) {
        try(SqlSession session = getSession()){
            try{
                getTripMapper(session).insertPassenger(order, passenger);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }
}
