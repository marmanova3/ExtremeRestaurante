package utils;

import model.ItemsEntity;
import model.OrdersEntity;
import model.TablesEntity;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class TestHelper {

    public static void deleteAllOrders() {
        final Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query qry = session.createQuery("DELETE FROM OrdersEntity");
        qry.executeUpdate();

        session.getTransaction().commit();
        session.close();
    }

    public static void insertTableOrder(int itemId, int tableId, boolean paid, int quantity) {
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
        session.beginTransaction();
        TablesEntity table = session.load(TablesEntity.class, tableId);
        session.getTransaction().commit();
        session.close();

        return table;
    }

    public static ItemsEntity getItem(int itemId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        ItemsEntity item = session.load(ItemsEntity.class, itemId);
        session.getTransaction().commit();
        session.close();

        return item;
    }
}
