package caller79.numericrange;

import org.junit.Assert;
import org.junit.Test;

public class NumericRangeFactoryTest {

    @Test
    public void testContainsAsInReadmeFile() {
        NumericRangeFactory factory = new NumericRangeFactory();
        String expression = "(,0)[1,2](3.141592,4)[5,6)(7,8][9]";
        MultipleNumericRange range = factory.parse(expression);

        Assert.assertTrue(range.contains(-1));
        Assert.assertFalse(range.contains(0));
        Assert.assertTrue(range.contains(1));
        Assert.assertTrue(range.contains(1.5));
        Assert.assertTrue(range.contains(2));
        Assert.assertFalse(range.contains(3.141592));
        Assert.assertTrue(range.contains(3.5));
        Assert.assertFalse(range.contains(4));
        Assert.assertTrue(range.contains(5));
        Assert.assertFalse(range.contains(6));
        Assert.assertFalse(range.contains(7));
        Assert.assertTrue(range.contains(8));
        Assert.assertTrue(range.contains(9));
        Assert.assertFalse(range.contains(10));

        Assert.assertTrue(range.overlaps(factory.parse("(-1,0]")));
        Assert.assertFalse(range.overlaps(factory.parse("[0,0.5]")));
        Assert.assertTrue(range.overlaps(factory.parse("[0,1]")));
        Assert.assertTrue(range.overlaps(factory.parse("[0,0.5][8,9)")));

        Assert.assertEquals(range.toString(), expression);

        String expectedSql = "(products.price_in_usd<0) OR (products.price_in_usd>=1 AND products.price_in_usd<=2) OR (products.price_in_usd>3.141592 AND products.price_in_usd<4) "
            + "OR (products.price_in_usd>=5 AND products.price_in_usd<6) OR (products.price_in_usd>7 AND products.price_in_usd<=8) OR (products.price_in_usd=9)";
        Assert.assertEquals(range.toString(MultipleNumericRange.ToStringCustomizer.sqlCustomizer("products.price_in_usd")), expectedSql);
    }

    @Test
    public void testToString() {
        NumericRangeFactory factory = new NumericRangeFactory();
        String expression = "(,0)[1,2](3,4)[5,6)(7,8](9,]";
        String expectedSQL = "(x<0) OR (x>=1 AND x<=2) OR (x>3 AND x<4) OR (x>=5 AND x<6) OR (x>7 AND x<=8) OR (x>9)";
        String expectedJavascript = "(x<0) || (x>=1 && x<=2) || (x>3 && x<4) || (x>=5 && x<6) || (x>7 && x<=8) || (x>9)";

        MultipleNumericRange range = factory.parse(expression);
        Assert.assertEquals(range.toString(MultipleNumericRange.ToStringCustomizer.numericRangeCustomizer()), expression);
        Assert.assertEquals(range.toString(MultipleNumericRange.ToStringCustomizer.sqlCustomizer("x")), expectedSQL);
        Assert.assertEquals(range.toString(MultipleNumericRange.ToStringCustomizer.javascriptCustomizer("x")), expectedJavascript);
    }

    @Test
    public void testEmptyToString() {
        String expression = "(,)";
        String expectedSQL = "(1=1)";
        String expectedJavascript = "(true)";

        MultipleNumericRange range = new MultipleNumericRange(null);
        Assert.assertEquals(range.toString(MultipleNumericRange.ToStringCustomizer.numericRangeCustomizer()), expression);
        Assert.assertEquals(range.toString(MultipleNumericRange.ToStringCustomizer.sqlCustomizer("x")), expectedSQL);
        Assert.assertEquals(range.toString(MultipleNumericRange.ToStringCustomizer.javascriptCustomizer("x")), expectedJavascript);

        NumericRangeFactory factory = new NumericRangeFactory();
        range = factory.parse(expression);
        Assert.assertEquals(range.toString(MultipleNumericRange.ToStringCustomizer.numericRangeCustomizer()), expression);
        Assert.assertEquals(range.toString(MultipleNumericRange.ToStringCustomizer.sqlCustomizer("x")), expectedSQL);
        Assert.assertEquals(range.toString(MultipleNumericRange.ToStringCustomizer.javascriptCustomizer("x")), expectedJavascript);
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
