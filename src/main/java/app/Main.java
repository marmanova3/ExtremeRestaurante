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
import model.TablesEntity;
import org.hibernate.Session;
import utils.HibernateUtil;

public class Main extends Application {
    public Main() {
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        TablesEntity table = new TablesEntity();
        table.setName("1");
        session.save(table);
        session.getTransaction().commit();
        session.close();
        System.out.println("Done");


        Parent root = (Parent) FXMLLoader.load(this.getClass().getResource("window.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300.0D, 275.0D));
        primaryStage.show();
    }
}
