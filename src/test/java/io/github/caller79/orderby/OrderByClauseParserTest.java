package io.github.caller79.orderby;

import io.github.caller79.orderby.propertyacceptors.DistanceToPropertyAcceptor;
import io.github.caller79.orderby.propertyextractors.CombinedPropertyExtractor;
import io.github.caller79.orderby.propertyextractors.DistanceToPropertyExtractor;
import io.github.caller79.orderby.propertyextractors.PropertyExtractor;
import io.github.caller79.orderby.propertyextractors.RandomPropertyExtractor;
import io.github.caller79.orderby.propertyextractors.ReflectionPropertyExtractor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by Carlos Aller on 21/08/19
 */
@Slf4j
@SuppressWarnings({"PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods"})
public class OrderByClauseParserTest {

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    public void testEmptyDoesntFail() throws ParseException {
        List<TestBean> testBeans = generateTestObjects(10);
        Collections.shuffle(testBeans);
        OrderByClause clause = OrderByClauseParser.parse("", "id");
        OrderByComparator<TestBean> comparator = new OrderByComparator<>(clause, new ReflectionPropertyExtractor<>("id"));
        testBeans.sort(comparator);
    }

    @Test
    public void testMultiReflectionComparator() throws ParseException {
        List<TestBean> testBeans = generateTestObjects(100);
        Collections.shuffle(testBeans);
        PropertyExtractor<TestBean> extractor = new ReflectionPropertyExtractor<>("id", "name", "type");
        OrderByClause clause = OrderByClauseParser.parse("type DESC, name ASC, id", extractor);
        OrderByComparator<TestBean> comparator = new OrderByComparator<>(clause, extractor);
        testBeans.sort(comparator);
        TestBean previous = null;
        for (TestBean testBean : testBeans) {
            if (previous != null) {
                Assert.assertTrue(previous.getType().compareTo(testBean.getType()) >= 0);
                if (previous.getType().equals(testBean.getType())) {
                    Assert.assertTrue(previous.getName().compareTo(testBean.getName()) <= 0);
                    if (previous.getName().equals(testBean.getName())) {
                        Assert.assertTrue(previous.getId() < (testBean.getId()));
                    }
                }
            }
            previous = testBean;
        }
    }

    @Test
    public void testMultiComparator() throws ParseException {
        List<TestBean> testBeans = generateTestObjects(100);
        Collections.shuffle(testBeans);
        PropertyExtractor<TestBean> extractor = new CombinedPropertyExtractor<>(
            new ReflectionPropertyExtractor<>("id", "name", "type"),
            new DistanceToPropertyExtractor<TestBean>() {
                @Override
                protected Double getLatitude(TestBean object) {
                    return object.getLatitude();
                }

                @Override
                protected Double getLongitude(TestBean object) {
                    return object.getLongitude();
                }
            },
            new RandomPropertyExtractor<TestBean>() {
                @Override
                protected long getId(TestBean item) {
                    return item.getId();
                }
            }
        );

        OrderByClause clause = OrderByClauseParser.parse("type DESC, name ASC, id", extractor);
        OrderByComparator<TestBean> comparator = new OrderByComparator<>(clause, extractor);
        testBeans.sort(comparator);
        TestBean previous = null;
        for (TestBean testBean : testBeans) {
            if (previous != null) {
                Assert.assertTrue(previous.getType().compareTo(testBean.getType()) >= 0);
                if (previous.getType().equals(testBean.getType())) {
                    Assert.assertTrue(previous.getName().compareTo(testBean.getName()) <= 0);
                    if (previous.getName().equals(testBean.getName())) {
                        Assert.assertTrue(previous.getId() < (testBean.getId()));
                    }
                }
            }
            previous = testBean;
        }
    }

    @Test
    public void testInvalidPropertiesFail() {
        try {
            OrderByClauseParser.parse("id ASC, name DESC, wrongThing DESC", "id", "name", "text", "whatever");
            Assert.fail("Expected exception not thrown!");
        } catch (ParseException e) {
            log.debug("Exception has been thrown but was expected", e);
        }
    }

    @Test
    public void testSortById() throws ParseException {
        List<TestBean> testBeans = generateTestObjects(10);
        Collections.shuffle(testBeans);
        OrderByClause clause = OrderByClauseParser.parse("id ASC", "id");
        OrderByComparator<TestBean> comparator = new OrderByComparator<>(clause, new ReflectionPropertyExtractor<>("id"));
        testBeans.sort(comparator);
        for (int i = 0; i < testBeans.size(); i++) {
            TestBean testBean = testBeans.get(i);
            Assert.assertEquals(testBean.id, i);
        }
    }

