package net.thumbtack.school.buscompany.dao;

import net.thumbtack.school.buscompany.exception.CheckedException;
import net.thumbtack.school.buscompany.model.Bus;
import net.thumbtack.school.buscompany.model.Client;
import net.thumbtack.school.buscompany.model.Schedule;
import net.thumbtack.school.buscompany.model.Trip;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AdminDao {
    List<Client> getAllClients() throws CheckedException;
    void registerBus(Bus bus);
    List<Bus> getAllBuses() throws CheckedException;
    boolean containsBus(String busName) throws CheckedException;
    void registerTrip(Trip trip) throws CheckedException;

    void updateTrip(Trip Trip) throws CheckedException;

    void deleteTrip(Trip trip);

    void approveTrip(Trip trip);

    void registerSchedule(Trip trip, Schedule schedule);

    void updateSchedule(Trip trip, Schedule schedule);

    Schedule getSchedule(Trip trip);

    void registerPlaces(Trip trip, Bus bus);
}
