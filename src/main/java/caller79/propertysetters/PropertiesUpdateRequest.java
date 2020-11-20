package caller79.propertysetters;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by Carlos Aller on 8/10/19
 */
@Data
public class PropertiesUpdateRequest {
    public static final String PROPERTIES = "properties";

    private List<PropertyRequest> properties;

    public static PropertiesUpdateRequest fromMap(Map<String, List<Map<String, Object>>> map) {
        PropertiesUpdateRequest request = new PropertiesUpdateRequest();
        List<Map<String, Object>> propertiesList = map.get(PROPERTIES);
        request.setProperties(propertiesList.stream().map(PropertyRequest::fromMap).collect(Collectors.toList()));
        return request;
    }

    public Map<String, List<Map<String, Object>>> asMap() {
        Map<String, List<Map<String, Object>>> result = new ConcurrentHashMap<>();
        if (properties != null && !properties.isEmpty()) {
            result.put(PROPERTIES, properties.stream().map(PropertyRequest::asMap).collect(Collectors.toList()));
        }
        return Collections.unmodifiableMap(result);
    }
}