    @Test
    public void testSortByRandom() throws ParseException {
        RandomPropertyExtractor<TestBean> extractor = new RandomPropertyExtractor<TestBean>() {
            @Override
            protected long getId(TestBean item) {
                return item.getId();
            }
        };
        OrderByComparator<TestBean> comparator = new OrderByComparator<>(OrderByClauseParser.parse("random(12a3) ASC", extractor), extractor);
        List<TestBean> testBeans = generateTestObjects(100);
        testBeans.sort(comparator);
        List<TestBean> testObjects2 = generateTestObjects(100);
        testObjects2.sort(comparator);
        Assert.assertEquals(testBeans, testObjects2);

        OrderByComparator<TestBean> comparator2 = new OrderByComparator<>(OrderByClauseParser.parse("random(2a3) ASC", extractor), extractor);
        testObjects2.sort(comparator2);
        Assert.assertNotEquals(testBeans, testObjects2);
    }

    @Test
    public void testSortByProximity() throws ParseException {
        List<TestBean> testBeans = generateTestObjects(10);
        Collections.shuffle(testBeans);
        OrderByClause clause = OrderByClauseParser.parse("distanceTo(5.5:4.4) ASC", new DistanceToPropertyAcceptor());
        OrderByComparator<TestBean> comparator = new OrderByComparator<>(clause,
            new DistanceToPropertyExtractor<TestBean>() {
                @Override
                protected Double getLatitude(TestBean object) {
                    return object.getLatitude();
                }

                @Override
                protected Double getLongitude(TestBean object) {
                    return object.getLongitude();
                }
            }
        );
        testBeans.sort(comparator);
        TestBean lastObject = null;
        for (TestBean testBean : testBeans) {
            if (lastObject != null) {
                Assert.assertTrue(distanceTo(lastObject, 5.5, 4.4) < distanceTo(testBean, 5.5, 4.4));
            }
            lastObject = testBean;
        }

        clause = OrderByClauseParser.parse("distanceTo(1.1:2.2) DESC", "distanceTo(1.1:2.2)"::equals);
        comparator = new OrderByComparator<>(clause, new CombinedPropertyExtractor<>(new DistanceToPropertyExtractor<TestBean>() {
            @Override
            protected Double getLatitude(TestBean object) {
                return object.getLatitude();
            }

            @Override
            protected Double getLongitude(TestBean object) {
                return object.getLongitude();
            }
        }));
        testBeans.sort(comparator);
        lastObject = null; //NOPMD
        for (TestBean testBean : testBeans) {
            if (lastObject != null) {
                Assert.assertTrue(distanceTo(lastObject, 1.1, 2.2) > distanceTo(testBean, 1.1, 2.2));
            }
            lastObject = testBean;
        }
    }

    private double distanceTo(TestBean object, double lat, double lng) {
        return new Point(object.latitude, object.longitude).approxDistanceTo(new Point(lat, lng));
    }

    @Test
    public void testSortByTypeAndId() throws ParseException {
        List<TestBean> testBeans = generateTestObjects(100);
        Collections.shuffle(testBeans);
        OrderByClause clause = OrderByClauseParser.parse(" type DESC,  id ASC", "id", "type");
        OrderByComparator<TestBean> comparator = new OrderByComparator<>(clause, new ReflectionPropertyExtractor<>("id", "type"));
        testBeans.sort(comparator);
        int lastId = -1;
        String lastType = null;
        for (int i = 0; i < testBeans.size(); i++) {
            TestBean testBean = testBeans.get(i);
            if (i == 0) {
                lastId = testBean.id;
                lastType = testBean.getType();
            } else {
                if (lastType.equals(testBean.getType())) {
                    if (lastId > testBean.getId()) {
                        Assert.fail("Expecting an ascending order in id, but found " + lastId + " followed by " + testBean.getId());
                    }
                } else {
                    if (lastType.compareTo(testBean.getType()) < 0) {
                        Assert.fail("Expecting a descending order in type, but found " + lastType + " followed by " + testBean.getType());
                    }
                }
            }
        }
    }

