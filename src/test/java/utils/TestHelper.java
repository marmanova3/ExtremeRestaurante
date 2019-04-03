package utils;

import model.ItemsEntity;
import model.OrderItemEntity;
import model.OrdersEntity;
import model.TablesEntity;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class TestHelper {

    public static void deleteAllOrders() {
        final Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query qry = session.createQuery("DELETE FROM OrdersEntity");
        qry.executeUpdate();

        session.getTransaction().commit();
        session.close();
    }

    public static OrdersEntity insertTableOrder(int itemId, int tableId, boolean paid, int quantity) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        ItemsEntity item = session.load(ItemsEntity.class, itemId);
        TablesEntity table = session.load(TablesEntity.class, tableId);

        OrdersEntity order = new OrdersEntity();
        order.setPaid(paid);
        order.setPrice(item.getPrice());
        order.setItem(item);
        order.setTable(table);
        order.setQuantity(quantity);
        session.save(order);

        session.getTransaction().commit();
        session.close();

        return order;
    }

    public static int insertTableOrder(OrdersEntity order) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        session.save(order);

        session.getTransaction().commit();
        session.close();

        return order.getId();
    }

    public static TablesEntity getTable(int tableId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        TablesEntity table = session.load(TablesEntity.class, tableId);
        session.close();

        return table;
    }

    public static ItemsEntity getItem(int itemId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        ItemsEntity item = session.load(ItemsEntity.class, itemId);
        session.close();

        return item;
    }


    public static String getNumberOfOrdersForTable(TablesEntity table) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query qry = session.createQuery("SELECT count(*) FROM OrdersEntity o WHERE o.table =:table");
        qry.setParameter("table", table);

        Long countRows = (Long) qry.uniqueResult();

        session.getTransaction().commit();
        session.close();

        return countRows.toString();
    }

    public static List<OrdersEntity> getAllPaidOrders() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from OrdersEntity where paid=true");

        List<OrdersEntity> orders = query.list();

        session.getTransaction().commit();
        session.close();

        return orders;
    }

    public static OrderItemEntity createOrderItemEntity(OrdersEntity order, ItemsEntity item) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderId(order.getId());
        orderItemEntity.setName(item.getName());
        orderItemEntity.setPrice(order.getPrice());
        orderItemEntity.setQuantity(order.getQuantity());
        orderItemEntity.setCheckbox(false);

        return orderItemEntity;
    }
}
