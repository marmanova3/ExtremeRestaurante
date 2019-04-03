package controller;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PopUpControllerTest {

    PopUpController popUpController;

    @Before
    public void setUp() {
        popUpController = new PopUpController();
    }

    @Test
    public void valueShouldBeInDoubleFormat() {
        assertTrue(popUpController.hasDoubleFormat("0.2"));
    }

    @Test
    public void valueShouldBeInDoubleFormat1() {
        assertTrue(popUpController.hasDoubleFormat("2"));
    }

    @Test
    public void valueShouldNotBeInDoubleFormat() {
        assertFalse(popUpController.hasDoubleFormat("2.222222"));
    }

    @Test
    public void valueShouldNotBeInDoubleFormat1() {
        assertFalse(popUpController.hasDoubleFormat("x"));
    }

    @Test
    public void valueShouldNotBeInDoubleFormat2() {
        assertFalse(popUpController.hasDoubleFormat("2,3"));
    }

    @Test
    public void cashIsNotANumber() {
        assertFalse(popUpController.receivedCashIsValid("a", 12.2));
    }

    @Test
    public void cashIsLesserThanPriceToPay() {
        assertFalse(popUpController.receivedCashIsValid("12.00", 12.01));
    }

    @Test
    public void cashIsGreaterThanPriceToPay() {
        assertTrue(popUpController.receivedCashIsValid("13", 1.03));
    }

    @Test
    public void cashIsEqualPriceToPay() {
        assertTrue(popUpController.receivedCashIsValid("12.02", 12.02));
    }

    @Test
    public void shouldReturnDifferenceInString() {
        assertEquals("1,22", popUpController.getOutlay(3.78, 5.00));
    }

    @Test
    public void shouldReturnDifferenceInString1() {
        assertEquals("2,00", popUpController.getOutlay(3.0, 5.0));
    }

}
