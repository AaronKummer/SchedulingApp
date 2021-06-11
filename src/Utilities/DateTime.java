package Utilities;

import Models.Appointment;
import javafx.util.converter.LocalDateTimeStringConverter;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author Aaron Kummer
 * helper methods for date time conversions etc
 */
public class DateTime {
    public static DateTimeFormatter commonDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     *
     * @return
     */
    public static ZoneId getZoneId() {
        return ZoneId.systemDefault();
    }

    /**
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDay(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate localDate2 = date2.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDate1.isEqual(localDate2);
    }

    /**
     *
     * @param locDateTime
     * @return
     */
    public static Timestamp convertToDatabaseColumn(LocalDateTime locDateTime) {
        return locDateTime == null ? null : Timestamp.valueOf(locDateTime);
    }

    /**
     *
     * @param sqlTimestamp
     * @return
     */
    public static LocalDateTime convertToEntityAttribute(Timestamp sqlTimestamp) {
        return sqlTimestamp == null ? null : sqlTimestamp.toLocalDateTime();
    }

    /**
     *
     * @param date
     * @return
     */
    public static java.sql.Timestamp getTimeStampForLocalDate(LocalDateTime date) {
        ZoneId zoneid = ZoneId.of("UTC");
        LocalDateTime localDateTime = LocalDateTime.from(date.atZone(zoneid));
        java.sql.Timestamp timeStamp = Timestamp.valueOf(localDateTime);
        return timeStamp;
    }

    /**
     *
     * @return
     */
    public static java.sql.Timestamp getNowTimeStamp() {
        ZoneId zoneid = ZoneId.of("UTC");
        LocalDateTime localDateTime = LocalDateTime.now(zoneid);
        java.sql.Timestamp timeStamp = Timestamp.valueOf(localDateTime);
        return timeStamp;
    }

    /**
     *
     * @return
     */
    public static java.sql.Date getDate() {
        java.sql.Date date = java.sql.Date.valueOf(LocalDate.now());
        return date;
    }

    /**
     *
     * @param ldt
     * @return
     */
    public static String localToUtc(LocalDateTime ldt) {
        ZonedDateTime ldtZoned = ldt.atZone(ZoneId.systemDefault());
        return ldtZoned.withZoneSameInstant(ZoneId.of("UTC")).toString();
    }

    /**
     *
     * @param time
     * @param date
     * @return
     */
    public static LocalDateTime parseTimeTextField(String time, LocalDate date) {
        if (!time.isBlank()) {
            if (time.contains("am") || time.contains("AM") || time.contains("pm") || time.contains("PM")) {
                try {
                    LocalTime startTime = LocalTime.parse(time.toUpperCase(Locale.ROOT), DateTimeFormatter.ofPattern("H:mm a"));
                    return LocalDateTime.of(date, startTime);
                } catch (Exception e) {
                    try {
                        LocalTime startTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("hh:mm a"));
                        return LocalDateTime.of(date, startTime);
                    } catch(Exception x) {
                        LocalTime startTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("hh:mm aa"));
                        return LocalDateTime.of(date, startTime);
                    }

                }
            } else {
                try {
                    LocalTime startTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("H:mm"));
                    return LocalDateTime.of(date, startTime);
                } catch (Exception e) {

                }
            }
        }

        return null;
    }

    public static String ToLocalTime(String time) {
        return time;
    }

    /**
     *
     * @param aptTime
     * @return
     */
    public static boolean isToday(LocalDate aptTime) {
        // aptTime has already been converted to localized date with zoneId of user
        if (aptTime.getYear() == LocalDate.now(DateTime.getZoneId()).getYear()) {
            if (aptTime.getDayOfYear() == LocalDate.now(DateTime.getZoneId()).getDayOfYear()) {
                return true;
            }
        }
        return  false;
    }
}
