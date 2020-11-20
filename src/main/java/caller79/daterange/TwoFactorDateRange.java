package caller79.daterange;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by Carlos Aller on 2/12/19
 */
@lombok.RequiredArgsConstructor
@lombok.Builder
class TwoFactorDateRange implements DateRange {
    private final DateRangeFactor from;
    private final DateRangeFactor to;

    @Override
    public boolean contains(LocalDateTime date, ZoneId zoneId) {
        return (from == null || !from.isAfter(date, zoneId)) && (to == null || !to.isBefore(date, zoneId));
    }

    @Override
    public boolean overlaps(LocalDateTime startDate, LocalDateTime endDate, ZoneId zoneId) {
        LocalDateTime start = startDate == null ? LocalDateTime.MIN : startDate;
        LocalDateTime end = endDate == null ? LocalDateTime.MAX : endDate;
        return (to == null || !to.isBefore(start, zoneId)) && (from == null || !from.isAfter(end, zoneId));
    }

    @Override
    public Instant getStart(ZoneId zoneId) {
        return from == null ? null : from.getRepresentedDate(zoneId);
    }

    @Override
    public Instant getEnd(ZoneId zoneId) {
        return to == null ? null : to.getRepresentedDate(zoneId);
    }

    @Override
    public String toString() {
        return from + "->" + to;
    }
}
