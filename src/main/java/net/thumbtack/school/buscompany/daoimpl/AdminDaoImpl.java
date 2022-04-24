package net.thumbtack.school.buscompany.daoimpl;

import net.thumbtack.school.buscompany.dao.AdminDao;

import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.model.*;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.List;

@Component
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

                if(trip instanceof DatesTrip){
                    DatesTrip datesTrip = (DatesTrip) trip;
                    for(Date date : datesTrip.getDates()){
                        getTripMapper(session).insertTripDate(datesTrip, date);
                    }
                }
                else {
                    ScheduleTrip scheduleTrip = (ScheduleTrip) trip;
                    getTripMapper(session).insertSchedule(scheduleTrip);
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

    @Override
    public Trip getTripById(String tripId) throws BusCompanyException {
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
    public void updateTrip(Trip trip) {
        try(SqlSession session = getSession()){
            try{
                getTripMapper(session).update(trip);
            }
        }
    }
}
