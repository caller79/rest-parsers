package io.github.caller79.propertysetters;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Carlos Aller on 9/10/19
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals"})
public class PropertySettingHelperTest {
    @Test
    public void trySettingManyProperties() throws PropertySetException {
        List<PropertyRequest> propertiesToSet = new ArrayList<>();
        propertiesToSet.add(PropertyRequest.builder().name("name").value("Carlos").build());
        propertiesToSet.add(PropertyRequest.builder().name("count").value(40).build());
        propertiesToSet.add(PropertyRequest.builder().name("weight").value(83.5).build());
        propertiesToSet.add(PropertyRequest.builder().name("startDate").value("1979-02-23").build());
        propertiesToSet.add(PropertyRequest.builder().name("startDateTime").value("1979-02-23 00:00:00").build());
        propertiesToSet.add(PropertyRequest.builder().name("valid").value(true).build());
        propertiesToSet.add(PropertyRequest.builder().name("myEnum").value("VAL2").build());

        MyBean bean = new MyBean();
        PropertySettingHelper<MyBean> helper = new PropertySettingHelper<>(null);
        helper.applyProperties(bean, propertiesToSet);
        Assert.assertEquals(bean.getName(), "Carlos");
        Assert.assertEquals((int) bean.getCount(), 40);
        Assert.assertEquals(bean.getWeight(), new BigDecimal("83.5"));
        Assert.assertEquals(bean.getStartDate().getTime(), Date.UTC(79, 1, 23, 0, 0, 0));
        Assert.assertEquals(bean.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), "1979-02-23 00:00:00");
        Assert.assertEquals(bean.getValid(), true);
        Assert.assertEquals(bean.getMyEnum(), MyEnum.VAL2);

