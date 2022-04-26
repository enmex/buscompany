package net.thumbtack.school.buscompany.mapper.mybatis;

import net.thumbtack.school.buscompany.model.Order;
import net.thumbtack.school.buscompany.model.Passenger;
import net.thumbtack.school.buscompany.model.Ticket;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface OrderMybatisMapper {

    @Insert("INSERT INTO ticket (id_client, id_trip, `date`) VALUES(#{clientId}, #{ticket.tripId}, DATE_FORMAT(#{ticket.date}, '%Y-%m-%d'))")
    @Options(useGeneratedKeys = true, keyProperty = "ticket.id")
    void insertTicket(@Param("clientId") int clientId, @Param("ticket") Ticket ticket);

    @Insert("INSERT INTO passenger (id_ticket, firstname, lastname, passport) " +
            "VALUES(#{ticketId}, #{passenger.firstName}, #{passenger.lastName}," +
            " #{passenger.passport})")
    @Options(useGeneratedKeys = true, keyProperty = "passenger.id")
    void insertPassenger(@Param("ticketId") int ticketId, @Param("passenger") Passenger passenger);

    @Select("SELECT place_number FROM occupied_seats WHERE id_ticket = #{orderId}")
    List<Integer> getOccupiedSeats(@Param("orderId") int orderId);

    @Select("SELECT id, firstname, lastname, passport FROM passenger WHERE passport = #{passport}")
    Passenger getByPassport(@Param("passport") String passport);

    @Update("UPDATE occupied_seats SET place_number = #{place} WHERE id_passenger = #{passenger.id}")
    void changeSeat(@Param("passenger") Passenger passenger, @Param("place") String placeNumber);

    @Delete("DELETE FROM ticket WHERE id = #{orderId}")
    void deleteTicket(@Param("orderId") int orderId);

    @Select("SELECT id_client FROM ticket WHERE id = #{order.id}")
    Integer getClientId(@Param("order") Order order);

    @Insert("INSERT INTO occupied_seats (id_ticket, id_passenger, place_number) VALUES(#{order.id}, #{passenger.id}, #{placeNumber})")
    void takeSeat(@Param("order") Order order, @Param("passenger") Passenger passenger, @Param("placeNumber") int placeNumber);
}
