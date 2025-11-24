package com.healthcare.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Date helper used by UI forms.
 */
public final class DateUtil {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private DateUtil() {
    }

    public static String format(LocalDate date) {
        return date == null ? "" : DISPLAY_FORMAT.format(date);
    }

    public static LocalDate parse(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return LocalDate.parse(value, DISPLAY_FORMAT);
    }
}

