package com.analysis.mutualFunds.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LastWorkingWeek {
    public static List<String> getLastWorkingWeek() {
        Set<LocalDate> holidays = new HashSet<>();
        holidays.add(LocalDate.of(2024, 1, 26)); // Republic Day, for example
        holidays.add(LocalDate.of(2024, 8, 15)); // Independence Day, for example
        LocalDate lastWorkingDay = findLastWorkingWeek(LocalDate.now(), holidays);
        List<String>lastWorkingWeek = new ArrayList<>();
        int i = 0;
        do{
            lastWorkingWeek.add(lastWorkingDay.minusDays(i).format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            i++;
        }
        while(i<6);
        return lastWorkingWeek;
    }

    public static LocalDate findLastWorkingWeek(LocalDate date, Set<LocalDate> holidays) {
        LocalDate lastWorkingDay = date;
        while (isHolidayOrWeekend(lastWorkingDay, holidays)) {
            lastWorkingDay = lastWorkingDay.minusDays(1);
        }
        return lastWorkingDay;
    }

    public static boolean isHolidayOrWeekend(LocalDate date, Set<LocalDate> holidays) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY || holidays.contains(date);
    }
}

