package net.thumbtack.school.buscompany.daoimpl;

import net.thumbtack.school.buscompany.dao.AdminDao;

import net.thumbtack.school.buscompany.exception.CheckedException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.model.*;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class AdminDaoImpl extends BaseDaoImpl implements AdminDao {

    @Override
    public List<Client> getAllClients() throws CheckedException {
        List<Client> clients;
        try(SqlSession session = getSession()){
            try{
                clients = session.selectList(path + "ClientMybatisMapper.getAllClients");
            }
            catch(RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return clients;
    }

    @Override
    public List<Bus> getAllBuses() throws CheckedException {
        List<Bus> buses;
        try(SqlSession session = getSession()){
            try{
                buses = getBusMapper(session).getAllBuses();
            }
            catch(RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return buses;
    }

    @Override
    public void registerTrip(Trip trip) throws CheckedException {
        try(SqlSession session = getSession()){
            try{
                getTripMapper(session).registerTrip(trip);

                for(TripDate tripDate : trip.getTripDates()){
                    getTripMapper(session).insertTripDate(trip, tripDate);
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                ex.printStackTrace();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public boolean containsBus(String busName) throws CheckedException {
        boolean containsBus;
        try(SqlSession session = getSession()){
            try{
                containsBus = getBusMapper(session).getBus(busName) != null;
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
        return containsBus;
    }

    @Override
    public void updateTrip(Trip trip) throws CheckedException {
        try(SqlSession session = getSession()){
            try{
                getTripMapper(session).updateTrip(trip);
                getTripMapper(session).deleteTripDates(trip);

                for(TripDate tripDate : trip.getTripDates()){
                    getTripMapper(session).insertTripDate(trip, tripDate);
                }
            }
            catch(RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
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
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
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
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
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
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
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
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
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
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
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

                for(TripDate tripDate : trip.getTripDates()){
                    for(int placeNumber = 0; placeNumber < placeCount; placeNumber++){
                        getTripMapper(session).registerPlace(tripDate.getId(), placeNumber);
                    }
                }
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }

    @Override
    public void registerBus(Bus bus) {
        try(SqlSession session = getSession()){
            try {
                getBusMapper(session).insert(bus);
            }
            catch (RuntimeException ex){
                session.rollback();
                throw new CheckedException(ErrorCode.DATABASE_ERROR);
            }
            session.commit();
        }
    }
}
