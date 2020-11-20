package caller79.daterange;

import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * Created by Carlos Aller on 4/10/19
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
public class DateRangeFactoryTest {
    @Test
    public void testBasicNumericParsing() {
        ZoneId serverTimezone = ZoneId.of("UTC");
        LocalDateTime today = LocalDateTime.of(2019, 2, 23, 0, 30, 0);
        Instant now = today.toInstant(serverTimezone.getRules().getOffset(today));

        DateRangeFactory dateRangeFactory = DateRangeFactory
            .builder()
            .currentTimestamp(now.getEpochSecond() * 1000)
            .build();


        ZoneId eventZone = ZoneId.of("America/New_York");

        DateRange range = dateRangeFactory.parseRange("0,30d");
        long start = 1550881800; // Saturday, 23 February 2019 0:30:00 UTC
        Assert.assertEquals(range.getUTCStart().getEpochSecond(), start);
        Assert.assertEquals(range.getUTCEnd().getEpochSecond(), start + 24 * 3600 * 30);

        // In NY, it is February 22nd at 19:30, so an event at 19:28 is not in the range 0,30d
        LocalDateTime eventDate = LocalDateTime.of(2019, 2, 22, 19, 28, 0);
        Assert.assertFalse(range.contains(eventDate, eventZone));
        Assert.assertTrue(range.intersects(eventDate, eventDate.plus(1, ChronoUnit.DAYS), eventZone));
        Assert.assertFalse(range.intersects(eventDate.minus(1, ChronoUnit.DAYS), eventDate, eventZone));

        // In NY, it is February 22nd at 19:30, so an event at 19:32 is in the range 0,30d
        eventDate = LocalDateTime.of(2019, 2, 22, 19, 32, 0);
        Assert.assertTrue(range.contains(eventDate, eventZone));

        range = dateRangeFactory.parseRange("-7,1d");
        // In NY, it is February 22nd at 19:30, so an event at 19:32 is in the range -7,1d
        eventDate = LocalDateTime.of(2019, 2, 23, 19, 28, 0);
        Assert.assertTrue(range.contains(eventDate, eventZone));

        // In NY, it is February 22nd at 19:30, so an event at 19:32 is not in the range -7,1d
        eventDate = LocalDateTime.of(2019, 2, 23, 19, 32, 0);
        Assert.assertFalse(range.contains(eventDate, eventZone));

        range = dateRangeFactory.parseRange(",0");

        // In NY, it is February 22nd at 19:30, so an event at 19:32 is in the range ,0
        eventDate = LocalDateTime.of(2019, 2, 22, 19, 28, 0);
        Assert.assertTrue(range.contains(eventDate, eventZone));

        // In NY, it is February 22nd at 19:30, so an event at 19:32 is not in the range ,0
        eventDate = LocalDateTime.of(2019, 2, 22, 19, 32, 0);
        Assert.assertFalse(range.contains(eventDate, eventZone));

        range = dateRangeFactory.parseRange("0,");

        // In NY, it is February 22nd at 19:30, so an event at 19:32 is not in the range 0,
        eventDate = LocalDateTime.of(2019, 2, 22, 19, 28, 0);
        Assert.assertFalse(range.contains(eventDate, eventZone));

        // In NY, it is February 22nd at 19:30, so an event at 19:32 is in the range 0,
        eventDate = LocalDateTime.of(2019, 2, 22, 19, 32, 0);
        Assert.assertTrue(range.contains(eventDate, eventZone));

        range = dateRangeFactory.parseRange(",");

        // In NY, it is February 22nd at 19:30, so an event at 19:32 is in the range 0,
        eventDate = LocalDateTime.of(2019, 2, 22, 19, 28, 0);
        Assert.assertTrue(range.contains(eventDate, eventZone));

        // In NY, it is February 22nd at 19:30, so an event at 19:32 is in the range 0,
        eventDate = LocalDateTime.of(2019, 2, 22, 19, 32, 0);
        Assert.assertTrue(range.contains(eventDate, eventZone));

        range = dateRangeFactory.parseRange("");
        // In NY, it is February 22nd at 19:30, so an event at 19:32 is in the range 0,
        eventDate = LocalDateTime.of(2019, 2, 22, 19, 28, 0);
        Assert.assertTrue(range.contains(eventDate, eventZone));

        // In NY, it is February 22nd at 19:30, so an event at 19:32 is in the range 0,
        eventDate = LocalDateTime.of(2019, 2, 22, 19, 32, 0);
        Assert.assertTrue(range.contains(eventDate, eventZone));
    }

