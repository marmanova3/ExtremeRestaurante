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

    @Test
    public void discountValidationGreaterThan100() {
        assertFalse(popUpController.discoutIsValid(100.1));
    }

    @Test
    public void discountValidationLowerThan0() {
        assertFalse(popUpController.discoutIsValid(-1.0));
    }

    @Test
    public void validDiscountValidation() {
        assertTrue(popUpController.discoutIsValid(20.0));
    }

    @Test
    public void discoutedPriceCorrect() {
        popUpController.setPriceToPay(100);
        assertEquals((Double) 80.0, popUpController.discountPriceToPay(20.0));
        assertEquals((Double) 65.0, popUpController.discountPriceToPay(35.0));
        assertEquals((Double) 0.0, popUpController.discountPriceToPay(100.0));
        assertEquals((Double) 100.0, popUpController.discountPriceToPay(0.0));
    }

    @Test
    public void discoutedPriceDouble() {
        popUpController.setPriceToPay(5.70);
        assertEquals((Double) 5.13, popUpController.discountPriceToPay(10.0));

    }
}
