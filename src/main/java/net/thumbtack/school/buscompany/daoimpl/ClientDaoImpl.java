package net.thumbtack.school.buscompany.daoimpl;

import net.thumbtack.school.buscompany.dao.ClientDao;
import net.thumbtack.school.buscompany.exception.ServerException;
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
                int idTripDate = order.getIdTripDate();

                if(getOrderMapper(session).takePlaces(order.getPassengersNumber(), order) == 0) {
                    throw new RuntimeException("Свободных мест нет");
                }

                getOrderMapper(session).insertOrder(idTripDate, order);

                for(Passenger passenger : order.getPassengers()){
                    getOrderMapper(session).insertPassenger(order, passenger);
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    if(ex.getMessage().contains("Duplicate entry")){
                        throw new ServerException(ErrorCode.PASSENGER_ALREADY_EXISTS);
                    }
                    throw new ServerException(ErrorCode.TRIP_NOT_EXISTS);
                }
                if(ex.getMessage().contains("Свободных мест нет")) {
                    throw new ServerException(ErrorCode.NO_FREE_PLACES);
                }
                throw new ServerException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public List<Integer> getFreePlaces(Order order) {
        List<Integer> places;
        try(SqlSession session = getSession()){
            try{
                int idTripDate = getTripMapper(session).getIdTripDateUsingOrder(order);
                places = getOrderMapper(session).getFreePlaces(idTripDate);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new ServerException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return places;
    }

    @Override
    public void changePlace(Place place) {
        try(SqlSession session = getSession()){
            try{
                int idTripDate = getTripMapper(session).getIdTripDateUsingOrder(place.getOrder());

                getOrderMapper(session).freePlace(place, idTripDate);

                int updatedRows = getOrderMapper(session).changePlace(place, idTripDate);

                if(updatedRows == 0){
                    throw new RuntimeException("Место занято");
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    throw new ServerException(ErrorCode.PLACE_IS_ALREADY_TAKEN_BY_THIS_PASSENGER);
                }
                if(ex.getMessage().equals("Место занято")){
                    throw new ServerException(ErrorCode.PLACE_IS_OCCUPIED);
                }
                throw new ServerException(ErrorCode.DATABASE_ERROR);
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
                throw new ServerException(ErrorCode.DATABASE_ERROR);
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
                throw new ServerException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return isClientOrder;
    }

    @Override
    public int takePlace(Place place) {
        int updatedRows = 0;
        try(SqlSession session = getSession()){
            try{
                int idTripDate = getTripMapper(session).getIdTripDateUsingOrder(place.getOrder());

                updatedRows = getOrderMapper(session).takePlace(idTripDate, place);
            }
            catch (RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    if(ex.getMessage().contains("Duplicate entry")){
                        throw new ServerException(ErrorCode.PASSENGER_ALREADY_EXISTS);
                    }
                    throw new ServerException(ErrorCode.TRIP_NOT_EXISTS);
                }
                throw new ServerException(ErrorCode.DATABASE_ERROR);
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
                throw new ServerException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
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
                throw new ServerException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return id;
    }
}
