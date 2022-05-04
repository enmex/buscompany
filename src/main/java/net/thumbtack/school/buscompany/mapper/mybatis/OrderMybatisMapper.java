package net.thumbtack.school.buscompany.mapper.mybatis;

import net.thumbtack.school.buscompany.model.Order;
import net.thumbtack.school.buscompany.model.Passenger;
import net.thumbtack.school.buscompany.model.Place;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface OrderMybatisMapper {

    @Insert("INSERT INTO `order` (id_client, id_trip_date) VALUES(#{order.client.id}, #{idTripDate})")
    @Options(useGeneratedKeys = true, keyProperty = "order.id")
    void insertOrder(@Param("idTripDate") int idTripDate, @Param("order") Order order);

    @Insert("INSERT INTO passenger (id_order, firstname, lastname, passport) " +
            "VALUES(#{order.id}, #{passenger.firstName}, #{passenger.lastName}," +
            " #{passenger.passport})")
    @Options(useGeneratedKeys = true, keyProperty = "passenger.id")
    void insertPassenger(@Param("order") Order order, @Param("passenger") Passenger passenger);

    @Select("SELECT place_number FROM seats WHERE id_trip_date = #{idTripDate} AND id_passenger IS NULL")
    List<Integer> getFreeSeats(@Param("idTripDate") int idTripDate);

    @Update("UPDATE seats SET id_passenger = #{place.passenger.id}" +
            " WHERE place_number = #{place.placeNumber} AND id_trip_date = #{idTripDate} AND id_passenger IS NULL")
    int changeSeat(@Param("place") Place place, @Param("idTripDate") int idTripDate);

    @Select("SELECT id_trip_date FROM seats WHERE id_passenger IS NULL AND place_number = #{place.placeNumber} AND id_trip_date = #{idTripDate}")
    Integer getSeat(@Param("place") Place place, @Param("idTripDate") int idTripDate);

    @Delete("DELETE FROM `order` WHERE id = #{orderId}")
    void deleteOrder(@Param("orderId") int orderId);

    @Select("SELECT id_client FROM `order` WHERE id = #{order.id}")
    Integer getClientId(@Param("order") Order order);

    @Update("UPDATE seats SET id_passenger = #{place.passenger.id} " +
            "WHERE id_trip_date = #{idTripDate} AND place_number = #{place.placeNumber}")
    int takeSeat(@Param("idTripDate") int idTripDate, @Param("place") Place place);

    @Update("UPDATE trip_date SET free_places = free_places - #{passengersNumber} WHERE free_places >= #{passengersNumber}" +
            " AND `date` = #{order.date} AND id_trip = #{order.trip.id}")
    int takeSeats(@Param("passengersNumber") int passengersNumber, @Param("order") Order order);

    @Update("UPDATE seats SET id_passenger = NULL WHERE id_passenger = #{place.passenger.id} AND id_trip_date = #{idTripDate}")
    void freeSeat(@Param("place") Place place, @Param("idTripDate") int idTripDate);
}
