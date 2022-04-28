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
            datesString.add(new SimpleDateFormat("yyyy-MM-dd").format(date));
        }
        return datesString;
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

        if(schedule.getPeriod().equals("odd") || schedule.getPeriod().equals("even")){
            LocalDate currentDate = schedule.getFromDate();

            if(isOddDay(currentDate)){
                currentDate = schedule.getFromDate();
            }
            else{
                currentDate = dateAfter(currentDate);
            }

            while(schedule.getToDate().isAfter(dateAfter(currentDate))){
                //  if(dateAfter(currentDate).toInstant().get(ChronoField.DAY_OF_MONTH))
                currentDate = dateAfter(dateAfter(currentDate));
                dates.add(currentDate);
            }

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

    protected ResponseCookie createJavaSessionIdCookie(String cookieValue){
        return ResponseCookie.from(BusCompanyCookies.JAVASESSIONID, cookieValue).maxAge(cookieMaxAge).build();
    }

    protected ResponseCookie deleteJavaSessionCookie(String cookieValue){
        return ResponseCookie.from(BusCompanyCookies.JAVASESSIONID, cookieValue).maxAge(0L).build();
    }
}
