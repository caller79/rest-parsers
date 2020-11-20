package caller79.daterange;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Created by Carlos Aller on 2/12/19
 */
@lombok.RequiredArgsConstructor
@lombok.Builder
class AbsoluteDateRangeFactor implements DateRangeFactor {
    private final LocalDateTime dateTime;

    @Override
    public boolean isBefore(LocalDateTime date, ZoneId zoneId) {
        return dateTime.isBefore(date);
    }

    @Override
    public boolean isAfter(LocalDateTime date, ZoneId zoneId) {
        return dateTime.isAfter(date);
    }

    @Override
    public Instant getRepresentedDate(ZoneId zoneId) {
        return dateTime.atZone(zoneId).toInstant();
    }

    @Override
    public String toString() {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
