package controller;

import app.Scenes;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import model.ItemsEntity;
import model.OrderItemEntity;
import model.OrdersEntity;
import model.TablesEntity;
import org.hibernate.Session;
import org.hibernate.query.Query;
import utils.HibernateUtil;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

public class TableWindowController extends AbstractController {

    public static int tableId;
    private double total;

    @FXML
    private Label tableLabel, priceTotal;

    @FXML
    private TableView tableview;

    @FXML
    private TableColumn col1, col2;

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
        tableLabel.setText("TABLE " + (tableId + 1));
    }

    public void updateTotal() {
        total = 0;
        for (OrdersEntity order : getOrders()) {
            total += order.getPrice() * order.getQuantity();
        }
        total = Math.round(total * 100.0) / 100.0;
        priceTotal.setText(String.valueOf(total) + " €");
        System.out.println(total);
    }

    public List<OrdersEntity> getOrders() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from OrdersEntity where paid=false and table=:id");
        query.setParameter("id", getThisTable());

        List orders = query.list();

        session.getTransaction().commit();
        session.close();

        return orders;
    }

    public void updateOrder(OrderItemEntity oe) {
        final Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        int id = oe.getOrderId();
        OrdersEntity order = session.load(OrdersEntity.class, id);
        if (oe.getQuantity() == 0) {
            session.remove(order);
        } else {
            order.setQuantity(oe.getQuantity());
            session.save(order);
        }
        session.getTransaction().commit();
        session.close();
        reload();
    }

    public void initialize(URL location, ResourceBundle resources) {
        initTable();

        final Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        TablesEntity table = getThisTable();

        Query qry = session.createQuery("from OrdersEntity as o left join ItemsEntity as i  on o.item.id=i.id where o.paid=false and o.table=:id");
        qry.setParameter("id", table);

        List l = qry.list();
        Iterator it = l.iterator();

        tableview.setEditable(true);
        tableview.setStyle("-fx-background-color: black");

        Callback<TableColumn, TableCell> cellFactory =
                new Callback<TableColumn, TableCell>() {
                    public TableCell call(TableColumn p) {
                        return new EditingCell();
                    }
                };

        TableColumn col3 = new TableColumn("Quantity");
        col3.setMinWidth(75);
        col3.setCellFactory(cellFactory);
        col3.setCellValueFactory(new PropertyValueFactory<OrderItemEntity, Integer>("quantity"));
        col3.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<OrderItemEntity, Integer>>() {
                    public void handle(TableColumn.CellEditEvent<OrderItemEntity, Integer> t) {
                        OrderItemEntity temp = (OrderItemEntity) t.getTableView().getItems().get(
                                t.getTablePosition().getRow());
                        temp.setQuantity(t.getNewValue());
                        updateOrder(temp);
                    }
                });

        col1.setStyle("-fx-background-color: black; -fx-text-fill: white");
        col2.setStyle("-fx-background-color: black; -fx-text-fill: white");
        col3.setStyle("-fx-background-color: black; -fx-text-fill: white");

        tableview.getColumns().addAll(col3);

        while (it.hasNext()) {
            Object o[] = (Object[]) it.next();

            OrdersEntity oe = (OrdersEntity) o[0];
            ItemsEntity ie = (ItemsEntity) o[1];

            OrderItemEntity oie = new OrderItemEntity();
            oie.setName(ie.getName());
            oie.setPrice(oe.getPrice());
            oie.setQuantity(oe.getQuantity());
            oie.setOrderId(oe.getId());

            data.add(oie);
        }

        tableview.setItems(data);

        updateTotal();

        session.getTransaction().commit();
        session.close();
    }

    private void reload() {
        redirect(Scenes.TABLE_WINDOW);
    }

    @FXML
    private void payAll() throws Exception {
        showPopupWindow(getOrders());
        reload();
    }

    @FXML
    private void devidePayment() throws Exception {
//        for (OrdersEntity order: getOrders()) {
//            if
//        }
//        showPopupWindow();
//        reload();
    }

    @FXML
    //sluzi docastne na pridavanie dat, naviazane na Print button
    private void addItemToOrders(MouseEvent event) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        //TO DO zavesit na spravy Shape
        Button clickedItem = (Button) event.getSource();
        String clickedItemId = clickedItem.getId();
        int itemId = Integer.parseInt(clickedItemId.substring(4, (clickedItem.getId()).length()));
        ItemsEntity item = session.load(ItemsEntity.class, itemId);

        boolean newOrder = true;
        for (OrdersEntity order : getOrders()) {
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
            order.setTable(getThisTable());
            order.setQuantity(1);
            session.save(order);
        }

        session.getTransaction().commit();
        session.close();

        reload();
    }

    @FXML
    private void handleBackAction() {
        redirect(Scenes.MAIN_WINDOW);
    }

    @FXML
    private void handleMenuAction() {
        redirect(Scenes.CHOOSE_ITEMS_WINDOW);
    }

    private void showPopupWindow(List<OrdersEntity> orders) throws Exception {

        FXMLLoader loader = getSceneLoader(Scenes.POP_UP_WINDOW);
        Parent root = (Parent) loader.load();
        PopUpController popupController = loader.getController();

        Scene scene = new Scene(root);
        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.UNDECORATED);
        if (this.main != null) {
            popupStage.initOwner(main.getPrimaryStage());
        }
        popupController.setStage(popupStage);
        popupController.setPriceToPay(this.total);
        popupController.setOrders(orders);
        // TODO posielam data na vypis bloku = this.data su vsetky items bude treba zmenit ked sa ucet bude delit
        popupController.setOrderItems(this.data);
        popupController.setTableId(tableId);
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    class EditingCell extends TableCell<OrderItemEntity, Integer> {

        private TextField textField;

        public EditingCell() {
        }

        @Override
        public void startEdit() {
            super.startEdit();

            if (textField == null) {
                createTextField();
            }

            setGraphic(textField);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//            textField.selectAll();
            Platform.runLater(new Runnable() {
                public void run() {
                    textField.requestFocus();
                    textField.selectAll();
                }
            });
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText(String.valueOf(getItem()));
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }

        public void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setGraphic(textField);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                } else {
                    setText(getString());
                    setContentDisplay(ContentDisplay.TEXT_ONLY);
                }
            }
        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {

                public void handle(KeyEvent t) {
                    if (t.getCode() == KeyCode.ENTER) {
                        commitEdit(Integer.parseInt(textField.getText()));
                    } else if (t.getCode() == KeyCode.ESCAPE) {
                        cancelEdit();
                    }
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }
}
