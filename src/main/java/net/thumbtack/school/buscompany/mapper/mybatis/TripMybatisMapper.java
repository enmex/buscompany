package net.thumbtack.school.buscompany.mapper.mybatis;

import net.thumbtack.school.buscompany.model.Schedule;
import net.thumbtack.school.buscompany.model.Trip;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.Date;

public interface TripMybatisMapper {

    @Insert("INSERT INTO trip (bus_name, from_station, to_station, `start`, duration, price) " +
            "VALUES (#{trip.busName}, #{trip.fromStation}, " +
                    "#{trip.toStation}, DATE_FORMAT(#{trip.start}, '%H:%i'), DATE_FORMAT(#{trip.duration}, '%H:%i'), #{trip.price})")
    @Options(useGeneratedKeys = true, keyProperty = "trip.id")
    void registerTrip(@Param("trip") Trip trip);

    @Insert("INSERT INTO trip_date (id_trip, `date`) VALUES (#{datesTrip.id}, DATE_FORMAT(#{date}, '%Y-%m-%d'))")
    void insertTripDate(@Param("datesTrip") Trip trip, @Param("date") LocalDate date);

    @Update("UPDATE trip SET bus_name = #{trip.busName}, from_station = #{trip.fromStation}, to_station = #{trip.toStation}" +
            ", `start` = DATE_FORMAT(#{trip.start}, '%H:%i'), duration = DATE_FORMAT(#{trip.duration}, '%H:%i'), " +
            "price = #{trip.price} WHERE id = #{trip.id}")
    void updateTrip(@Param("trip") Trip trip);

    @Delete("DELETE FROM trip WHERE id = #{trip.id}")
    void deleteTrip(@Param("trip") Trip trip);

    @Update("UPDATE trip SET approved = #{trip.approved} WHERE id = #{trip.id}")
    void approveTrip(@Param("trip") Trip trip);

    @Insert("INSERT INTO `schedule` VALUES (#{trip.id}, #{schedule.fromDate}, #{schedule.toDate}, #{schedule.period})")
    void registerSchedule(@Param("trip") Trip trip, @Param("schedule") Schedule schedule);

    @Update("UPDATE `schedule` SET from_date = #{schedule.fromDate}, to_date = #{schedule.toDate}, period = #{schedule.period} WHERE id_trip = #{trip.id}")
    void updateSchedule(@Param("trip") Trip trip, @Param("schedule") Schedule schedule);

    @Select("SELECT from_date, to_date, period FROM `schedule` WHERE id_trip = #{trip.id}")
    @Results({
            @Result(property = "fromDate", column = "from_date"),
            @Result(property = "toDate", column = "to_date"),
            @Result(property = "period", column = "period")
    })
    Schedule getSchedule(@Param("trip") Trip trip);

    @Delete("DELETE FROM trip")
    void clear();
}
