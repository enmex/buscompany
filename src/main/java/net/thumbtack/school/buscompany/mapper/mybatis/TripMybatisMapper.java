package net.thumbtack.school.buscompany.mapper.mybatis;

import net.thumbtack.school.buscompany.model.*;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

public interface TripMybatisMapper {

    @Insert("INSERT INTO trip (bus_name, from_station, to_station, `start`, duration, price) " +
            "VALUES (#{trip.bus.busName}, #{trip.fromStation}, " +
                    "#{trip.toStation}, #{trip.start}, #{trip.duration}, #{trip.price})")
    @Options(useGeneratedKeys = true, keyProperty = "trip.id")
    void registerTrip(@Param("trip") Trip trip);

    @Insert("INSERT INTO trip_date (id_trip, `date`, free_places) VALUES (#{trip.id}, #{tripDate.date}, #{trip.bus.placeCount})")
    @Options(useGeneratedKeys = true, keyProperty = "tripDate.id")
    void insertTripDate(@Param("trip") Trip trip, @Param("tripDate") TripDate tripDate);

    @Update("UPDATE trip SET bus_name = #{trip.bus.busName}, from_station = #{trip.fromStation}, to_station = #{trip.toStation}" +
            ", `start` = #{trip.start}, duration = #{trip.duration}, " +
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

    @Select("SELECT id_trip, from_date, to_date, period FROM `schedule` WHERE id_trip = #{trip.id}")
    @Results({
            @Result(property = "tripId", column = "id_trip"),
            @Result(property = "fromDate", column = "from_date"),
            @Result(property = "toDate", column = "to_date"),
            @Result(property = "period", column = "period")
    })
    Schedule getSchedule(@Param("trip") Trip trip);

    @Delete("DELETE FROM trip")
    void clear();

    @Delete("DELETE FROM trip_date WHERE id_trip = #{trip.id}")
    void deleteTripDates(@Param("trip") Trip trip);

    @Select("SELECT id FROM trip_date WHERE id_trip = #{order.trip.id} AND `date` = #{order.date}")
    int getIdTripDateUsingOrder(@Param("order") Order order);

    @Insert("INSERT INTO seats (id_trip_date, place_number) VALUES(#{idTripDate}, #{placeNumber})")
    void registerPlace(@Param("idTripDate") int idTripDate, @Param("placeNumber") int placeNumber);

    @Select("SELECT id, `date` FROM trip_date WHERE id_trip = #{trip.id}")
    @Results({
            @Result(column = "id", property = "id"),
            @Result(column = "`date`", property = "date")
    })
    List<TripDate> getTripDatesList(@Param("trip") Trip trip);

    @Insert("INSERT INTO passenger (id_order, firstname, lastname, passport) VALUES (#{order.id}, #{p.firstName}, #{p.lastName}, #{p.passport})")
    @Options(useGeneratedKeys = true, keyProperty = "p.id")
    void insertPassenger(@Param("order") Order order, @Param("p") Passenger passenger);
}
