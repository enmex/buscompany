package net.thumbtack.school.buscompany.daoimpl;

import net.thumbtack.school.buscompany.dao.AdminDao;

import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.model.*;
import org.apache.ibatis.session.SqlSession;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.List;

public class AdminDaoImpl extends BaseDaoImpl implements AdminDao {

    @Override
    public List<Client> getAllClients() throws BusCompanyException {
        List<Client> clients;
        try(SqlSession session = getSession()){
            try{
                clients = session.selectList(path + "ClientMybatisMapper.getAllClients");
            }
            catch(RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return clients;
    }

    @Override
    public void registerBus(Bus bus) throws BusCompanyException {
        try(SqlSession session = getSession()){
            try{
                getBusMapper(session).insert(bus);
            }
            catch(RuntimeException ex){
                session.rollback();
                if(ex.getCause() instanceof SQLIntegrityConstraintViolationException){
                    throw new BusCompanyException(ErrorCode.DUPLICATE_BUS);
                }
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public List<Bus> getAllBuses() throws BusCompanyException {
        List<Bus> buses;
        try(SqlSession session = getSession()){
            try{
                buses = getBusMapper(session).getAllBuses();
            }
            catch(RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return buses;
    }

    @Override
    public void registerTrip(Trip trip) throws BusCompanyException {
        try(SqlSession session = getSession()){
            try{
                getTripMapper(session).registerTrip(trip);

                for(LocalDate date : trip.getDates()){
                    getTripMapper(session).insertTripDate(trip, date);
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                ex.printStackTrace();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public boolean containsBus(String busName) throws BusCompanyException {
        boolean containsBus;
        try(SqlSession session = getSession()){
            try{
                containsBus = getBusMapper(session).getBus(busName) != null;
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return containsBus;
    }

    @Override
    public void updateTrip(Trip trip) throws BusCompanyException {
        try(SqlSession session = getSession()){
            try{
                getTripMapper(session).updateTrip(trip);
                getTripMapper(session).deleteTripDates(trip);

                for(LocalDate date : trip.getDates()){
                    getTripMapper(session).insertTripDate(trip, date);
                }
            }
            catch(RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public void deleteTrip(Trip trip) {
        try(SqlSession session = getSession()){
            try{
                getTripMapper(session).deleteTrip(trip);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public void approveTrip(Trip trip) {
        try(SqlSession session = getSession()){
            try{
                getTripMapper(session).approveTrip(trip);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public void registerSchedule(Trip trip, Schedule schedule) {
        try(SqlSession session = getSession()){
            try{
                getTripMapper(session).registerSchedule(trip, schedule);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public void updateSchedule(Trip trip, Schedule schedule) {
        try(SqlSession session = getSession()){
            try{
                getTripMapper(session).updateSchedule(trip, schedule);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public Schedule getSchedule(Trip trip) {
        Schedule schedule;
        try(SqlSession session = getSession()){
            try{
                schedule = getTripMapper(session).getSchedule(trip);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return schedule;
    }

    @Override
    public void registerPlaces(Trip trip, Bus bus) {
        try(SqlSession session = getSession()){
            try{
                int placeCount = bus.getPlaceCount();

                for(LocalDate date : trip.getDates()){
                    int idTripDate = getTripMapper(session).getIdTripDateUsingTripAndDate(trip, date);

                    for(int placeNumber = 0; placeNumber < placeCount; placeNumber++){
                        getTripMapper(session).registerPlace(idTripDate, placeNumber);
                    }
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new BusCompanyException(ErrorCode.DATABASE_ERROR, "registerPlaces");
            }
            session.commit();
        }
    }
}
