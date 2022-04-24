package net.thumbtack.school.buscompany.dao;

import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.model.Bus;
import net.thumbtack.school.buscompany.model.Client;
import net.thumbtack.school.buscompany.model.ScheduleTrip;
import net.thumbtack.school.buscompany.model.Trip;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AdminDao {
    List<Client> getAllClients() throws BusCompanyException;
    void registerBus(Bus bus) throws BusCompanyException;
    List<Bus> getAllBuses() throws BusCompanyException;
    boolean containsBus(String busName) throws BusCompanyException;
    void registerTrip(Trip trip) throws BusCompanyException;

    Bus getBus(String busName) throws BusCompanyException;

    Trip getTripById(String tripId) throws BusCompanyException;

    void updateTrip(Trip Trip);
}