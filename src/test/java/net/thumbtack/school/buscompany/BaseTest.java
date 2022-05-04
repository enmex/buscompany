package net.thumbtack.school.buscompany;

import com.google.gson.Gson;
import net.thumbtack.school.buscompany.dto.request.EmptyDtoRequest;
import net.thumbtack.school.buscompany.dto.request.admin.trip.RegisterTripDtoRequest;
import net.thumbtack.school.buscompany.dto.request.admin.trip.ScheduleDtoRequest;
import net.thumbtack.school.buscompany.dto.request.client.OrderTicketDtoRequest;
import net.thumbtack.school.buscompany.dto.request.client.PassengerDtoRequest;
import net.thumbtack.school.buscompany.dto.request.common.login.LoginDtoRequest;
import net.thumbtack.school.buscompany.dto.request.common.register.RegisterAdminDtoRequest;
import net.thumbtack.school.buscompany.dto.request.common.register.RegisterClientDtoRequest;
import net.thumbtack.school.buscompany.dto.response.error.ErrorDtoResponse;
import net.thumbtack.school.buscompany.model.Passenger;
import net.thumbtack.school.buscompany.service.GlobalErrorHandler;
import net.thumbtack.school.buscompany.util.MyBatisUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
public class BaseTest {
    private static boolean setUpIsDone = false;

    @Autowired
    protected MockMvc mvc;

    protected static Gson gson = new Gson();

    @BeforeAll
    public static void setUp(){
        if (!setUpIsDone) {
            boolean initSqlSessionFactory = MyBatisUtils.initSqlSessionFactory();
            if (!initSqlSessionFactory) {
                throw new RuntimeException("Can't create connection, stop");
            }
            setUpIsDone = true;
        }

    }

    @AfterEach
    public void clearDatabase() throws Exception {
        mvc.perform(post("/api/debug/clear").contentType(MediaType.APPLICATION_JSON)).andReturn();
    }

    protected static <T> T getContent(MvcResult result, Class<T> clazz) throws UnsupportedEncodingException {
        String res = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        return gson.fromJson(result.getResponse().getContentAsString(StandardCharsets.UTF_8), clazz);
    }

