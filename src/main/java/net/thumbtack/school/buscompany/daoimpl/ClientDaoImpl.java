package net.thumbtack.school.buscompany.daoimpl;

import net.thumbtack.school.buscompany.dao.ClientDao;
import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.model.Order;
import net.thumbtack.school.buscompany.model.Passenger;
import net.thumbtack.school.buscompany.model.Ticket;
import net.thumbtack.school.buscompany.model.User;
import org.apache.ibatis.session.SqlSession;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class ClientDaoImpl extends BaseDaoImpl implements ClientDao {

    @Override
    public void insertTicket(int clientId, Ticket ticket) {
        try(SqlSession session = getSession()){
            try{
                getTicketMapper(session).insertTicket(clientId, ticket);
                for(Passenger passenger : ticket.getPassengers()){
                    getTicketMapper(session).insertPassenger(ticket.getId(), passenger);
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
    public List<Integer> getOccupiedSeats(int orderId) {
        List<Integer> seats;
        try(SqlSession session = getSession()){
            try{
                seats = getTicketMapper(session).getOccupiedSeats(orderId);
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
                passenger = getTicketMapper(session).getByPassport(passport);
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
    public void changeSeat(Passenger passenger, String placeNumber) {
        try(SqlSession session = getSession()){
            try{
                getTicketMapper(session).changeSeat(passenger, placeNumber);
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
                getTicketMapper(session).deleteTicket(orderId);
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
                Integer id = getTicketMapper(session).getClientId(order);
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
    public void takeSeat(Order order, Passenger passenger, int seatNumber) {
        try(SqlSession session = getSession()){
            try{
                getTicketMapper(session).takeSeat(order, passenger, seatNumber);
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
}
