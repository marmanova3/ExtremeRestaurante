package utils;

import model.CategoriesEntity;
import model.ItemsEntity;
import model.OrdersEntity;
import model.TablesEntity;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HibernateQueries {

    public static OrdersEntity getOrder(int orderId) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        OrdersEntity order = session.load(OrdersEntity.class, orderId);
        session.close();
        return order;
    }

    public static List<String> findOccupiedTables(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query qry = session.createQuery(""+
                "SELECT t.id, t.name FROM OrdersEntity as o LEFT JOIN TablesEntity as t on o.table.id = t.id "+
                "GROUP BY t.id " +
                "HAVING bool_and(o.paid) = false");

        List l = qry.list();
        Iterator it = l.iterator();

        List<String> occupiedTablesNames = new ArrayList<>();
        while (it.hasNext()) {
            Object o[] = (Object[]) it.next();
            occupiedTablesNames.add(o[1].toString());
        }
        session.getTransaction().commit();
        session.close();

        return occupiedTablesNames;
    }

    public static List<OrdersEntity> getOrders(TablesEntity table) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from OrdersEntity where paid=false and table=:table");
        query.setParameter("table", table);

        List<OrdersEntity> orders = query.list();

        session.getTransaction().commit();
        session.close();

        return orders;
    }

    public static void addItemToTableOrders(int itemId, TablesEntity table) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        ItemsEntity item = session.load(ItemsEntity.class, itemId);

        boolean newOrder = true;
        for (OrdersEntity order : HibernateQueries.getOrders(table)) {
            if (order.getItem().getId() == itemId) {
                order.setQuantity(order.getQuantity() + 1);
                newOrder = false;
                session.update(order);
            }
        }
        if (newOrder) {
            OrdersEntity order = new OrdersEntity();
            order.setPaid(false);
            order.setItem(item);
            order.setPrice(item.getPrice());
            order.setTable(table);
            order.setQuantity(1);
            session.save(order);
        }
        session.getTransaction().commit();
        session.close();
    }

    public static TablesEntity getTableById(int tableId){
        Session session = HibernateUtil.getSessionFactory().openSession();
        TablesEntity table = session.load(TablesEntity.class, tableId);
        session.close();
        return table;
    }

    public static List<ItemsEntity> getItemsByCategoryId(int categoryId) {
        List<ItemsEntity> items = new ArrayList<>();

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("select id, name, price, category from ItemsEntity where category =: categoryId");
        query.setParameter("categoryId", categoryId);

        Iterator iterator = query.list().iterator();

        while (iterator.hasNext()) {
            Object[] object = (Object[]) iterator.next();
            ItemsEntity item = new ItemsEntity();
            item.setId((int) object[0]);
            item.setName(object[1].toString());
            item.setPrice((double) object[2]);
            CategoriesEntity category = (CategoriesEntity) object[3];
            item.setCategory(category);
            items.add(item);
        }

        session.getTransaction().commit();
        session.close();

        return items;
    }

    public static void payOrders(List<OrdersEntity> orders){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        for (OrdersEntity order : orders) {
            order.setPaid(true);
            session.update(order);
            session.save(order);
        }

        session.flush();
        session.getTransaction().commit();
        session.close();
        session = null;
    }
}
