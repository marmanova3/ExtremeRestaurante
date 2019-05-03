package utils;

import javafx.collections.FXCollections;
import model.ItemsEntity;
import model.OrderItemEntity;
import model.OrdersEntity;
import model.TablesEntity;
import org.hibernate.Hibernate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class HibernateQueriesTest {

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Before
    public void setUp() {
        TestHelper.deleteAllOrders();
    }

    @Test
    public void shouldReturnOrderById() {
        ItemsEntity item = TestHelper.getItem(1);
        TablesEntity table = TestHelper.getTable(1);
        OrdersEntity testOrder = new OrdersEntity();
        testOrder.setItem(item);
        testOrder.setQuantity(2);
        testOrder.setTable(table);
        testOrder.setPrice(item.getPrice());
        testOrder.setPaid(true);

        int orderId = TestHelper.insertTableOrder(testOrder);
        OrdersEntity fetchedOrder = (OrdersEntity) Hibernate.unproxy(HibernateQueries.getOrderById(orderId));

        assertEquals(testOrder, fetchedOrder);
    }

    @Test
    public void shouldReturnListWithOneOccupiedTableNames() {
        TestHelper.insertTableOrder(1, 0, false, 1);
        List<String> occupiedTables = Arrays.asList("1");

        assertArrayEquals(occupiedTables.toArray(), HibernateQueries.getOccupiedTables().toArray());
    }

    @Test
    public void shouldReturnListWithNoneOccupiedTableNames() {
        TestHelper.insertTableOrder(1, 0, true, 1);
        List<String> occupiedTables = new ArrayList<>();

        assertArrayEquals(occupiedTables.toArray(), HibernateQueries.getOccupiedTables().toArray());
    }

    @Test
    public void shouldReturnListWithTwoOccupiedTableNames() {
        TestHelper.insertTableOrder(1, 0, true, 1);
        TestHelper.insertTableOrder(2, 0, false, 1);
        TestHelper.insertTableOrder(16, 7, false, 1);
        TestHelper.insertTableOrder(16, 6, true, 1);
        List<String> occupiedTables = Arrays.asList("1", "8");

        assertArrayEquals(occupiedTables.toArray(), HibernateQueries.getOccupiedTables().toArray());
    }

    @Test
    public void shouldReturnListWithTwoUnpaidOrders() {
        int tableId = 0;
        OrdersEntity unPaidOrder1 = TestHelper.insertTableOrder(1, tableId, false, 1);
        OrdersEntity unPaidOrder2 = TestHelper.insertTableOrder(2, tableId, false, 10);
        OrdersEntity paidOrder = TestHelper.insertTableOrder(4, tableId, true, 1);

        List<OrdersEntity> insertedUnpaidOrders = new ArrayList<>();
        insertedUnpaidOrders.add(unPaidOrder1);
        insertedUnpaidOrders.add(unPaidOrder2);

        TablesEntity table = HibernateQueries.getTableById(tableId);

        assertArrayEquals(insertedUnpaidOrders.toArray(), HibernateQueries.getUnpaidOrdersByTable(table).toArray());
    }

    @Test
    public void shouldReturnEmptyOrdersList() {
        int tableId = 1;
        OrdersEntity paidOrder1 = TestHelper.insertTableOrder(1, tableId, true, 1);
        OrdersEntity paidOrder2 = TestHelper.insertTableOrder(4, tableId, true, 1);

        List<OrdersEntity> insertedUnpaidOrders = new ArrayList<>();

        TablesEntity table = HibernateQueries.getTableById(tableId);

        assertArrayEquals(insertedUnpaidOrders.toArray(), HibernateQueries.getUnpaidOrdersByTable(table).toArray());
    }

    @Test
    public void tableShouldHaveZeroRows() {
        TablesEntity table = HibernateQueries.getTableById(0);
        assertEquals("0", TestHelper.getNumberOfOrdersForTable(table));
    }

    @Test
    public void tableShouldHaveOneRow() {
        TablesEntity table = HibernateQueries.getTableById(1);

        HibernateQueries.addItemToTableOrders(1, table, false);

        assertEquals("1", TestHelper.getNumberOfOrdersForTable(table));
    }

    @Test
    public void allOrdersShouldBePaid() {
        OrdersEntity unpaidOrder1 = TestHelper.insertTableOrder(1, 2, false, 2);
        OrdersEntity unpaidOrder2 = TestHelper.insertTableOrder(2, 3, false, 2);

        List<OrdersEntity> orders = Arrays.asList(unpaidOrder1, unpaidOrder2);

        HibernateQueries.payOrders(orders);

        List<OrdersEntity> paidOrders = TestHelper.getAllPaidOrders();

        assertArrayEquals(paidOrders.toArray(), orders.toArray());
    }

    @Test
    public void shouldReturnTableWidthId0() {
        TablesEntity table0 = new TablesEntity();
        table0.setId(0);
        table0.setName("1");

        TablesEntity fetchedTable = (TablesEntity) Hibernate.unproxy(HibernateQueries.getTableById(0));
        assertEquals(table0, fetchedTable);
    }

    @Test
    public void shouldReturnOrderItemsEntitiesByTable() {
        int tableId = 1;
        int itemOneId = 1;
        int itemTwoId = 2;
        OrdersEntity order1 = TestHelper.insertTableOrder(itemOneId, tableId, false, 2);
        OrdersEntity order2 = TestHelper.insertTableOrder(itemTwoId, tableId, true, 3);
        OrdersEntity order3 = TestHelper.insertTableOrder(itemTwoId, tableId, false, 1);

        ItemsEntity item1 = TestHelper.getItem(itemOneId);
        ItemsEntity item2 = TestHelper.getItem(itemTwoId);

        OrderItemEntity orderItemEntity1 = TestHelper.createOrderItemEntity(order1, item1);
        OrderItemEntity orderItemEntity2 = TestHelper.createOrderItemEntity(order3, item2);

        List<OrderItemEntity> orderItemEntities = FXCollections.observableArrayList(orderItemEntity1, orderItemEntity2);

        TablesEntity table = HibernateQueries.getTableById(tableId);

        List<OrderItemEntity> fetchedOrderItemEntities = HibernateQueries.getUnpaidOrderItemsEntitiesByTable(table);

        for (int i = 0; i < orderItemEntities.size(); i++) {
            OrderItemEntity oie1 = orderItemEntities.get(0);
            OrderItemEntity oie2 = fetchedOrderItemEntities.get(0);
            assertTrue(oie1.equals(oie2));
        }
    }

    @Test
    public void shouldDeleteOrder() {
        int itemId = 1;
        int tableId = 1;
        OrdersEntity order = TestHelper.insertTableOrder(itemId, tableId, false, 0);
        ItemsEntity item = TestHelper.getItem(itemId);
        OrderItemEntity orderItemEntity = TestHelper.createOrderItemEntity(order, item);

        TablesEntity table = HibernateQueries.getTableById(tableId);

        assertEquals("1", TestHelper.getNumberOfOrdersForTable(table));

        HibernateQueries.updateOrderByQuantity(orderItemEntity);

        assertEquals("0", TestHelper.getNumberOfOrdersForTable(table));
    }

    @Test
    public void shouldUpdateQuantity() {
        int itemId = 1;
        int tableId = 2;
        OrdersEntity order = TestHelper.insertTableOrder(itemId, tableId, false, 2);
        ItemsEntity item = TestHelper.getItem(itemId);
        OrderItemEntity orderItemEntity = TestHelper.createOrderItemEntity(order, item);
        orderItemEntity.setQuantity(55);

        HibernateQueries.updateOrderByQuantity(orderItemEntity);

        OrdersEntity fetchedOrder = HibernateQueries.getOrderById(order.getId());

        assertEquals(55, fetchedOrder.getQuantity());
    }

}
