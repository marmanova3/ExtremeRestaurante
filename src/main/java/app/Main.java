//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hibernate.Session;
import utils.HibernateUtil;

public class Main extends Application {
    public static Stage mainStage;
    public Main() {
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();


//        CategoriesEntity cat = new CategoriesEntity();
//        cat.setName("Hlavné jedlá");
//        session.save(cat);
//
//        CategoriesEntity cat2 = session.load(CategoriesEntity.class, 0);
//
//        ItemsEntity item = new ItemsEntity("Paradajková polievka", 5.0, cat2);
//        session.save(item);

//        ItemsEntity item = session.load(ItemsEntity.class, 1);
//        TablesEntity table = session.load(TablesEntity.class, 0);
//        OrdersEntity order = new OrdersEntity();
//        order.setItem(item);
//        order.setPaid(false);
//        order.setTable(table);
//        order.setPrice(5.0);
//        session.save(order);

//        Query qry = session.createQuery("select name, price from ItemsEntity where category='0'");
//
//        List l = qry.list();
//        System.out.println("Total Number Of Records : "+l.size());
//        Iterator it = l.iterator();
//
//        while(it.hasNext())
//        {
//            Object o[] = (Object[])it.next();
//
//            System.out.println("Item name : "+o[0]+ " Item price : "+o[1]);
//
//            System.out.println("----------------");
//        }
//
//
        session.getTransaction().commit();
        session.close();
        System.out.println("Done");


        Parent root = (Parent) FXMLLoader.load(this.getClass().getResource("/windows/mainWindow.fxml"));
        primaryStage.setTitle("Extreme Restaurante");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
