package caller79.daterange;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by Carlos Aller on 4/10/19
 */
public interface DateRange {
    boolean contains(LocalDateTime date, ZoneId zoneId);

    boolean intersects(LocalDateTime startDate, LocalDateTime endDate, ZoneId zoneId);

    Instant getUTCStart();

    Instant getUTCEnd();
}
