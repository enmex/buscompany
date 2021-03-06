package net.thumbtack.school.buscompany.trip;

import net.thumbtack.school.buscompany.BaseTest;
import net.thumbtack.school.buscompany.controller.TripController;
import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.admin.trip.ScheduleDtoRequest;
import net.thumbtack.school.buscompany.dto.request.admin.trip.UpdateTripDtoRequest;
import net.thumbtack.school.buscompany.dto.response.admin.ApproveTripDtoResponse;
import net.thumbtack.school.buscompany.dto.response.admin.GetTripProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.admin.RegisterTripDtoResponse;
import net.thumbtack.school.buscompany.dto.response.admin.UpdateTripDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.trip.GetTripsDtoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TripController.class)
public class TripControllerOperationsTest extends BaseTest {

    @Test
    public void testAddTripWithSchedule() throws Exception {
        Cookie cookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        MvcResult result = registerScheduleTrip(cookie);
        RegisterTripDtoResponse response = getContent(result, RegisterTripDtoResponse.class);
        assertEquals(200, result.getResponse().getStatus());
        assertEquals("Автобус", response.getBus().getBusName());
        assertTrue(response.getBus().getPlaceCount() > 0);
        assertFalse(response.isApproved());
        assertEquals("Омск", response.getFromStation());
        assertEquals("Калачинск", response.getToStation());
        assertEquals("07:00", response.getStart());
        assertEquals("03:00", response.getDuration());
        assertEquals(9, response.getDates().size());
    }

    @Test
    public void testAddTripWithDates() throws Exception {
        Cookie cookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        MvcResult result = registerDatesTrip(cookie);
        RegisterTripDtoResponse response = getContent(result, RegisterTripDtoResponse.class);
        assertEquals(200, result.getResponse().getStatus());
        assertEquals("Автобус", response.getBus().getBusName());
        assertTrue(response.getBus().getPlaceCount() > 0);
        assertFalse(response.isApproved());
        assertEquals("Омск", response.getFromStation());
        assertEquals("Калачинск", response.getToStation());
        assertEquals("07:00", response.getStart());
        assertEquals("03:00", response.getDuration());
        assertEquals(4, response.getDates().size());
        assertNull(response.getSchedule());
    }

    @Test
    public void testUpdateTripWithSchedule() throws Exception {
        Cookie cookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        int tripId = getContent(registerScheduleTrip(cookie),
                                    RegisterTripDtoResponse.class).getTripId();

        ScheduleDtoRequest scheduleDtoRequest = new ScheduleDtoRequest(
                "2022-01-01",
                "2022-01-08",
                "odd"
        );

        UpdateTripDtoRequest request = new UpdateTripDtoRequest();
        request.setPrice(1000);
        request.setFromStation("Омск");
        request.setToStation("Новосибирск");
        request.setBusName("Автобус");
        request.setStart("09:00");
        request.setDuration("18:00");
        request.setSchedule(scheduleDtoRequest);

        MvcResult result = httpPut("/api/trips/" + tripId, cookie, gson.toJson(request));
        UpdateTripDtoResponse response = getContent(result, UpdateTripDtoResponse.class);

        assertEquals(200, result.getResponse().getStatus());
        assertEquals(1000, response.getPrice());
        assertEquals("Омск", response.getFromStation());
        assertEquals("Новосибирск", response.getToStation());
        assertEquals("Автобус", response.getBus().getBusName());
        assertEquals("09:00", response.getStart());
        assertEquals("18:00", response.getDuration());
        assertFalse(response.isApproved());
        assertEquals("2022-01-01", response.getSchedule().getFromDate());
        assertEquals("2022-01-08", response.getSchedule().getToDate());
        assertEquals("odd", response.getSchedule().getPeriod());
        assertEquals(4, response.getDates().size());
    }