    @Test
    public void testSortByTypeNameAndId() throws ParseException {
        List<TestBean> testBeans = generateTestObjects(200);
        Collections.shuffle(testBeans);
        OrderByClause clause = OrderByClauseParser.parse(" type ASC, name ,  \n id DESC", "id", "type", "name");
        OrderByComparator<TestBean> comparator = new OrderByComparator<>(clause, new ReflectionPropertyExtractor<>("id", "type", "name"));
        testBeans.sort(comparator);
        int lastId = -1;
        String lastType = null;
        String lastName = null;
        for (int i = 0; i < testBeans.size(); i++) {
            TestBean testBean = testBeans.get(i);
            if (i == 0) {
                lastId = testBean.id;
                lastType = testBean.getType();
                lastName = testBean.getName();
            } else {
                if (lastType.equals(testBean.getType()) && lastName.equals(testBean.getName())) {
                    if (lastId < testBean.getId()) {
                        Assert.fail("Expecting an descending order in id, but found " + lastId + " followed by " + testBean.getId());
                    }
                } else if (lastType.equals(testBean.getType())) {
                    if (lastName.compareTo(testBean.getName()) > 0) {
                        Assert.fail("Expecting an ascending order in name, but found " + lastName + " followed by " + testBean.getName());
                    }
                } else if (lastType.compareTo(testBean.getType()) > 0) {
                    Assert.fail("Expecting a ascending order in type, but found " + lastType + " followed by " + testBean.getType());

                }
            }
        }
    }

    @Test
    public void testReflectionPropertyExtractorWithClassConstructor() {
        ReflectionPropertyExtractor<TestBean> extractor = new ReflectionPropertyExtractor<>(TestBean.class);
        Assert.assertTrue(extractor.isPropertyAccepted("id"));
        Assert.assertTrue(extractor.isPropertyAccepted("name"));
        Assert.assertTrue(extractor.isPropertyAccepted("type"));
        Assert.assertTrue(extractor.isPropertyAccepted("latitude"));
        Assert.assertTrue(extractor.isPropertyAccepted("longitude"));
        Assert.assertTrue(extractor.isPropertyAccepted("date"));
        Assert.assertTrue(extractor.isPropertyAccepted("bool"));
        Assert.assertTrue(extractor.isPropertyAccepted("radius"));
        Assert.assertFalse(extractor.isPropertyAccepted("listString"));
        Assert.assertFalse(extractor.isPropertyAccepted("other"));
        Assert.assertTrue(extractor.isPropertyAccepted("myEnum"));
    }

    @Test
    public void testReflectionPropertyExtractorWithClassConstructorExcludedProperties() {
        ReflectionPropertyExtractor<TestBean> extractor = new ReflectionPropertyExtractor<>(TestBean.class, "latitude", "id");
        Assert.assertFalse(extractor.isPropertyAccepted("id"));
        Assert.assertTrue(extractor.isPropertyAccepted("name"));
        Assert.assertTrue(extractor.isPropertyAccepted("type"));
        Assert.assertFalse(extractor.isPropertyAccepted("latitude"));
        Assert.assertTrue(extractor.isPropertyAccepted("longitude"));
        Assert.assertFalse(extractor.isPropertyAccepted("other"));
        Assert.assertTrue(extractor.isPropertyAccepted("myEnum"));
    }

    @Test
    public void testNumberOfDistanceToParsesDone() throws ParseException {
        final int[] parsersCreated = {0};
        DistanceToPropertyExtractor<TestBean> distanceToPropertyExtractor = new DistanceToPropertyExtractor<TestBean>() {
            @Override
            protected DistanceToPropertyParser createNewParser(String name) {
                parsersCreated[0] = parsersCreated[0] + 1;
                return super.createNewParser(name);
            }

            @Override
            protected Double getLatitude(TestBean object) {
                return object.getLatitude();
            }

            @Override
            protected Double getLongitude(TestBean object) {
                return object.getLongitude();
            }
        };

        CombinedPropertyExtractor<TestBean> combinedExtractor = new CombinedPropertyExtractor<>(distanceToPropertyExtractor);

        List<TestBean> testBeans = generateTestObjects(10000);
        OrderByClause clause = OrderByClauseParser.parse("distanceTo(5.5:4.4) ASC", combinedExtractor);
        OrderByComparator<TestBean> comparator = new OrderByComparator<>(clause, combinedExtractor);
        testBeans.sort(comparator);
        Assert.assertEquals(parsersCreated[0], 1);
    }

    private List<TestBean> generateTestObjects(int length) {
        List<TestBean> result = new ArrayList<>();
        int index = 0;
        while (result.size() < length) {
            result.add(TestBean
                .builder()
                .id(index++)
                .name("name" + ((length - index) % index))
                .type("type" + (index % 10))
                .latitude(((index * 311.123456789) % 180) - 90.0)
                .longitude(((index * 210.0123456789) % 360) - 180.0)
                .build()
            );
        }
        return result;
    }

    @lombok.Data
    @lombok.Builder
    public static class TestBean {
        private final int id;
        private final String name;
        private final String type;
        private final Double latitude;
        private final Double longitude;
        private final Date date;
        private final boolean bool;
        private final double radius;
        private final List<String> listString;
        private final MyEnum myEnum;
    }

    public enum MyEnum {
        A, B, C
    }
}
