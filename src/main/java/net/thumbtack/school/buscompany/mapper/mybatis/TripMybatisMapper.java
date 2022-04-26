package net.thumbtack.school.buscompany.mapper.mybatis;

import net.thumbtack.school.buscompany.model.DatesTrip;
import net.thumbtack.school.buscompany.model.ScheduleTrip;
import net.thumbtack.school.buscompany.model.Trip;
import org.apache.ibatis.annotations.*;

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

    @Update("UPDATE trip SET bus_name = #{trip.busName}, from_station = #{trip.fromStation}, to_station = #{trip.toStation}" +
            ", `start` = DATE_FORMAT(#{trip.start}, '%H:%i'), duration = DATE_FORMAT(#{trip.duration}, '%H:%i'), " +
            "price = #{trip.price} WHERE id = #{trip.id}")
    void updateTrip(@Param("trip") Trip trip);

    @Delete("DELETE FROM trip WHERE id = #{trip.id}")
    void deleteTrip(@Param("trip") Trip trip);

    @Update("UPDATE trip SET approved = #{trip.approved} WHERE id = #{trip.id}")
    void approveTrip(@Param("trip") Trip trip);
}
