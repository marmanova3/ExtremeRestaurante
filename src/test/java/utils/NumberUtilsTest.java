package utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NumberUtilsTest {

    NumberUtils numberUtils;

    @Before
    public void setUp() {
        numberUtils = new NumberUtils();
    }

    @Test
    public void valueShouldBeInDoubleFormat() {
        assertTrue(numberUtils.hasDoubleFormat("0.2"));
    }

    @Test
    public void valueShouldBeInDoubleFormat1() {
        assertTrue(numberUtils.hasDoubleFormat("2"));
    }

    @Test
    public void valueShouldNotBeInDoubleFormat() {
        assertFalse(numberUtils.hasDoubleFormat("2.222222"));
    }

    @Test
    public void valueShouldNotBeInDoubleFormat1() {
        assertFalse(numberUtils.hasDoubleFormat("x"));
    }

    @Test
    public void valueShouldNotBeInDoubleFormat2() {
        assertFalse(numberUtils.hasDoubleFormat("2,3"));
    }

    @Test
    public void cashIsNotANumber() {
        assertFalse(numberUtils.isValidDoubleFormat("a"));
    }

    @Test
    public void cashIsAnEmptyString() {
        assertFalse(numberUtils.isValidDoubleFormat(""));
    }

    @Test
    public void cashIsCorrectNumber() {
        assertTrue(numberUtils.isValidDoubleFormat("13"));
    }

    @Test
    public void cashIsCorrectDecimalNumber() {
        assertTrue(numberUtils.isValidDoubleFormat("12.023453458"));
    }

    @Test
    public void doubleNumbersEquals() {
        assertTrue(numberUtils.isGreaterOrEqual(new Double(20), new Double(20)));
    }

    @Test
    public void doubleNumberIsGreaterThanOther() {
        assertTrue(numberUtils.isGreaterOrEqual(new Double(20.01), new Double(20)));
    }

    @Test
    public void doubleNumberIsLessThanOther() {
        assertFalse(numberUtils.isGreaterOrEqual(new Double(20.00), new Double(20.01)));
    }

    @Test
    public void valueShouldBeRounded() {
        assertEquals(new Double(12.12), numberUtils.getRoundedDecimalNumber(new Double(12.123), 2));
    }

    @Test
    public void valueShouldBeRounded2() {
        assertEquals(new Double(12.123), numberUtils.getRoundedDecimalNumber(new Double(12.123), 3));
    }

    @Test
    public void valueShouldBeRounded3() {
        assertEquals(new Double(12.13), numberUtils.getRoundedDecimalNumber(new Double(12.127), 2));
    }

    @Test
    public void valueShouldBeRounded4() {
        assertEquals(new Double(12), numberUtils.getRoundedDecimalNumber(new Double(12.28), 0));
    }
}