    @Test
    public void testUpdateTripWithDates() throws Exception {
        Cookie cookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        int tripId = getContent(registerDatesTrip(cookie),
                RegisterTripDtoResponse.class).getTripId();

        List<String> dates = new ArrayList<>();
        dates.add("2022-01-01");
        dates.add("2022-01-02");

        UpdateTripDtoRequest request = new UpdateTripDtoRequest();
        request.setPrice(1000);
        request.setFromStation("Омск");
        request.setToStation("Новосибирск");
        request.setBusName("Автобус");
        request.setStart("09:00");
        request.setDuration("18:00");
        request.setDates(dates);

        MvcResult result = httpPut("/api/trips/" + tripId, cookie, gson.toJson(request));
        UpdateTripDtoResponse response = getContent(result, UpdateTripDtoResponse.class);

        assertEquals(200, result.getResponse().getStatus());
        assertEquals(1000, response.getPrice());
        assertEquals("Омск", response.getFromStation());
        assertEquals("Новосибирск", response.getToStation());
        assertEquals("Автобус", response.getBus().getBusName());
        assertEquals("09:00", response.getStart());
        assertEquals("18:00", response.getDuration());
        assertFalse(response.isApproved());
        assertTrue(response.getDates().contains("2022-01-01"));
        assertTrue(response.getDates().contains("2022-01-02"));
    }

    @Test
    public void testGetTripWithScheduleInfo() throws Exception {
        Cookie cookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        int tripId = getContent(registerScheduleTrip(cookie),
                RegisterTripDtoResponse.class).getTripId();

        MvcResult result = httpGet("/api/trips/" + tripId, cookie);
        GetTripProfileDtoResponse response = getContent(result, GetTripProfileDtoResponse.class);

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("Автобус", response.getBus().getBusName());
        assertEquals("Омск", response.getFromStation());
        assertEquals("Калачинск", response.getToStation());
        assertEquals("07:00", response.getStart());
        assertEquals("03:00", response.getDuration());
        assertNotNull(response.getSchedule());
        assertEquals(9, response.getDates().size());
    }

    @Test
    public void testGetTripWithDatesInfo() throws Exception {
        Cookie cookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        int tripId = getContent(registerDatesTrip(cookie),
                RegisterTripDtoResponse.class).getTripId();

        MvcResult result = httpGet("/api/trips/" + tripId, cookie);
        GetTripProfileDtoResponse response = getContent(result, GetTripProfileDtoResponse.class);

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("Автобус", response.getBus().getBusName());
        assertEquals("Омск", response.getFromStation());
        assertEquals("Калачинск", response.getToStation());
        assertEquals("07:00", response.getStart());
        assertEquals("03:00", response.getDuration());
        assertNull(response.getSchedule());
        assertEquals(4, response.getDates().size());
    }

    @Test
    public void testDeleteTrip() throws Exception {
        Cookie cookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        int datesTripId = getContent(registerDatesTrip(cookie),
                RegisterTripDtoResponse.class).getTripId();
        int scheduleTripId = getContent(registerScheduleTrip(cookie),
                RegisterTripDtoResponse.class).getTripId();

        MvcResult deleteScheduleTrip = httpDelete("/api/trips/" + scheduleTripId, cookie);
        MvcResult deleteDatesResult = httpDelete("/api/trips/" + datesTripId, cookie);

        assertEquals(200, deleteDatesResult.getResponse().getStatus());
        assertEquals(200, deleteScheduleTrip.getResponse().getStatus());
    }

