package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.model.Schedule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.*;
import java.util.*;

@Component
public class DateService {

    @Value("${buscompany.displayed_dates_number}")
    private int datesAmount;

    private Map<String, DayOfWeek> dayOfWeekMap;

    public DateService(){
        dayOfWeekMap = new HashMap<>(7);
        dayOfWeekMap.put("Sun", DayOfWeek.SUNDAY);
        dayOfWeekMap.put("Mon", DayOfWeek.MONDAY);
        dayOfWeekMap.put("Tue", DayOfWeek.TUESDAY);
        dayOfWeekMap.put("Wen", DayOfWeek.WEDNESDAY);
        dayOfWeekMap.put("Thu", DayOfWeek.THURSDAY);
        dayOfWeekMap.put("Fri", DayOfWeek.FRIDAY);
        dayOfWeekMap.put("Sat", DayOfWeek.SATURDAY);
    }

    public List<String> createDates(Schedule schedule){
        List<String> dates = new ArrayList<>(datesAmount);

        if (schedule.getPeriod().equals("daily")) {
            Date currentDate = schedule.getFromDate();
            dates.add(formatDate(currentDate));

            for(int i = 1; i < datesAmount && !datesEqual(dateAfter(currentDate), schedule.getToDate()); i++){
                currentDate = dateAfter(currentDate);
                dates.add(formatDate(currentDate));
            }

            return dates;
        }

        if(schedule.getPeriod().equals("odd") || schedule.getPeriod().equals("even")){
            Date currentDate = schedule.getFromDate();

            if(isOddDay(currentDate)){
                currentDate = schedule.getFromDate();
            }
            else{
                currentDate = dateAfter(currentDate);
            }

            for(int i = 1; i < datesAmount && !datesEqual(dateAfter(currentDate), schedule.getToDate()); i++){
                currentDate = dateAfter(dateAfter(currentDate));
                dates.add(formatDate(currentDate));
            }

            return dates;
        }

        String[] daysOfPeriod = schedule.getPeriod().split(",\\s+");

        if(isDaysOfWeek(daysOfPeriod)) {
            Date currentDate = schedule.getFromDate();

            for (int i = 0; i < datesAmount && schedule.getToDate().toInstant().isAfter(currentDate.toInstant()); i += daysOfPeriod.length) {
                for(String day : daysOfPeriod){
                    DayOfWeek dayOfWeek = dayOfWeekMap.get(day);
                    currentDate = nextDayOfWeekFrom(currentDate, dayOfWeek);
                    dates.add(formatDate(currentDate));
                }
            }

        }
        else{
            Date currentDate = schedule.getFromDate();
            int addedDays = 0;
            for(int i = 0; i < datesAmount; i+= addedDays) {
                addedDays = 0;
                for (String day : daysOfPeriod) {
                    int dayOfMonth = Integer.parseInt(day);

                    if(isAfter(currentDate, dayOfMonth)){
                        currentDate = nextDayOfMonth(currentDate, dayOfMonth);
                        dates.add(formatDate(currentDate));
                        addedDays ++;
                    }

                }
            }
        }

        return dates;
    }

    public String formatDate(Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public boolean isDaysOfWeek(String[] days){
        return Arrays.stream(days).allMatch(str -> dayOfWeekMap.get(str) != null);
    }

    private Date dateAfter(Date date){
        return Date.from(date.toInstant().plus(1, ChronoUnit.DAYS));
    }

    private boolean isAfter(Date date, int day){
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int currentDay = localDate.getDayOfMonth();
        return currentDay <= day;
    }

    private boolean isDayOfMonth(Date date, int day){
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int lastDay = localDate.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        return lastDay >= day;
    }

    private boolean isOddDay(Date date){
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.getDayOfMonth() % 2 == 1;
    }

    private boolean datesEqual(Date a, Date b){
        return a.compareTo(b) == 0;
    }

    private Date nextDayOfWeekFrom(Date date, DayOfWeek day){
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int currentDay = localDate.getDayOfWeek().getValue();
        int difference = day.getValue() - currentDay;

        if(difference <= 0){
            difference += 7;
        }

        return Date.from(date.toInstant().plus(difference, ChronoUnit.DAYS));
    }

    private Date nextDayOfMonth(Date date, int day){
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int currentDay = localDate.getDayOfMonth();

        return Date.from(date.toInstant().plus(day - currentDay, ChronoUnit.DAYS));
    }

    private Date nextMonth(Date date){
        Calendar cal = GregorianCalendar.getInstance();
        return null;
    }

    private int getDayOfWeek(Date date){
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.getDayOfWeek().getValue();
    }

    public List<String> createDates(List<Date> dates) {
        List<String> datesString = new ArrayList<>();
        for(Date date : dates){
            datesString.add(formatDate(date));
        }
        return datesString;
    }
}
