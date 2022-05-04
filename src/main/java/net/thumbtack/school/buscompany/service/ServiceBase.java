package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dao.AdminDao;
import net.thumbtack.school.buscompany.dao.BaseDao;
import net.thumbtack.school.buscompany.dao.ClientDao;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.daoimpl.BaseDaoImpl;
import net.thumbtack.school.buscompany.exception.CheckedException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ServiceBase {
    private final Map<String, DayOfWeek> dayOfWeekMap;

    protected final UserDao userDao;
    protected final AdminDao adminDao;
    protected final ClientDao clientDao;

    protected final BaseDao baseDao = new BaseDaoImpl();

    @Value("${buscompany.user_idle_timeout}")
    protected long cookieMaxAge;

    protected ServiceBase(UserDao userDao, AdminDao adminDao, ClientDao clientDao){
        this.userDao = userDao;
        this.adminDao = adminDao;
        this.clientDao = clientDao;

        dayOfWeekMap = new HashMap<>(7);
        dayOfWeekMap.put("Sun", DayOfWeek.SUNDAY);
        dayOfWeekMap.put("Mon", DayOfWeek.MONDAY);
        dayOfWeekMap.put("Tue", DayOfWeek.TUESDAY);
        dayOfWeekMap.put("Wen", DayOfWeek.WEDNESDAY);
        dayOfWeekMap.put("Thu", DayOfWeek.THURSDAY);
        dayOfWeekMap.put("Fri", DayOfWeek.FRIDAY);
        dayOfWeekMap.put("Sat", DayOfWeek.SATURDAY);
    }

    protected void refreshSession(String cookieValue) {
        userDao.updateSession(cookieValue);
    }

    protected Admin getAdmin(String cookieValue){
        if(cookieValue == null){
            throw new CheckedException(ErrorCode.ONLINE_OPERATION);
        }
        refreshSession(cookieValue);

        User user = userDao.getBySession(cookieValue);

        if(user.getUserType() != UserType.ADMIN){
            throw new CheckedException(ErrorCode.OPERATION_NOT_ALLOWED);
        }

        return (Admin) user;
    }

    protected Client getClient(String cookieValue){
        if(cookieValue == null){
            throw new CheckedException(ErrorCode.ONLINE_OPERATION);
        }
        refreshSession(cookieValue);

        User user = userDao.getBySession(cookieValue);

        if(user.getUserType() != UserType.CLIENT){
            throw new CheckedException(ErrorCode.OPERATION_NOT_ALLOWED);
        }

        return (Client) user;
    }
    protected List<String> formatDates(List<TripDate> dates){
        List<String> datesString = new ArrayList<>(dates.size());

        for(TripDate date : dates){
            datesString.add(date.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        return datesString;
    }

    protected List<TripDate> parseDates(List<String> dates){
        List<TripDate> dateList = new ArrayList<>(dates.size());

        for(String date : dates){
            dateList.add(new TripDate(0, LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        }

        return dateList;
    }

    protected List<LocalDate> createDates(Schedule schedule){
        List<LocalDate> dates = new ArrayList<>();

        if (schedule.getPeriod().equals("daily")) {
            for (LocalDate date = schedule.getFromDate();
                 !date.isAfter(schedule.getToDate());
                 date = date.plus(1, ChronoUnit.DAYS)) {
                dates.add(date);
            }

            if(dates.size() == 0) {
                throw new CheckedException(ErrorCode.NO_DATES_ON_THIS_SCHEDULE);
            }

            return dates;
        }

        if(schedule.getPeriod().equals("odd")){
            for (LocalDate date = schedule.getFromDate();
                 !date.isAfter(schedule.getToDate());
                 date = date.plus(1, ChronoUnit.DAYS)) {
                if(date.getDayOfMonth() % 2 == 1) {
                    dates.add(date);
                }
            }

            if(dates.size() == 0) {
                throw new CheckedException(ErrorCode.NO_DATES_ON_THIS_SCHEDULE);
            }

            return dates;
        }

        if(schedule.getPeriod().equals("even")){
            for (LocalDate date = schedule.getFromDate();
                 !date.isAfter(schedule.getToDate());
                 date = date.plus(1, ChronoUnit.DAYS)) {
                if(date.getDayOfMonth() % 2 == 0) {
                    dates.add(date);
                }
            }

            if(dates.size() == 0) {
                throw new CheckedException(ErrorCode.NO_DATES_ON_THIS_SCHEDULE);
            }

            return dates;
        }

        String[] daysOfPeriod = schedule.getPeriod().split(",\\s+");

        if(isDaysOfWeek(daysOfPeriod)) {

            for (LocalDate date = schedule.getFromDate(); !date.isAfter(schedule.getToDate()); date = date.plus(1, ChronoUnit.DAYS)) {
                if(dayOfWeeksMatch(date, daysOfPeriod)) {
                    dates.add(date);
                }
            }

        }
        else{
            List<Integer> days = new ArrayList<>(daysOfPeriod.length);
            for(String day : daysOfPeriod){
                days.add(Integer.parseInt(day));
            }

            for(LocalDate date = schedule.getFromDate();
                !date.isAfter(schedule.getToDate());
                date = date.plus(1, ChronoUnit.DAYS)) {

                if(days.contains(date.getDayOfMonth())){
                    dates.add(date);
                }
            }
        }

        if(dates.size() == 0) {
            throw new CheckedException(ErrorCode.NO_DATES_ON_THIS_SCHEDULE);
        }

        return dates;
    }

    protected boolean isDaysOfWeek(String[] days){
        return Arrays.stream(days).allMatch(str -> dayOfWeekMap.get(str) != null);
    }

    protected boolean dayOfWeeksMatch(LocalDate date, String[] days){
        for(String day : days){
            DayOfWeek dayOfWeek = dayOfWeekMap.get(day);

            if(date.getDayOfWeek().equals(dayOfWeek)){
                return true;
            }
        }
        return false;
    }

    protected LocalDate parseDate(String date) {
        return LocalDate.parse(date);
    }

    protected LocalTime parseTime(String time){
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
    }

    protected ResponseCookie createJavaSessionIdCookie(String cookieValue){
        return ResponseCookie.from(BusCompanyCookies.JAVASESSIONID, cookieValue).maxAge(cookieMaxAge).build();
    }

    protected ResponseCookie deleteJavaSessionCookie(String cookieValue){
        return ResponseCookie.from(BusCompanyCookies.JAVASESSIONID, "").maxAge(0L).build();
    }

    protected boolean inBetween(LocalDate date, List<TripDate> dates){
        return date.isAfter(dates.get(0).getDate()) && dates.get(dates.size() - 1).getDate().isAfter(date);
    }
}
