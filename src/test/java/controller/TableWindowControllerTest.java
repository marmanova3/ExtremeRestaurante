package controller;

import model.OrdersEntity;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TableWindowControllerTest {
    TableWindowController tableWindowController;

    @Before
    public void setUp() {
        tableWindowController = new TableWindowController();
    }

    @Test
    public void shouldReturnLastCharacter() {
        assertEquals('1', tableWindowController.getLastCharacter("tab1"));
    }

    @Test
    public void shouldReturnEmptyChar() {
        assertEquals(' ', tableWindowController.getLastCharacter(""));
    }

    @Test
    public void shouldReturnEmptyChar1() {
        assertEquals('b', tableWindowController.getLastCharacter("tab"));
    }

    @Test
    public void shouldReturnTotalPrice() {
        OrdersEntity order1 = new OrdersEntity();
        order1.setQuantity(2);
        order1.setPrice(1.0);

        OrdersEntity order2 = new OrdersEntity();
        order2.setQuantity(5);
        order2.setPrice(1.59);

        List<OrdersEntity> orders = Arrays.asList(order1, order2);

        assertEquals(9.95, tableWindowController.getTotalCountOfOrders(orders), 0.00);
    }
}
