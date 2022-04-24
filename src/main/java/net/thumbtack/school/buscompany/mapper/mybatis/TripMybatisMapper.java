package net.thumbtack.school.buscompany.mapper.mybatis;

import net.thumbtack.school.buscompany.model.DatesTrip;
import net.thumbtack.school.buscompany.model.ScheduleTrip;
import net.thumbtack.school.buscompany.model.Trip;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

public interface TripMybatisMapper {

    @Insert("INSERT INTO trip (bus_name, from_station, to_station, `start`, duration, price) " +
            "VALUES (#{trip.busName}, #{trip.fromStation}, " +
                    "#{trip.toStation}, DATE_FORMAT(#{trip.start}, '%H:%i'), DATE_FORMAT(#{trip.duration}, '%H:%i'), #{trip.price})")
    @Options(useGeneratedKeys = true, keyProperty = "trip.id")
    void registerTrip(@Param("trip") Trip trip);

    @Insert("INSERT INTO trip_date (id_trip, `date`) VALUES (#{datesTrip.id}, DATE_FORMAT(#{date}, '%Y-%m-%d'))")
    void insertTripDate(@Param("datesTrip") DatesTrip datesTrip, @Param("date") Date date);

    @Insert("INSERT INTO trip_schedule (id_trip, from_date, to_date, period) " +
            "VALUES (#{scheduleTrip.id}, #{scheduleTrip.schedule.fromDate}, #{scheduleTrip.schedule.toDate}, " +
            "#{scheduleTrip.schedule.period})")
    void insertSchedule(@Param("scheduleTrip") ScheduleTrip scheduleTrip);

    @Update("UPDATE trip SET ")
    void update(@Param("trip") Trip trip);
}
