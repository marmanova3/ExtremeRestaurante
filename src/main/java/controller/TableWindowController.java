package controller;

import app.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.ItemsEntity;
import model.OrderItemEntity;
import model.OrdersEntity;
import model.TablesEntity;
import org.hibernate.Session;
import org.hibernate.query.Query;
import utils.HibernateUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

public class TableWindowController implements Initializable {

    private int tableId;
    private List<OrdersEntity> orders;

    @FXML
    private Label tableLabel, priceTotal;

    @FXML
    private TableView tableview;

    private ObservableList<OrderItemEntity> data = FXCollections.observableArrayList();

    public TableWindowController() {
    }

    private TablesEntity getThisTable() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        TablesEntity table = session.load(TablesEntity.class, tableId);
        session.close();
        return table;
    }

    public void initTable() {
        String id = MainWindowController.clickedTable;
        tableId = Character.getNumericValue(id.charAt(id.length() - 1)) - 1;
        tableLabel.setText("Table " + (tableId + 1));
    }

    public void initialize(URL location, ResourceBundle resources) {
        orders = new ArrayList<OrdersEntity>();
        initTable();

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        TablesEntity table = getThisTable();

        Query qry = session.createQuery("from OrdersEntity as o left join ItemsEntity as i  on o.item.id=i.id where o.paid=false and o.table=:id");
        qry.setParameter("id", table);

        List l = qry.list();
        Iterator it = l.iterator();

        double total = 0;
        while (it.hasNext()) {
            Object o[] = (Object[]) it.next();

            OrdersEntity oe = (OrdersEntity) o[0];
            ItemsEntity ie = (ItemsEntity) o[1];

            OrderItemEntity oie = new OrderItemEntity();
            oie.setName(ie.getName());
            oie.setPrice(oe.getPrice());

            data.add(oie);

            orders.add(oe);
            total += oe.getPrice();
        }
        tableview.setItems(data);

        priceTotal.setText(String.valueOf(total) + " €");

        session.getTransaction().commit();
        session.close();
    }

    private void reload() throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/windows/tableWindow.fxml"));
        Parent root = (Parent) loader.load();

        Stage stage = Main.mainStage;
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void payAll() throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        for (OrdersEntity order : orders) {
            order.setPaid(true);
            session.update(order);
            session.save(order);
        }

        //TO DO update cash_register +priceTotal
        session.flush();
        session.getTransaction().commit();
        session.close();

        reload();
    }

    @FXML
    private void addItemToOrders(MouseEvent event) throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        //TO DO zavesit na spravy Shape
        Button clickedItem = (Button) event.getSource();
        String clickedItemId = clickedItem.getId();
        int itemId = Integer.parseInt(clickedItemId.substring(4, (clickedItem.getId()).length()));
        ItemsEntity item = session.load(ItemsEntity.class, itemId);

        OrdersEntity order = new OrdersEntity();
        order.setPaid(false);
        order.setItem(item);
        order.setPrice(item.getPrice());
        order.setTable(getThisTable());
        orders.add(order);

        session.save(order);
        session.getTransaction().commit();
        session.close();

        reload();
    }

    @FXML
    private void handleBackAction() throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/windows/mainWindow.fxml"));
        Parent root = (Parent) loader.load();

        Stage stage = Main.mainStage;
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void handleMenuAction() throws Exception {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/windows/chooseItemsWindow.fxml"));
        Parent root = (Parent) loader.load();

        Stage stage = Main.mainStage;
        stage.setScene(new Scene(root));
        stage.show();
    }
}
