package io.github.caller79.daterange;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by Carlos Aller on 4/10/19
 */
public interface DateRange {
    boolean contains(LocalDateTime date, ZoneId zoneId);

    boolean overlaps(LocalDateTime startDate, LocalDateTime endDate, ZoneId zoneId);

    Instant getStart(ZoneId zoneId);

    Instant getEnd(ZoneId zoneId);
}
