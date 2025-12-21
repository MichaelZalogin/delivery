package libs.common;

import java.time.*;
import java.time.temporal.ChronoUnit;

public class Timex {
    private static Clock clock = Clock.systemDefaultZone();

    public static LocalDate currentLocalDate() {
        return LocalDate.now(clock);
    }

    public static LocalTime currentLocalTime() {
        return LocalTime.now(clock);
    }

    public static LocalDateTime currentLocalDateTime() {
        return LocalDateTime.now(clock).truncatedTo(ChronoUnit.MICROS);
    }

    public static OffsetDateTime currentOffsetDateTime() {
        return OffsetDateTime.now(clock).truncatedTo(ChronoUnit.MICROS);
    }

    public static ZonedDateTime currentZonedDateTime() {
        return ZonedDateTime.now(clock).truncatedTo(ChronoUnit.MICROS);
    }

    public static Instant currentInstant() {
        return Instant.now(clock).truncatedTo(ChronoUnit.MICROS);
    }

    public static long currentTimeMillis() {
        return Instant.now(clock).toEpochMilli();
    }

    public static void useMockTime(Instant instant, ZoneId zoneId) {
        setMockTime(Clock.fixed(instant, zoneId));
    }

    public static void useMockTime(OffsetDateTime offsetDateTime) {
        setMockTime(Clock.fixed(offsetDateTime.toInstant().truncatedTo(ChronoUnit.MICROS), offsetDateTime.getOffset()));
    }

    public static void useMockTime(ZonedDateTime zonedDateTime) {
        setMockTime(Clock.fixed(zonedDateTime.toInstant().truncatedTo(ChronoUnit.MICROS), zonedDateTime.getZone()));
    }

    public static void useMockTime(Clock clock) {
        setMockTime(clock);
    }

    public static void useSystemDefault() {
        clock = Clock.systemDefaultZone();
    }

    public static Clock clock() {
        return clock;
    }

    private static void setMockTime(Clock val) {
        checkIsJUnitTest();
        clock = val;
    }

    private static void checkIsJUnitTest() {
        for(StackTraceElement element : Thread.currentThread().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) {
                return;
            }
        }

        throw new IllegalStateException("This method is only allowed in unit tests!");
    }
}