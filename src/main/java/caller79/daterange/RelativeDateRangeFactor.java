package caller79.daterange;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * Created by Carlos Aller on 2/12/19
 */
@lombok.RequiredArgsConstructor
@lombok.Builder
class RelativeDateRangeFactor implements DateRangeFactor {
    private final long amount;
    private final ChronoUnit chronoUnit;
    private final Instant now;
    private final boolean truncated;

    private LocalDateTime getLocalDateTime(LocalDateTime date, ZoneId zoneId) {
        LocalDateTime nowTime = now.plus(amount, chronoUnit).atOffset(zoneId.getRules().getOffset(date)).toLocalDateTime();
        return truncated ? nowTime.truncatedTo(chronoUnit) : nowTime;
    }

    @Override
    public boolean isBefore(LocalDateTime date, ZoneId zoneId) {
        LocalDateTime nowAsLocalDate = getLocalDateTime(date, zoneId);
        return nowAsLocalDate.isBefore(date);
    }

    @Override
    public boolean isAfter(LocalDateTime date, ZoneId zoneId) {
        LocalDateTime nowAsLocalDate = getLocalDateTime(date, zoneId);
        return nowAsLocalDate.isAfter(date);
    }

    @Override
    public Instant getRepresentedDate(ZoneId zoneId) {
        return getLocalDateTime(now.atZone(zoneId).toLocalDateTime(), zoneId).toInstant(zoneId.getRules().getOffset(now));
    }

    @Override
    public String toString() {
        return now + " +" + amount + " " + chronoUnit + (truncated ? " (truncated)" : "");
    }
}
