package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dao.AdminDao;
import net.thumbtack.school.buscompany.dao.BaseDao;
import net.thumbtack.school.buscompany.dao.ClientDao;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.daoimpl.BaseDaoImpl;
import net.thumbtack.school.buscompany.model.Schedule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;

import java.text.SimpleDateFormat;
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

    protected List<String> formatDates(List<LocalDate> dates){
        List<String> datesString = new ArrayList<>(dates.size());

        for(LocalDate date : dates){
            datesString.add(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        return datesString;
    }

    protected List<LocalDate> parseDates(List<String> dates){
        List<LocalDate> dateList = new ArrayList<>(dates.size());

        for(String date : dates){
            dateList.add(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        return dateList;
    }

    protected List<LocalDate> createDates(Schedule schedule){
        List<LocalDate> dates = new ArrayList<>();

        if (schedule.getPeriod().equals("daily")) {
            LocalDate currentDate = schedule.getFromDate();
            dates.add(currentDate);

            while(schedule.getToDate().isAfter(dateAfter(currentDate))){
                currentDate = dateAfter(currentDate);
                dates.add(currentDate);
            }
            dates.add(schedule.getToDate());

            return dates;
        }

        if(schedule.getPeriod().equals("odd")){
            LocalDate currentDate = schedule.getFromDate();

            if(!isOddDay(currentDate)){
                currentDate = currentDate.plus(1, ChronoUnit.DAYS);
            }

            do {
                dates.add(currentDate);
                currentDate = currentDate.plus(2, ChronoUnit.DAYS);

                if(!isOddDay(currentDate)){
                    currentDate = currentDate.minus(1, ChronoUnit.DAYS);
                }
            } while(schedule.getToDate().isAfter(currentDate));

            return dates;
        }

        if(schedule.getPeriod().equals("even")){
            LocalDate currentDate = schedule.getFromDate();

            if(isOddDay(currentDate)){
                currentDate = currentDate.plus(1, ChronoUnit.DAYS);
            }

            do {
                dates.add(currentDate);
                currentDate = currentDate.plus(2, ChronoUnit.DAYS);

                if(isOddDay(currentDate)){
                    currentDate = currentDate.minus(1, ChronoUnit.DAYS);
                }
            } while(schedule.getToDate().isAfter(currentDate));

            return dates;
        }

        String[] daysOfPeriod = schedule.getPeriod().split(",\\s+");

        if(isDaysOfWeek(daysOfPeriod)) {
            LocalDate currentDate = schedule.getFromDate();

            while(schedule.getToDate().isAfter(currentDate)) {
                for(String day : daysOfPeriod){
                    DayOfWeek dayOfWeek = dayOfWeekMap.get(day);
                    currentDate = nextDayOfWeekFrom(currentDate, dayOfWeek);
                    dates.add(currentDate);
                }
            }

        }
        else{
            LocalDate currentDate = schedule.getFromDate();
            int addedDays;
            for(int i = 0; i < 1000; i+= addedDays) {
                addedDays = 0;
                for (String day : daysOfPeriod) {
                    int dayOfMonth = Integer.parseInt(day);

                    if(isAfter(currentDate, dayOfMonth)){
                        currentDate = nextDayOfMonth(currentDate, dayOfMonth);
                        dates.add(currentDate);
                        addedDays ++;
                    }

                }
            }
        }

        return dates;
    }

    protected boolean isDaysOfWeek(String[] days){
        return Arrays.stream(days).allMatch(str -> dayOfWeekMap.get(str) != null);
    }

    protected boolean nextDayIsNextMonth(LocalDate from){
        int currentMonth = from.getMonthValue();
        return from.plus(1, ChronoUnit.DAYS).getDayOfMonth() != currentMonth;
    }

    protected LocalDate dateAfter(LocalDate date){
        return date.plus(1, ChronoUnit.DAYS);
    }

    protected boolean isAfter(LocalDate date, int day){
        int currentDay = date.getDayOfMonth();
        return currentDay <= day;
    }

    protected boolean isOddDay(LocalDate date){
        return date.getDayOfMonth() % 2 == 1;
    }

    protected LocalDate nextDayOfWeekFrom(LocalDate date, DayOfWeek day){
        int currentDay = date.getDayOfWeek().getValue();
        int difference = day.getValue() - currentDay;

        if(difference <= 0){
            difference += 7;
        }

        return date.plus(difference, ChronoUnit.DAYS);
    }

    protected LocalDate nextDayOfMonth(LocalDate date, int day){
        int currentDay = date.getDayOfMonth();
        return date.plus(day - currentDay, ChronoUnit.DAYS);
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

    protected boolean inBetween(LocalDate date, List<LocalDate> dates){
        return date.isAfter(dates.get(0)) && dates.get(dates.size() - 1).isAfter(date);
    }
}
