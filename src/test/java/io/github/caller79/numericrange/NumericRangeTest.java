package io.github.caller79.numericrange;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class NumericRangeTest {

    @Test
    public void testRangeOverlappingWithoutNulls() {
        NumericRange baseRange = NumericRange.builder()
            .start(10d)
            .end(20d)
            .startIncluded(true)
            .endIncluded(true).build();

        List<NumericRange> overlapping = Arrays.asList(
            NumericRange.builder()
                .start(9d).end(10d)
                .startIncluded(true).endIncluded(true)
                .build(),
            NumericRange.builder()
                .start(19d).end(30d)
                .startIncluded(true).endIncluded(true)
                .build(),
            NumericRange.builder()
                .start(20d).end(30d)
                .startIncluded(true).endIncluded(true)
                .build()
        );

        for (NumericRange range : overlapping) {
            Assert.assertTrue(range.overlaps(baseRange));
            Assert.assertTrue(baseRange.overlaps(range));
        }

        List<NumericRange> nonOverlapping = Arrays.asList(
            NumericRange.builder()
                .start(9d).end(10d)
                .startIncluded(true).endIncluded(false)
                .build(),
            NumericRange.builder()
                .start(20d).end(30d)
                .startIncluded(false).endIncluded(true)
                .build()
        );
        for (NumericRange range : nonOverlapping) {
            Assert.assertFalse(range.overlaps(baseRange));
            Assert.assertFalse(baseRange.overlaps(range));
        }
    }

    @Test
    public void testRangeOverlappingWithNulls() {
        NumericRange baseRangeNN = NumericRange.builder()
            .start(null)
            .end(null)
            .startIncluded(true)
            .endIncluded(true).build();

        NumericRange baseRangeNV = NumericRange.builder()
            .start(null)
            .end(1d)
            .startIncluded(true)
            .endIncluded(true).build();

        NumericRange baseRangeVN = NumericRange.builder()
            .start(2d)
            .end(null)
            .startIncluded(true)
            .endIncluded(true).build();

        NumericRange baseRangeVV = NumericRange.builder()
            .start(0d)
            .end(1d)
            .startIncluded(true)
            .build();

        Assert.assertTrue(baseRangeNN.overlaps(baseRangeNN));
        Assert.assertTrue(baseRangeNN.overlaps(baseRangeNV));
        Assert.assertTrue(baseRangeNN.overlaps(baseRangeVN));
        Assert.assertTrue(baseRangeNN.overlaps(baseRangeVV));

        Assert.assertTrue(baseRangeNN.overlaps(baseRangeNN));
        Assert.assertTrue(baseRangeNV.overlaps(baseRangeNN));
        Assert.assertTrue(baseRangeVN.overlaps(baseRangeNN));
        Assert.assertTrue(baseRangeVV.overlaps(baseRangeNN));

        Assert.assertTrue(baseRangeNV.overlaps(baseRangeNN));
        Assert.assertTrue(baseRangeNV.overlaps(baseRangeNV));
        Assert.assertFalse(baseRangeNV.overlaps(baseRangeVN));
        Assert.assertTrue(baseRangeNV.overlaps(baseRangeVV));

        Assert.assertTrue(baseRangeNN.overlaps(baseRangeNV));
        Assert.assertTrue(baseRangeNV.overlaps(baseRangeNV));
        Assert.assertFalse(baseRangeVN.overlaps(baseRangeNV));
        Assert.assertTrue(baseRangeVV.overlaps(baseRangeNV));

        Assert.assertTrue(baseRangeVN.overlaps(baseRangeNN));
        Assert.assertTrue(baseRangeVN.overlaps(baseRangeVN));
        Assert.assertFalse(baseRangeVN.overlaps(baseRangeNV));
        Assert.assertFalse(baseRangeVN.overlaps(baseRangeVV));

        Assert.assertTrue(baseRangeNN.overlaps(baseRangeVN));
        Assert.assertTrue(baseRangeVN.overlaps(baseRangeVN));
        Assert.assertFalse(baseRangeNV.overlaps(baseRangeVN));
        Assert.assertFalse(baseRangeVV.overlaps(baseRangeVN));
    }
}
