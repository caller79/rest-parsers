package caller79.daterange;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Created by Carlos Aller on 4/10/19
 */
@lombok.Builder
public class DateRangeFactory {
    private static final DateTimeFormatter FORMATTER_WITH_HOUR = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Pattern MINIMAL_DATE_PATTERN = Pattern.compile("^[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}$");

    private static final Pattern NUMBER_FORMAT_PATTERN = Pattern.compile("^-?[0-9]+[dhm]?\\|?$");
    private static final String SEPARATOR = ",";
    private static final Map<String, ChronoUnit> CHRONO_UNIT_MAP = new ConcurrentHashMap<>();

    static {
        CHRONO_UNIT_MAP.put("d", ChronoUnit.DAYS);
        CHRONO_UNIT_MAP.put("h", ChronoUnit.HOURS);
        CHRONO_UNIT_MAP.put("m", ChronoUnit.MINUTES);
    }

    @lombok.Builder.Default
    private final long currentTimestamp = System.currentTimeMillis();

    /**
     * Expected text representation is
     * START,END
     * where both START and END can be
     * <li>empty (infinite range)</li>
     * <li>a specific date (like 2019-12-25 or 2019-12-25 00:00:00) </li>
     * <li> an integer, interpreted as an offset from today followed by 'd' for days or 'h' for hours or 'm' for minutes.</li>
     *
     * <p>
     * This integer offset can be truncated to the beginning of the day, hour or minute by adding the character |.
     *
     * </p>
     * <p>
     * For example, '0d,1d' means 'next 24 hours', while '0d,1d|' means 'from now until the end of the day'
     * </p>
     *
     * @param textRepresentation
     * @return
     */
    public DateRange parseRange(String textRepresentation) {
        String[] components = textRepresentation.split(SEPARATOR);
        if (components.length > 2) {
            throw new IllegalArgumentException("Invalid range representation, found " + components.length + " components, expecting 1 or 2.");
        }
        String fromComponent = components.length > 0 ? components[0].trim() : "";
        String toComponent = components.length > 1 ? components[1].trim() : "";
        return TwoFactorDateRange.builder()
            .from(parseDateComponent(fromComponent))
            .to(parseDateComponent(toComponent))
            .build();
    }

    public Instant getCurrentInstant() {
        return Instant.ofEpochMilli(currentTimestamp);
    }

    private DateRangeFactor parseDateComponent(String component) {
        if (StringUtils.isEmpty(component)) {
            return null;
        }
        if (NUMBER_FORMAT_PATTERN.matcher(component).matches()) {
            return parseNumericComponent(component);
        }
        return parseFixedDateComponent(component);
    }

    private DateRangeFactor parseNumericComponent(String component) {
        ChronoUnit unit = ChronoUnit.DAYS;
        boolean isTruncated = component.endsWith("|");
        String numericComponent = isTruncated ? component.substring(0, component.length() - 1) : component;
        Set<Map.Entry<String, ChronoUnit>> entries = CHRONO_UNIT_MAP.entrySet();
        for (Map.Entry<String, ChronoUnit> next : entries) {
            if (numericComponent.endsWith(next.getKey())) {
                numericComponent = numericComponent.substring(0, numericComponent.length() - next.getKey().length());
                unit = next.getValue();
                break;
            }
        }
        long numericValue = Long.parseLong(StringUtils.defaultIfEmpty(numericComponent, "0"));
        return RelativeDateRangeFactor
            .builder()
            .amount(numericValue)
            .truncated(isTruncated)
            .chronoUnit(unit)
            .now(getCurrentInstant())
            .build();
    }

    private DateRangeFactor parseFixedDateComponent(String component) {
        String fullComponent = MINIMAL_DATE_PATTERN.matcher(component).matches() ? (component + " 00:00:00") : component;
        LocalDateTime localDateTime;
        localDateTime = LocalDateTime.parse(fullComponent, FORMATTER_WITH_HOUR);
        return AbsoluteDateRangeFactor.builder().dateTime(localDateTime).build();
    }

}