    @Test
    public void testApproveTrip() throws Exception {
        Cookie cookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        int datesTripId = getContent(registerDatesTrip(cookie),
                RegisterTripDtoResponse.class).getTripId();
        int scheduleTripId = getContent(registerScheduleTrip(cookie),
                RegisterTripDtoResponse.class).getTripId();

        MvcResult approveDatesTrip = httpPost("/api/trips/" + datesTripId + "/approve", cookie);
        MvcResult approveScheduleTrip = httpPost("/api/trips/" + scheduleTripId + "/approve", cookie);

        ApproveTripDtoResponse response1 = getContent(approveDatesTrip, ApproveTripDtoResponse.class);
        ApproveTripDtoResponse response2 = getContent(approveScheduleTrip, ApproveTripDtoResponse.class);

        assertEquals(200, approveDatesTrip.getResponse().getStatus());
        assertEquals(200, approveScheduleTrip.getResponse().getStatus());
        assertTrue(response1.isApproved());
        assertTrue(response2.isApproved());
    }

    @Test
    public void testGetAllTrips() throws Exception {
        Cookie cookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        getContent(registerDatesTrip(cookie),
                RegisterTripDtoResponse.class);
        getContent(registerScheduleTrip(cookie),
                RegisterTripDtoResponse.class);

        List<String> datesStrings = new ArrayList<>();
        datesStrings.add("2022-01-01");
        datesStrings.add("2022-01-02");

        registerDatesTrip(cookie, "Автобус", "Москва", "Санкт-Петербург", "10:00", "11:00", datesStrings);

        MvcResult getAll = httpGet("/api/trips", cookie);
        MvcResult getByBus = httpGet("/api/trips?busName=Автобус", cookie);
        MvcResult getFromStation = httpGet("/api/trips?fromStation=Москва", cookie);
        MvcResult getToStation = httpGet("/api/trips?toStation=Санкт-Петербург", cookie);
        MvcResult getByStart = httpGet("/api/trips?start=10:00", cookie);
        MvcResult getByDuration = httpGet("/api/trips?start=11:00", cookie);
        MvcResult getFromDate = httpGet("/api/trips?fromDate=2022-01-01", cookie);
        MvcResult getToDate = httpGet("/api/trips?toDate=2022-01-02", cookie);
        
        GetTripsDtoResponse response = getContent(getAll, GetTripsDtoResponse.class);
        GetTripsDtoResponse responseByBus = getContent(getByBus, GetTripsDtoResponse.class);
        GetTripsDtoResponse responseFromStation = getContent(getFromStation, GetTripsDtoResponse.class);
        GetTripsDtoResponse responseToStation = getContent(getToStation, GetTripsDtoResponse.class);
        GetTripsDtoResponse responseStart = getContent(getByStart, GetTripsDtoResponse.class);
        GetTripsDtoResponse responseDuration = getContent(getByDuration, GetTripsDtoResponse.class);
        GetTripsDtoResponse responseFromDate = getContent(getFromDate, GetTripsDtoResponse.class);
        GetTripsDtoResponse responseToDate = getContent(getToDate, GetTripsDtoResponse.class);

        assertEquals(200, getAll.getResponse().getStatus());
        assertEquals(200, getByBus.getResponse().getStatus());
        assertEquals(200, getFromStation.getResponse().getStatus());
        assertEquals(200, getToStation.getResponse().getStatus());
        assertEquals(200, getByStart.getResponse().getStatus());
        assertEquals(200, getByDuration.getResponse().getStatus());
        assertEquals(200, getFromDate.getResponse().getStatus());
        assertEquals(200, getToDate.getResponse().getStatus());
        assertEquals(3, response.getTrips().size());
        assertEquals(3, responseByBus.getTrips().size());
        assertEquals(1, responseFromStation.getTrips().size());
        assertEquals(1, responseToStation.getTrips().size());
        assertEquals(3, responseStart.getTrips().size());
        assertEquals(3, responseDuration.getTrips().size());
        assertEquals(3, responseFromDate.getTrips().size());
        assertEquals(1, responseToDate.getTrips().size());

        logout(cookie);

        Cookie client = registerClient().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        GetTripsDtoResponse getAllResponseClient = getContent(httpGet("/api/trips", client), GetTripsDtoResponse.class);

        assertEquals(0, getAllResponseClient.getTrips().size());
    }
}
