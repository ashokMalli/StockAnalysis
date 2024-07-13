package com.analysis.mutualFunds.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class LastWorkingDay {
    public static String getLastWorkingDate() {
        Set<LocalDate> holidays = new HashSet<>();
        holidays.add(LocalDate.of(2024, 1, 26)); // Republic Day, for example
        holidays.add(LocalDate.of(2024, 8, 15)); // Independence Day, for example
        LocalDate lastWorkingDay = findLastWorkingDay(LocalDate.now().minusDays(1), holidays);
        return lastWorkingDay.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public static LocalDate findLastWorkingDay(LocalDate date, Set<LocalDate> holidays) {
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

