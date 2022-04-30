package net.thumbtack.school.buscompany.dao;

import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.model.Bus;
import net.thumbtack.school.buscompany.model.Client;
import net.thumbtack.school.buscompany.model.Schedule;
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

    void updateTrip(Trip Trip) throws BusCompanyException;

    void deleteTrip(Trip trip);

    void approveTrip(Trip trip);

    void registerSchedule(Trip trip, Schedule schedule);

    void updateSchedule(Trip trip, Schedule schedule);

    Schedule getSchedule(Trip trip);

    void registerPlaces(Trip trip, Bus bus);
}
