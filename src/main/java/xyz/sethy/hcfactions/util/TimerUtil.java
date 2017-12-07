package xyz.sethy.hcfactions.util;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Owned by SethyCorp, and KueMedia respectively.
 **/
public class TimerUtil {
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d MMMM YYYY, HH:mm");

    public static String getFormatted(Date date) {
        return FORMATTER.format(date.toInstant().atZone(ZoneId.of("UTC")));
    }

    public static long getTimeFromString(final String a) {
        if (a.endsWith("s"))
            return Long.valueOf(a.substring(0, a.length() - 1)) * 1000L;
        if (a.endsWith("m"))
            return Long.valueOf(a.substring(0, a.length() - 1)) * 60000L;
        if (a.endsWith("h"))
            return Long.valueOf(a.substring(0, a.length() - 1)) * 3600000L;
        if (a.endsWith("d"))
            return Long.valueOf(a.substring(0, a.length() - 1)) * 86400000L;
        if (a.endsWith("m"))
            return Long.valueOf(a.substring(0, a.length() - 1)) * 2592000000L;
        if (a.endsWith("y"))
            return Long.valueOf(a.substring(0, a.length() - 1)) * 31104000000L;
        return -1L;
    }
}