    protected MvcResult httpPost(String url) throws Exception {
        return mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(Charset.defaultCharset())).andReturn();
    }

    protected MvcResult httpPost(String url, String json) throws Exception {
        return mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(Charset.defaultCharset())
                .content(json)).andReturn();
    }

    protected MvcResult httpPost(String url, Cookie cookie) throws Exception {
        return mvc.perform(post(url)
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(Charset.defaultCharset())).andReturn();
    }

    protected MvcResult httpPost(String url, Cookie cookie, String json) throws Exception {
        return mvc.perform(post(url)
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(Charset.defaultCharset())
                .content(json)).andReturn();
    }

    protected MvcResult httpPut(String url, Cookie cookie, String json) throws Exception {
        return mvc.perform(put(url)
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(Charset.defaultCharset())
                .content(json)).andReturn();
    }

    protected MvcResult httpPut(String url, String json) throws Exception {
        return mvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(Charset.defaultCharset())
                .content(json)).andReturn();
    }

    protected MvcResult httpGet(String url, Cookie cookie, String json) throws Exception {
        return mvc.perform(get(url)
                        .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(Charset.defaultCharset())
                .content(json)).andReturn();
    }

    protected MvcResult httpGet(String url, Cookie cookie) throws Exception {
        return mvc.perform(get(url)
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(Charset.defaultCharset()))
                .andReturn();
    }

    protected MvcResult httpGet(String url) throws Exception {
        return mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(Charset.defaultCharset())).andReturn();
    }

    protected MvcResult httpDelete(String url, Cookie cookie) throws Exception {
        return mvc.perform(delete(url)
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charset.defaultCharset())
                .content(gson.toJson(new EmptyDtoRequest()))).andReturn();
    }

    protected MvcResult httpDelete(String url) throws Exception {
        return mvc.perform(delete(url)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(Charset.defaultCharset())
                .content("{}")).andReturn();
    }

    protected MvcResult registerOrder(Cookie cookie, int tripId, String date) throws Exception {
        PassengerDtoRequest passenger = new PassengerDtoRequest();
        passenger.setFirstName("Иван");
        passenger.setLastName("Иванов");
        passenger.setPassport("1234567");

        List<PassengerDtoRequest> passengers = new ArrayList<>();
        passengers.add(passenger);

        OrderTicketDtoRequest request = new OrderTicketDtoRequest(
                tripId, date, passengers
        );

        return httpPost("/api/orders", cookie, gson.toJson(request));
    }

    protected MvcResult registerOrder(Cookie cookie, int tripId, String date, List<PassengerDtoRequest> passengers) throws Exception {
        OrderTicketDtoRequest request = new OrderTicketDtoRequest(
                tripId, date, passengers
        );

        return httpPost("/api/orders", cookie, gson.toJson(request));
    }

    protected MvcResult registerScheduleTrip(Cookie cookie, String busName, String from,
                                             String to, String start, String duration,
                                             String fromDate, String toDate, String period) throws Exception {
        ScheduleDtoRequest scheduleDtoRequest = new ScheduleDtoRequest(
                fromDate, toDate, period
        );

        RegisterTripDtoRequest request = new RegisterTripDtoRequest();
        request.setBusName(busName);
        request.setPrice(500);
        request.setFromStation(from);
        request.setToStation(to);
        request.setStart(start);
        request.setDuration(duration);
        request.setSchedule(scheduleDtoRequest);

        return httpPost("/api/trips", cookie, gson.toJson(request));
    }

    protected MvcResult registerScheduleTrip(Cookie cookie) throws Exception {
        return registerScheduleTrip(cookie, "Автобус", "Омск",
                "Калачинск", "07:00", "03:00",
                "2022-09-01", "2022-09-09", "daily");
    }

    protected MvcResult registerDatesTrip(Cookie cookie, String busName, String from,
                                          String to, String start, String duration, List<String> dates) throws Exception {

        RegisterTripDtoRequest request = new RegisterTripDtoRequest();
        request.setBusName(busName);
        request.setPrice(500);
        request.setFromStation(from);
        request.setToStation(to);
        request.setStart(start);
        request.setDuration(duration);
        request.setDates(dates);

        return httpPost("/api/trips", cookie, gson.toJson(request));
    }

    protected MvcResult registerDatesTrip(Cookie cookie) throws Exception {
        List<String> dates = new ArrayList<>();

        dates.add("2022-09-04");
        dates.add("2022-09-08");
        dates.add("2022-10-04");
        dates.add("2022-10-08");

        RegisterTripDtoRequest request = new RegisterTripDtoRequest();
        request.setBusName("Автобус");
        request.setPrice(500);
        request.setFromStation("Омск");
        request.setToStation("Калачинск");
        request.setStart("07:00");
        request.setDuration("03:00");
        request.setDates(dates);

        return httpPost("/api/trips", cookie, gson.toJson(request));
    }

    protected MvcResult registerAdmin() throws Exception {
        RegisterAdminDtoRequest request = new RegisterAdminDtoRequest(
                "Михаил", "Привалов", "Андреевич",
                "Директор", "mich_admin", "123"
        );

        return httpPost("/api/admins", gson.toJson(request));
    }

    protected MvcResult registerAdmin(String firstName, String lastName, String patronymic, String position, String login, String password) throws Exception {
        RegisterAdminDtoRequest request = new RegisterAdminDtoRequest(
                firstName, lastName, patronymic, position, login, password
        );
        String json = gson.toJson(request);


        return httpPost("/api/admins", gson.toJson(request));
    }

    protected MvcResult logout(Cookie cookie) throws Exception {
        return httpDelete("/api/sessions", cookie);
    }

    protected MvcResult registerClient() throws Exception {
        RegisterClientDtoRequest request = new RegisterClientDtoRequest(
                "Михаил", "Привалов", "Андреевич",
                "mikhail@mail.ru", "79998887766",  "mich_client", "123"
        );

        return httpPost("/api/clients", gson.toJson(request));
    }

    protected MvcResult registerClient(String firstName, String lastName, String patronymic,
                                       String email, String phone, String login, String password) throws Exception {
        RegisterClientDtoRequest request = new RegisterClientDtoRequest(
                firstName, lastName, patronymic, email, phone, login, password
        );
        String json = gson.toJson(request);

        return httpPost("/api/clients", gson.toJson(request));
    }


    protected MvcResult login(String login, String password) throws Exception {
        LoginDtoRequest request = new LoginDtoRequest(login, password);

        return httpPost("/api/sessions", gson.toJson(request));
    }

    protected void assertBadRequest(MvcResult result, String expectedErrorCode) throws UnsupportedEncodingException {
        assertEquals(400, result.getResponse().getStatus());
        ErrorDtoResponse response = getContent(result, ErrorDtoResponse.class);

        assertEquals(1, response.getErrors().size());

        GlobalErrorHandler.Error error = response.getErrors().get(0);
        assertEquals(expectedErrorCode, error.getErrorCode());
    }

    protected void assertInvalidRequest(MvcResult result, int errorsNumber) throws UnsupportedEncodingException {
        assertEquals(400, result.getResponse().getStatus());
        ErrorDtoResponse response = getContent(result, ErrorDtoResponse.class);

        assertEquals(errorsNumber, response.getErrors().size());
    }

}
