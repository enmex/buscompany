package net.thumbtack.school.buscompany.mapper.mybatis;

import net.thumbtack.school.buscompany.model.Order;
import net.thumbtack.school.buscompany.model.Passenger;
import net.thumbtack.school.buscompany.model.Place;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface OrderMybatisMapper {

    @Insert("INSERT INTO `order` (id_client, id_trip) VALUES(#{order.client.id}, #{idTripDate})")
    @Options(useGeneratedKeys = true, keyProperty = "order.id")
    void insertOrder(@Param("idTripDate") int idTripDate, @Param("order") Order order);

    @Insert("INSERT INTO passenger (id_order, firstname, lastname, passport) " +
            "VALUES(#{order.id}, #{passenger.firstName}, #{passenger.lastName}," +
            " #{passenger.passport})")
    @Options(useGeneratedKeys = true, keyProperty = "passenger.id")
    void insertPassenger(@Param("order") Order order, @Param("passenger") Passenger passenger);

    @Select("SELECT place_number FROM occupied_seats WHERE id_trip = #{idTripDate} AND id_passenger IS NULL")
    List<Integer> getFreeSeats(@Param("idTripDate") int idTripDate);

    @Select("SELECT id, firstname, lastname, passport FROM passenger WHERE passport = #{passport}")
    Passenger getByPassport(@Param("passport") String passport);

    @Update("UPDATE occupied_seats SET place_number = #{place} WHERE id_passenger = #{passenger.id}")
    void changeSeat(@Param("passenger") Passenger passenger, @Param("place") int placeNumber);

    @Delete("DELETE FROM `order` WHERE id = #{orderId}")
    void deleteOrder(@Param("orderId") int orderId);

    @Select("SELECT id_client FROM `order` WHERE id = #{order.id}")
    Integer getClientId(@Param("order") Order order);

    @Update("UPDATE occupied_seats SET id_passenger = #{place.passenger.id} " +
            "WHERE id_trip = #{idTripDate} AND place_number = #{place.placeNumber}")
    void takeSeat(@Param("idTripDate") int idTripDate, @Param("place") Place place);
}
