package caller79.propertysetters;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Carlos Aller on 8/10/19
 */
@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Builder
public class PropertyRequest {
    private String name;
    private Object value;

    public static PropertyRequest fromMap(Map<String, Object> map) {
        return PropertyRequest.builder().name(String.valueOf(map.get("name"))).value(map.get("value")).build();
    }

    public Map<String, Object> asMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("value", value);
        return Collections.unmodifiableMap(result);
    }
}
