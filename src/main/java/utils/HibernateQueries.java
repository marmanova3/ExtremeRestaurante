package utils;

import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HibernateQueries {

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
}
