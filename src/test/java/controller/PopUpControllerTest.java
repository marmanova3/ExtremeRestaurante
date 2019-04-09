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
    public void shouldReturnDifference() {
        assertEquals(new Double(1.22), popUpController.getOutlay(3.78, 5.00));
    }

    @Test
    public void shouldReturnDifference1() {
        assertEquals(new Double(2.0), popUpController.getOutlay(3.0, 5.0));
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
