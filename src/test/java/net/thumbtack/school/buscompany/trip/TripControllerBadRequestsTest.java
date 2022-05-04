package net.thumbtack.school.buscompany.trip;

import net.thumbtack.school.buscompany.BaseTest;
import net.thumbtack.school.buscompany.controller.TripController;
import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.admin.trip.RegisterTripDtoRequest;
import net.thumbtack.school.buscompany.dto.request.admin.trip.ScheduleDtoRequest;
import net.thumbtack.school.buscompany.dto.request.admin.trip.UpdateTripDtoRequest;
import net.thumbtack.school.buscompany.dto.response.admin.RegisterTripDtoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.Cookie;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TripController.class)
public class TripControllerBadRequestsTest extends BaseTest {
    @Test
    public void testBadAddTrip() throws Exception {
        Cookie admin = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        Cookie client = registerClient().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        RegisterTripDtoRequest requestEmpty = new RegisterTripDtoRequest();

        ScheduleDtoRequest schedule = new ScheduleDtoRequest();
        schedule.setFromDate("2022-09-01");
        schedule.setToDate("2022-09-09");
        schedule.setPeriod("daily");
        RegisterTripDtoRequest requestInvalid = new RegisterTripDtoRequest();
        requestInvalid.setSchedule(schedule);
        requestInvalid.setDuration("abc");
        requestInvalid.setPrice(-100);
        requestInvalid.setStart("abc");

        assertInvalidRequest(httpPost("/api/trips", admin, gson.toJson(requestEmpty)), 7);
        assertInvalidRequest(httpPost("/api/trips", admin, gson.toJson(requestInvalid)), 6);

        List<String> dates = new ArrayList<>();
        dates.add("2022-01-01");
        dates.add("2022-01-05");
        RegisterTripDtoRequest datesAndScheduleRequest = new RegisterTripDtoRequest();
        datesAndScheduleRequest.setDates(dates);
        datesAndScheduleRequest.setSchedule(schedule);
        datesAndScheduleRequest.setPrice(500);
        datesAndScheduleRequest.setDuration("07:00");
        datesAndScheduleRequest.setStart("03:00");
        datesAndScheduleRequest.setFromStation("Омск");
        datesAndScheduleRequest.setToStation("Калачинск");
        datesAndScheduleRequest.setBusName("Автобус");

        assertInvalidRequest(httpPost("/api/trips", admin, gson.toJson(datesAndScheduleRequest)), 1);
        assertBadRequest(registerScheduleTrip(client), "OPERATION_NOT_ALLOWED");
    }

    @Test
    public void testBadScheduleAndDates() throws Exception {
        Cookie admin = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        ScheduleDtoRequest invalidRequest = new ScheduleDtoRequest();
        invalidRequest.setToDate("2022-09-01");
        invalidRequest.setFromDate("2022-08-01");
        invalidRequest.setPeriod("dail");

        RegisterTripDtoRequest registerTripDtoRequest = new RegisterTripDtoRequest();
        registerTripDtoRequest.setSchedule(invalidRequest);
        registerTripDtoRequest.setBusName("Автобус");
        registerTripDtoRequest.setDuration("03:00");
        registerTripDtoRequest.setStart("08:00");
        registerTripDtoRequest.setFromStation("Омск");
        registerTripDtoRequest.setToStation("Калачинск");
        registerTripDtoRequest.setPrice(500);

        assertInvalidRequest(httpPost("/api/trips", admin, gson.toJson(registerTripDtoRequest)), 1);

        invalidRequest.setPeriod("Sun, Sun, Sun");
        registerTripDtoRequest.setSchedule(invalidRequest);
        assertInvalidRequest(httpPost("/api/trips", admin, gson.toJson(registerTripDtoRequest)), 1);

        invalidRequest.setPeriod("dfd, ,asdas,das, dsa,dlas");
        registerTripDtoRequest.setSchedule(invalidRequest);
        assertInvalidRequest(httpPost("/api/trips", admin, gson.toJson(registerTripDtoRequest)), 1);

        invalidRequest.setPeriod("Mon, Wen");
        invalidRequest.setFromDate("2022-01-01");
        invalidRequest.setToDate("2022-01-02");
        assertBadRequest(httpPost("/api/trips", admin, gson.toJson(registerTripDtoRequest)), "NO_DATES_ON_THIS_SCHEDULE");

        invalidRequest.setPeriod("1, 2, 3");
        invalidRequest.setFromDate("2022-04-04");
        invalidRequest.setToDate("2022-04-07");
        registerTripDtoRequest.setSchedule(invalidRequest);
        assertBadRequest(httpPost("/api/trips", admin, gson.toJson(registerTripDtoRequest)), "NO_DATES_ON_THIS_SCHEDULE");
    }

