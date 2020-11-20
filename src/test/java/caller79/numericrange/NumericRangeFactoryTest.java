package caller79.numericrange;

import org.junit.Assert;
import org.junit.Test;

public class NumericRangeFactoryTest {

    @Test
    public void testContainsAsInReadmeFile() {
        NumericRangeFactory factory = new NumericRangeFactory();
        MultipleNumericRange range = factory.parse("(,0)[1,2](3,4)[5,6)(7,8]");

        Assert.assertTrue(range.contains(-1));
        Assert.assertFalse(range.contains(0));
        Assert.assertTrue(range.contains(1));
        Assert.assertTrue(range.contains(1.5));
        Assert.assertTrue(range.contains(2));
        Assert.assertFalse(range.contains(3));
        Assert.assertTrue(range.contains(3.5));
        Assert.assertFalse(range.contains(4));
        Assert.assertTrue(range.contains(5));
        Assert.assertFalse(range.contains(6));
        Assert.assertFalse(range.contains(7));
        Assert.assertTrue(range.contains(8));
        Assert.assertFalse(range.contains(9));

        Assert.assertTrue(range.overlaps(factory.parse("(-1,0]")));
        Assert.assertFalse(range.overlaps(factory.parse("[0,0.5]")));
        Assert.assertTrue(range.overlaps(factory.parse("[0,1]")));
        Assert.assertTrue(range.overlaps(factory.parse("[0,0.5][8,9)")));
    }

    @Test
    public void testContains() {
        NumericRangeFactory factory = new NumericRangeFactory();
        MultipleNumericRange parsed = factory.parse("(,-1)[-1,0)(0,1][" + Math.E + ",3](3.141592,4][5,]");
        Assert.assertNotNull(parsed);
        Assert.assertTrue(parsed.contains(-2));
        Assert.assertTrue(parsed.contains(-1));
        Assert.assertFalse(parsed.contains(0));
        Assert.assertTrue(parsed.contains(1));
        Assert.assertFalse(parsed.contains(2));
        Assert.assertTrue(parsed.contains(Math.E));
        Assert.assertTrue(parsed.contains(3));
        Assert.assertTrue(parsed.contains(4));
        Assert.assertFalse(parsed.contains(4.5));
        Assert.assertTrue(parsed.contains(5));
        Assert.assertTrue(parsed.contains(6));
    }

    @Test
    public void testOverlaps() {
        NumericRangeFactory factory = new NumericRangeFactory();
        MultipleNumericRange parsed = factory.parse("(,-1)(0,1][" + Math.E + ",3](3.141592,4][5,]");
        Assert.assertNotNull(parsed);
        Assert.assertTrue(parsed.overlaps(factory.parse("(,)").getRanges().get(0)));
        Assert.assertTrue(parsed.overlaps(factory.parse("(,]").getRanges().get(0)));
        Assert.assertTrue(parsed.overlaps(factory.parse("[,)").getRanges().get(0)));
        Assert.assertTrue(parsed.overlaps(factory.parse("[,]").getRanges().get(0)));
        Assert.assertFalse(parsed.overlaps(factory.parse("(-0.5,-0.4)").getRanges().get(0)));
        Assert.assertTrue(parsed.overlaps(factory.parse("(-1.5,0)").getRanges().get(0)));
    }

    @Test
    public void testExactRanges() {
        NumericRangeFactory factory = new NumericRangeFactory();
        MultipleNumericRange parsed = factory.parse("[1][2,3][4]");
        Assert.assertNotNull(parsed);
        Assert.assertFalse(parsed.contains(0));
        Assert.assertTrue(parsed.contains(1));
        Assert.assertTrue(parsed.contains(2));
        Assert.assertTrue(parsed.contains(2.5));
        Assert.assertTrue(parsed.contains(3));
        Assert.assertTrue(parsed.contains(4));
        Assert.assertFalse(parsed.contains(5));
    }
}