        propertiesToSet.clear();
        propertiesToSet.add(PropertyRequest.builder().name("name").value(null).build());
        propertiesToSet.add(PropertyRequest.builder().name("count").value(null).build());
        propertiesToSet.add(PropertyRequest.builder().name("weight").value(null).build());
        propertiesToSet.add(PropertyRequest.builder().name("startDate").value(null).build());
        propertiesToSet.add(PropertyRequest.builder().name("startDateTime").value(null).build());
        propertiesToSet.add(PropertyRequest.builder().name("valid").value(null).build());
        propertiesToSet.add(PropertyRequest.builder().name("myEnum").value(null).build());
        helper.applyProperties(bean, propertiesToSet);
        Assert.assertNull(bean.getName());
        Assert.assertNull(bean.getCount());
        Assert.assertNull(bean.getWeight());
        Assert.assertNull(bean.getStartDate());
        Assert.assertNull(bean.getStartDateTime());
        Assert.assertNull(bean.getValid());
        Assert.assertNull(bean.getMyEnum());
    }

    @Test
    public void trySettingManyDateProperties() throws PropertySetException {
        List<PropertyRequest> propertiesToSet = new ArrayList<>();
        String[] dates = {"1979-02-23", "1979-02-23 00:00", "1979-02-23 00:00:00"};
        PropertySettingHelper<MyBean> helper = new PropertySettingHelper<>(null);
        for (String date : dates) {
            propertiesToSet.clear();
            propertiesToSet.add(PropertyRequest.builder().name("startDate").value(date).build());
            propertiesToSet.add(PropertyRequest.builder().name("startDateTime").value(date).build());
            MyBean bean = new MyBean();
            helper.applyProperties(bean, propertiesToSet);
            Assert.assertEquals(bean.getStartDate().getTime(), Date.UTC(79, 1, 23, 0, 0, 0));
            Assert.assertEquals(bean.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), "1979-02-23 00:00:00");
        }

        propertiesToSet.clear();
        propertiesToSet.add(PropertyRequest.builder().name("startDate").value("1979-02-23 01:02:03").build());
        propertiesToSet.add(PropertyRequest.builder().name("startDateTime").value("1979-02-23 01:02:03").build());
        MyBean bean = new MyBean();
        helper.applyProperties(bean, propertiesToSet);
        Assert.assertEquals(bean.getStartDate().getTime(), Date.UTC(79, 1, 23, 1, 2, 3));
        Assert.assertEquals(bean.getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), "1979-02-23 01:02:03");
    }

    @Test
    public void trySettingNestedProperties() throws PropertySetException {
        List<PropertyRequest> nestedPropertiesToSet = new ArrayList<>();
        nestedPropertiesToSet.add(PropertyRequest.builder().name("name").value("Carlos").build());
        nestedPropertiesToSet.add(PropertyRequest.builder().name("count").value(40).build());
        nestedPropertiesToSet.add(PropertyRequest.builder().name("weight").value(83.5).build());
        nestedPropertiesToSet.add(PropertyRequest.builder().name("startDate").value("1979-02-23").build());
        nestedPropertiesToSet.add(PropertyRequest.builder().name("startDateTime").value("1979-02-23 00:00:00").build());
        nestedPropertiesToSet.add(PropertyRequest.builder().name("valid").value(true).build());
        PropertiesUpdateRequest nestedPropertiesUpdateRequest = new PropertiesUpdateRequest();
        nestedPropertiesUpdateRequest.setProperties(nestedPropertiesToSet);
        List<PropertyRequest> propertiesToSet = new ArrayList<>();
        propertiesToSet.add(PropertyRequest.builder().name("nested").value(nestedPropertiesUpdateRequest.asMap()).build());
        MyBean bean = new MyBean();
        // bean.setNested(new MyNestedBean());
        PropertySettingHelper<MyBean> helper = new PropertySettingHelper<>(null);
        helper.applyProperties(bean, propertiesToSet);
        Assert.assertNotNull(bean.getNested());
        Assert.assertEquals(bean.getNested().getName(), "Carlos");
        Assert.assertEquals((int) bean.getNested().getCount(), 40);
        Assert.assertEquals(bean.getNested().getWeight(), new BigDecimal("83.5"));
        Assert.assertEquals(bean.getNested().getStartDate().getTime(), Date.UTC(79, 1, 23, 0, 0, 0));
        Assert.assertEquals(bean.getNested().getStartDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), "1979-02-23 00:00:00");
        Assert.assertEquals(bean.getNested().getValid(), true);
    }

    @Test
    public void tryReadonlyProperty() throws PropertySetException {
        List<PropertyRequest> nestedPropertiesToSet = new ArrayList<>();
        nestedPropertiesToSet.add(PropertyRequest.builder().name("readonly").value(true).build());
        PropertySettingHelper<MyNestedBean> helper = new PropertySettingHelper<>(null);
        MyNestedBean bean = new MyNestedBean();
        try {
            helper.applyProperties(bean, nestedPropertiesToSet);
            Assert.fail("Expected exception was not thrown");
        } catch (PropertySetException e) {
            System.out.println("Expected exception thrown");
        }
    }

    @Test
    public void testPropertiesAreSetInTheRightOrder() throws PropertySetException {
        List<PropertyRequest> propertiesToSet = new ArrayList<>();
        propertiesToSet.add(PropertyRequest.builder().name("field1").value("A").build());
        propertiesToSet.add(PropertyRequest.builder().name("field2").value("B").build());
        propertiesToSet.add(PropertyRequest.builder().name("field3").value("C").build());

        for (int i = 0; i < 20; i++) {
            Collections.shuffle(propertiesToSet, new Random(i));
            MyBean myBean = new MyBean();
            PropertySettingHelper<MyBean> helper = new PropertySettingHelper<>(null);
            helper.applyProperties(myBean, propertiesToSet);
            Assert.assertEquals(myBean.getField1(), "A"); // A, the property set since the rest were empty
            Assert.assertEquals(myBean.getField2(), "AB"); // AB, since this is set to B always AFTER field1 is set.
            Assert.assertEquals(myBean.getField3(), "AABC"); // AABC, since this is set to C always after field2 is set.
        }
    }

    @Test
    public void testWithUnknownProperties() throws PropertySetException {
        List<PropertyRequest> propertiesToSet = new ArrayList<>();
        propertiesToSet.add(PropertyRequest.builder().name("name").value("Carlos").build());
        propertiesToSet.add(PropertyRequest.builder().name("invalidPropertyName").value("VAL2").build());
        MyBean bean = new MyBean();
        PropertySettingHelper<MyBean> helper = new PropertySettingHelper<>(null, PropertySettingHelper.PropertySettingOptions.builder().failOnUnknownProperties(false).build());
        helper.applyProperties(bean, propertiesToSet); // No error

        PropertySettingHelper<MyBean> failingHelper = new PropertySettingHelper<>(null, PropertySettingHelper.PropertySettingOptions.builder().failOnUnknownProperties(true).build());
        try {
            failingHelper.applyProperties(bean, propertiesToSet);
            Assert.fail("Expected exception was not thrown.");
        } catch (PropertySetException be) {
            System.out.println("Expected exception was thrown.");
            Assert.assertTrue(be.getMessage().startsWith("Cannot set that property"));
        }
    }

    @Test
    public void testWithVirtualProperties() throws PropertySetException {
        List<PropertyRequest> propertiesToSet = new ArrayList<>();
        propertiesToSet.add(PropertyRequest.builder().name("vprop1").value("Pro1").build());
        propertiesToSet.add(PropertyRequest.builder().name("vprop2").value(1L).build());
        MyBean bean = new MyBean();
        PropertySettingHelper<MyBean> helper = new PropertySettingHelper<>(null, PropertySettingHelper.PropertySettingOptions.builder().failOnUnknownProperties(true).build());
        helper.applyProperties(bean, propertiesToSet);
        Assert.assertEquals(bean.getDynamicProperties().get("vprop1"), "Pro1");
        Assert.assertEquals(bean.getDynamicProperties().get("vprop2"), 1L);
    }

    @lombok.Data
    @SetteableVirtualProperties(properties = {@SetteableFromPropertyRequest(name = "vprop1", setter = DynamicVirtualPropertySetter.class), @SetteableFromPropertyRequest(name = "vprop2", setter = DynamicVirtualPropertySetter.class)})
    public static class MyBean {
        @SetteableFromPropertyRequest
        private String name;
        @SetteableFromPropertyRequest
        private Integer count;
        @SetteableFromPropertyRequest
        private BigDecimal weight;
        @SetteableFromPropertyRequest
        private Date startDate;
        @SetteableFromPropertyRequest
        private LocalDateTime startDateTime;
        @SetteableFromPropertyRequest
        private Boolean valid;
        @SetteableFromPropertyRequest(setter = NestedObjectPropertySetter.class)
        private MyNestedBean nested;
        @SetteableFromPropertyRequest
        private MyEnum myEnum;

        @SetteableFromPropertyRequest(priority = 1, setter = OrderCheckingStringPropertySetter.class)
        private String field1 = "";
        @SetteableFromPropertyRequest(priority = 2, setter = OrderCheckingStringPropertySetter.class)
        private String field2 = "";
        @SetteableFromPropertyRequest(priority = 3, setter = OrderCheckingStringPropertySetter.class)
        private String field3 = "";

        private Map<String, Object> dynamicProperties = new HashMap<>();
    }

    public static class DynamicVirtualPropertySetter extends BeanPropertySetter<MyBean> {
        @Override
        public void setProperty(MyBean item, String propertyName, Object propertyValue) throws PropertySetException {
            item.getDynamicProperties().put(propertyName, propertyValue);
        }
    }

    public static class OrderCheckingStringPropertySetter extends BeanPropertySetter<MyBean> {
        @Override
        public void setProperty(MyBean item, String propertyName, Object propertyValue) throws PropertySetException {
            switch (propertyName) {
                case "field1":
                    super.setProperty(item, propertyName, propertyValue + item.getField2() + item.getField3());
                    break;
                case "field2":
                    super.setProperty(item, propertyName, item.getField1() + propertyValue + item.getField3());
                    break;
                case "field3":
                    super.setProperty(item, propertyName, item.getField1() + item.getField2() + propertyValue);
                    break;
                default:
                    throw new PropertySetException("Unrecognized property " + propertyName);
            }
        }
    }

    @lombok.Data
    @SetteableFromPropertyRequest
    public static class MyNestedBean {
        private String name;
        private Integer count;
        private BigDecimal weight;
        private Date startDate;
        private LocalDateTime startDateTime;
        private Boolean valid;
        @SetteableFromPropertyRequest(ignore = true)
        private Boolean readOnly;
    }

    public enum MyEnum {
        VAL1, VAL2, VAL3
    }
}