    @Test
    public void testBasicDateParsing() {
        ZoneId serverTimezone = ZoneId.of("UTC");
        LocalDateTime today = LocalDateTime.of(2019, 2, 23, 0, 30, 0);
        Instant now = today.toInstant(serverTimezone.getRules().getOffset(today));

        DateRangeFactory dateRangeFactory = DateRangeFactory
            .builder()
            .currentTimestamp(now.getEpochSecond() * 1000)
            .build();

        ZoneId eventZone = ZoneId.of("America/New_York");
        DateRange range = dateRangeFactory.parseRange("2019-02-22,0d");
        long start = 1550793600; // Friday, 22 February 2019 0:00:00 0:30:00 UTC
        long end = 1550881800; // Saturday, 23 February 2019 0:30:00 UTC
        Assert.assertEquals(range.getUTCStart().getEpochSecond(), start);
        Assert.assertEquals(range.getUTCEnd().getEpochSecond(), end);


        // In NY, it is February 22nd at 19:30, so an event at 19:28 is in the range 2019-02-23,1d
        LocalDateTime eventDate = LocalDateTime.of(2019, 2, 22, 19, 28, 0);
        Assert.assertTrue(range.contains(eventDate, eventZone));

        range = dateRangeFactory.parseRange("2019-02-22 00:00:00,0d");
        // In NY, it is February 22nd at 19:30, so an event at 19:28 is in the range 2019-02-23 00:00:00,1d
        eventDate = LocalDateTime.of(2019, 2, 22, 19, 28, 0);
        Assert.assertTrue(range.contains(eventDate, eventZone));

        range = dateRangeFactory.parseRange("2019-02-23 00:00:00,");
        // In NY, it is February 22nd at 19:30, so an event at 19:28 is in the range 2019-02-23 00:00:00,
        eventDate = LocalDateTime.of(2019, 2, 23, 0, 28, 0);
        Assert.assertTrue(range.contains(eventDate, eventZone));

        range = dateRangeFactory.parseRange(",2019-02-23 00:30:00");
        // In NY, it is February 22nd at 19:30, so an event at 19:28 is in the range ,2019-02-23 00:30:00
        eventDate = LocalDateTime.of(2019, 2, 23, 0, 28, 0);
        Assert.assertTrue(range.contains(eventDate, eventZone));

        range = dateRangeFactory.parseRange(",2019-02-23 00:30:00");
        // In NY, it is February 22nd at 19:30, so an event at 19:28 is in the range ,2019-02-23 00:30:00
        eventDate = LocalDateTime.of(2019, 2, 23, 0, 31, 0);
        Assert.assertFalse(range.contains(eventDate, eventZone));
    }

    @Test
    public void testIntersections() {
        ZoneId serverTimezone = ZoneId.of("UTC");
        LocalDateTime today = LocalDateTime.of(2019, 2, 23, 0, 30, 0);
        Instant now = today.toInstant(serverTimezone.getRules().getOffset(today));

        DateRangeFactory dateRangeFactory = DateRangeFactory
            .builder()
            .currentTimestamp(now.getEpochSecond() * 1000)
            .build();

        ZoneId eventZone = ZoneId.of("UTC");
        DateRange range = dateRangeFactory.parseRange("0,1h");
        Assert.assertFalse(
            range.intersects(
                LocalDateTime.of(2019, 2, 23, 0, 0, 0),
                LocalDateTime.of(2019, 2, 23, 0, 29, 0),
                eventZone));
        Assert.assertTrue(
            range.intersects(
                LocalDateTime.of(2019, 2, 23, 0, 0, 0),
                LocalDateTime.of(2019, 2, 23, 0, 30, 0),
                eventZone));
        Assert.assertTrue(
            range.intersects(
                LocalDateTime.of(2019, 2, 23, 0, 0, 0),
                LocalDateTime.of(2019, 2, 23, 0, 31, 0),
                eventZone));
        Assert.assertTrue(
            range.intersects(
                LocalDateTime.of(2019, 2, 23, 0, 31, 0),
                LocalDateTime.of(2019, 2, 23, 0, 34, 0),
                eventZone));
        Assert.assertTrue(
            range.intersects(
                LocalDateTime.of(2019, 2, 23, 1, 29, 0),
                LocalDateTime.of(2019, 2, 23, 1, 34, 0),
                eventZone));
        Assert.assertTrue(
            range.intersects(
                LocalDateTime.of(2019, 2, 23, 1, 30, 0),
                LocalDateTime.of(2019, 2, 23, 1, 34, 0),
                eventZone));
        Assert.assertFalse(
            range.intersects(
                LocalDateTime.of(2019, 2, 23, 1, 31, 0),
                LocalDateTime.of(2019, 2, 23, 1, 34, 0),
                eventZone));

        range = dateRangeFactory.parseRange(",");
        Assert.assertTrue(
            range.intersects(
                LocalDateTime.of(2019, 2, 23, 0, 0, 0),
                LocalDateTime.of(2019, 2, 23, 0, 29, 0),
                eventZone));
    }

    @Test
    public void testTruncatedTimes() {
        ZoneId serverTimezone = ZoneId.of("UTC");
        LocalDateTime today = LocalDateTime.of(2019, 2, 23, 12, 30, 0);
        Instant now = today.toInstant(serverTimezone.getRules().getOffset(today));

        DateRangeFactory dateRangeFactory = DateRangeFactory
            .builder()
            .currentTimestamp(now.getEpochSecond() * 1000)
            .build();


        ZoneId eventZone = ZoneId.of("America/New_York");

        DateRange range = dateRangeFactory.parseRange("0,1d|");
        // In NY, it is February 23nd at 6:30
        LocalDateTime eventDate = LocalDateTime.of(2019, 2, 23, 7, 28, 0);
        Assert.assertFalse(range.contains(eventDate, eventZone));

        eventDate = LocalDateTime.of(2019, 2, 23, 7, 32, 0);
        Assert.assertTrue(range.contains(eventDate, eventZone));

        eventDate = LocalDateTime.of(2019, 2, 24, 0, 1, 0);
        Assert.assertFalse(range.contains(eventDate, eventZone));
    }
}