    @Test
    public void testBadUpdateTrip() throws Exception {
        Cookie admin = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        int tripId = getContent(registerScheduleTrip(admin), RegisterTripDtoResponse.class).getTripId();

        ScheduleDtoRequest invalidRequest = new ScheduleDtoRequest();
        invalidRequest.setToDate("2022-09-01");
        invalidRequest.setFromDate("2022-08-01");
        invalidRequest.setPeriod("dail");

        UpdateTripDtoRequest updateTripDtoRequest = new UpdateTripDtoRequest();
        updateTripDtoRequest.setSchedule(invalidRequest);
        updateTripDtoRequest.setBusName("Автобус");
        updateTripDtoRequest.setDuration("03:00");
        updateTripDtoRequest.setStart("08:00");
        updateTripDtoRequest.setFromStation("Омск");
        updateTripDtoRequest.setToStation("Калачинск");
        updateTripDtoRequest.setPrice(500);

        assertInvalidRequest(httpPut("/api/trips/" + tripId, admin, gson.toJson(updateTripDtoRequest)), 1);

        invalidRequest.setPeriod("Sun, Sun, Sun");
        updateTripDtoRequest.setSchedule(invalidRequest);
        assertInvalidRequest(httpPut("/api/trips/" + tripId, admin, gson.toJson(updateTripDtoRequest)), 1);

        invalidRequest.setPeriod("dfd, ,asdas,das, dsa,dlas");
        updateTripDtoRequest.setSchedule(invalidRequest);
        assertInvalidRequest(httpPut("/api/trips/" + tripId, admin, gson.toJson(updateTripDtoRequest)), 1);

        invalidRequest.setPeriod("Mon, Wen");
        invalidRequest.setFromDate("2022-01-01");
        invalidRequest.setToDate("2022-01-02");
        assertBadRequest(httpPut("/api/trips/" + tripId, admin, gson.toJson(updateTripDtoRequest)), "NO_DATES_ON_THIS_SCHEDULE");

        invalidRequest.setPeriod("1, 2, 3");
        invalidRequest.setFromDate("2022-04-04");
        invalidRequest.setToDate("2022-04-07");
        updateTripDtoRequest.setSchedule(invalidRequest);
        assertBadRequest(httpPut("/api/trips/" + tripId, admin, gson.toJson(updateTripDtoRequest)), "NO_DATES_ON_THIS_SCHEDULE");

    }

    @Test
    public void testBadDeleteTrip() throws Exception {
        Cookie admin = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        Cookie client = registerClient().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        int tripId = getContent(registerScheduleTrip(admin), RegisterTripDtoResponse.class).getTripId();

        assertBadRequest(httpDelete("/api/trips/10000", admin), "TRIP_NOT_EXISTS");
        assertBadRequest(httpDelete("/api/trips/abc", admin), "INVALID_ID");
        assertBadRequest(httpDelete("/api/trips/" + tripId), "ONLINE_OPERATION");
        assertBadRequest(httpDelete("/api/trips/" + tripId, client), "OPERATION_NOT_ALLOWED");
    }

    @Test
    public void testBadGetTripInfo() throws Exception {
        Cookie admin = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        Cookie client = registerClient().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        int tripId = getContent(registerScheduleTrip(admin), RegisterTripDtoResponse.class).getTripId();

        assertBadRequest(httpGet("/api/trips/10000", admin), "TRIP_NOT_EXISTS");
        assertBadRequest(httpGet("/api/trips/abc", admin), "INVALID_ID");
        assertBadRequest(httpGet("/api/trips/" + tripId), "ONLINE_OPERATION");
        assertBadRequest(httpGet("/api/trips/" + tripId, client), "OPERATION_NOT_ALLOWED");
    }

    @Test
    public void testBadApproveTrip() throws Exception {
        Cookie admin = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        Cookie client = registerClient().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        int tripId = getContent(registerScheduleTrip(admin), RegisterTripDtoResponse.class).getTripId();

        assertBadRequest(httpPost("/api/trips/10000/approve", admin), "TRIP_NOT_EXISTS");
        assertBadRequest(httpPost("/api/trips/abc/approve", admin), "INVALID_ID");
        assertBadRequest(httpPost("/api/trips/" + tripId + "/approve"), "ONLINE_OPERATION");
        assertBadRequest(httpPost("/api/trips/" + tripId + "/approve", client), "OPERATION_NOT_ALLOWED");
    }

    @Test
    public void testBadGetAllTrips() throws Exception {
        Cookie admin = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        registerScheduleTrip(admin);
        registerDatesTrip(admin);

        assertBadRequest(httpGet("/api/trips"), "ONLINE_OPERATION");
        assertBadRequest(httpGet("/api/trips?fromDate=456", admin), "INVALID_DATE");
        assertBadRequest(httpGet("/api/trips?toDate=456", admin), "INVALID_DATE");
    }
}
