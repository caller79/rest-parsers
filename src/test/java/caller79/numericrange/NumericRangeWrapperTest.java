package caller79.numericrange;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NumericRangeWrapperTest {
    private final NumericRangeWrapper numericRangeWrapper = new NumericRangeWrapper();

    private NumericRangeWrapper.WrapOptions getOptions(int maxExpressions) {
        return NumericRangeWrapper.WrapOptions.builder().maxExpressions(maxExpressions).build();
    }

    @Test
    public void test1000Ids() {
        List<Long> list = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            if (i != 501) {
                list.add((long) i);
            }
        }
        list.add(1010L);

        Assert.assertEquals(list.size(), 1000);
        MultipleNumericRange range = numericRangeWrapper.wrapDiscrete(list, NumericRangeWrapper.WrapOptions.builder().maxExpressions(list.size()).build());
        String rangeAsSQLQuery = range.toString(MultipleNumericRange.ToStringCustomizer.sqlCustomizer("x"));
        Assert.assertEquals(rangeAsSQLQuery, "(x>=1 AND x<=500) OR (x>=502 AND x<=1000) OR (x=1010)");
    }

    @Test
    public void testEmpty() {
        List<Long> list = Collections.emptyList();
        Assert.assertEquals(numericRangeWrapper.wrap(list, getOptions(1)).toString(), "(0,0)");
        Assert.assertEquals(numericRangeWrapper.wrapDiscrete(list, getOptions(5)).toString(), "(0,0)");
    }

    @Test
    public void testSmallWrap() {
        List<Long> list = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        Assert.assertEquals(numericRangeWrapper.wrap(list, getOptions(1)).toString(), "[1,5]");
        Assert.assertEquals(numericRangeWrapper.wrapDiscrete(list, getOptions(5)).toString(), "[1,5]");
    }

    @Test
    public void testPairsAreSimplifiedWrap() {
        List<Long> list = Arrays.asList(1L, 2L, 30L, 40L, 150L, 151L, 152L, 200L, 201L, 300L, 301L);
        Assert.assertEquals(numericRangeWrapper.wrapDiscrete(list, getOptions(5)).toString(), "[1,2][30][40][150,152][200,201][300,301]");
    }

    @Test
    public void testSmallWrapWithTwoConnectedIslands() {
        List<Long> list = Arrays.asList(1L, 2L, 3L, 4L, 5L, 81L, 82L, 83L, 84L);
        Assert.assertEquals(numericRangeWrapper.wrapDiscrete(list, getOptions(5)).toString(), "[1,5][81,84]");
        Assert.assertEquals(numericRangeWrapper.wrap(list, getOptions(1)).toString(), "[1,84]");
    }

    @Test
    public void testSmallWrapWithTwoDisConnectedIslands() {
        List<Long> list = Arrays.asList(1L, 2L, 3L, 4L, 5L, 81L, 82L, 83L, 84L, 86L);
        Assert.assertEquals(numericRangeWrapper.wrapDiscrete(list, getOptions(5)).toString(), "[1,5][81,84][86]");
        Assert.assertEquals(numericRangeWrapper.wrapDiscrete(list, getOptions(2)).toString(), "[1,5][81,86]");
        Assert.assertEquals(numericRangeWrapper.wrap(list, getOptions(1)).toString(), "[1,86]");
    }

    @Test
    public void testSmallWrapTwoIslands() {
        MultipleNumericRange wrappedNumericRange = new NumericRangeWrapper().wrap(Arrays.asList(1, 2, 30, 32, 35), getOptions(2));
        Assert.assertEquals(wrappedNumericRange.toString(), "[1][2][30,35]");
    }

    @Test
    public void testSmallWrapThreeIslands() {
        MultipleNumericRange wrappedNumericRange = new NumericRangeWrapper().wrap(Arrays.asList(1, 2, 31, 32, 33, 51, 53, 56), getOptions(3));
        Assert.assertEquals(wrappedNumericRange.toString(), "[1][2][31,33][51,56]");
    }

    @Test
    public void testSmallWrapThreeIslandsLimitedToTwoExpressions() {
        MultipleNumericRange wrappedNumericRange = new NumericRangeWrapper().wrap(Arrays.asList(1, 2, 31, 32, 33, 51, 53, 56), getOptions(2));
        Assert.assertEquals(wrappedNumericRange.toString(), "[1][2][31,56]");
    }

    @Test
    public void testRandom() {
        Random random = new Random(123);
        for (int i = 0; i < 100; i++) {
            List<Number> list = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                list.add(random.nextDouble());
            }
            MultipleNumericRange wrappedNumericRange = new NumericRangeWrapper().wrap(list, getOptions(i / 4));
            List<NumericRange> ranges = wrappedNumericRange.getRanges();
            for (NumericRange numericRange : ranges) {
                Assert.assertNotNull(numericRange.start());
                Assert.assertNotNull(numericRange.end());
            }

            for (Number number : list) {
                Assert.assertTrue("Range " + wrappedNumericRange + " does not contain expected element " + number, wrappedNumericRange.contains(number.doubleValue()));
            }
        }
    }
}
