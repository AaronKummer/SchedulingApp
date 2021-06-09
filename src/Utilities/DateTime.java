package Utilities;

import javafx.util.converter.LocalDateTimeStringConverter;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateTime {

    public static Timestamp convertToDatabaseColumn(LocalDateTime locDateTime) {
        return locDateTime == null ? null : Timestamp.valueOf(locDateTime);
    }

    public static LocalDateTime convertToEntityAttribute(Timestamp sqlTimestamp) {
        return sqlTimestamp == null ? null : sqlTimestamp.toLocalDateTime();
    }

    public static java.sql.Timestamp getTimeStamp() {
        ZoneId zoneid = ZoneId.of("UTC");
        LocalDateTime localDateTime = LocalDateTime.now(zoneid);
        java.sql.Timestamp timeStamp = Timestamp.valueOf(localDateTime);
        return timeStamp;
    }
    
    public static java.sql.Date getDate() {
        java.sql.Date date = java.sql.Date.valueOf(LocalDate.now());
        return date;
    }

    public static String localToUtc(LocalDateTime ldt) {
        ZonedDateTime ldtZoned = ldt.atZone(ZoneId.systemDefault());
        return ldtZoned.withZoneSameInstant(ZoneId.of("UTC")).toString();
    }

    public static LocalDateTime parseTimeTextField(String time, LocalDate date) {
        if (!time.isBlank()) {
        try {
            LocalTime startTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("H:mm"));
            return LocalDateTime.of(date, startTime);

        } catch (Exception e) {
            e.printStackTrace();
        }
        } else {

        }

        return null;
    }

    public static String ToLocalTime(String time) {
        return time;
    }
}
