package caller79.daterange;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by Carlos Aller on 2/12/19
 */
interface DateRangeFactor {
    boolean isBefore(LocalDateTime date, ZoneId zoneId);

    boolean isAfter(LocalDateTime date, ZoneId zoneId);

    Instant getRepresentedUTCDate();
}
