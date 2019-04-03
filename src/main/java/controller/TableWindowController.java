package controller;

import app.Scenes;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import model.OrderItemEntity;
import model.OrdersEntity;
import model.TablesEntity;
import utils.EditingCell;
import utils.HibernateQueries;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TableWindowController extends AbstractController {

    private static final String BUTTON_TEXT_DIVIDE = "Divide";
    private static final String BUTTON_TEXT_CANCEL = "Cancel";
    private static final String TABLE_COLUMN_STYLE = "-fx-background-color: black; -fx-text-fill: white";
    private static final String TABLE_VIEW_STYLE = "-fx-background-color: black";

    private static final String QUANTITY_COLUMN_NAME = "Quantity";
    private static final String PAY_COLUMN_NAME = "Pay";
    private static final String REMOVE_COLUMN_NAME = "Remove";
    private static final int QUANTITY_COLUMN_WIDTH = 75;
    private static final int PAY_COLUMN_WIDTH = 50;

    private static int tableId;
    private double total;
    private boolean dividePayment;
    ObservableList<OrderItemEntity> orderItemEntities;
    private TablesEntity table;

    @FXML private Label tableLabel, priceTotal;
    @FXML private TableView tableview;
    @FXML private TableColumn col1, col2, col3, col4, col5;
    @FXML private Button divideButton;


    public void initialize(URL location, ResourceBundle resources) {
        setTableInfo();
        setTableEntity();
        setDefaultDivideInfo();
        addQuantityColumn();
        addRemoveColumn();
        setTableViewAttributes();
        fillTableView();
        updateTotal(getOrders());
    }

    @FXML
    private void handleBackAction() {
        redirect(Scenes.MAIN_WINDOW);
    }

    @FXML
    private void handleMenuAction() {
        redirect(Scenes.CHOOSE_ITEMS_WINDOW);
    }

    @FXML
    private void pay() {
        List<OrdersEntity> ordersToPay = getOrdersByDividePayment();
        showPopupWindow(ordersToPay);
        reload();
    }

    @FXML
    private void handleDivideButton() {
        dividePayment = !dividePayment;
        if (dividePayment) {
            divideButton.setText(BUTTON_TEXT_CANCEL);
            addDividePaymentColumn();
            updateTotal(getDividedOrders());
        } else {
            divideButton.setText(BUTTON_TEXT_DIVIDE);
            removeDividePaymentColumn();
            updateTotal(getOrders());
        }
    }

    private void setTableViewAttributes(){
        tableview.setEditable(true);
        tableview.setStyle(TABLE_VIEW_STYLE);
        col1.setStyle(TABLE_COLUMN_STYLE);
        col2.setStyle(TABLE_COLUMN_STYLE);
        col3.setStyle(TABLE_COLUMN_STYLE);
    }

    private void addQuantityColumn(){
        Callback<TableColumn, TableCell> cellFactory = p -> new EditingCell();
        col3 = new TableColumn(QUANTITY_COLUMN_NAME);
        col3.setMinWidth(QUANTITY_COLUMN_WIDTH);
        col3.setCellFactory(cellFactory);
        col3.setCellValueFactory(new PropertyValueFactory<OrderItemEntity, Integer>("quantity"));
        setOnEditCommitToColumn(col3);
        tableview.getColumns().addAll(col3);
    }

    private void setOnEditCommitToColumn(TableColumn tableColumn){
        tableColumn.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<OrderItemEntity, Integer>>() {
                public void handle(TableColumn.CellEditEvent<OrderItemEntity, Integer> t) {
                    OrderItemEntity orderItemEntity = (OrderItemEntity) t.getTableView().getItems().get(
                            t.getTablePosition().getRow());
                    orderItemEntity.setQuantity(t.getNewValue());
                    updateOrderByQuantity(orderItemEntity);
                }
            });
    }

    private void addDividePaymentColumn() {
        col4 = new TableColumn(PAY_COLUMN_NAME);
        col4.setMinWidth(PAY_COLUMN_WIDTH);
        col4.setStyle(TABLE_COLUMN_STYLE);
        addCheckboxesToColumn(col4);
        tableview.getColumns().addAll(col4);
    }

    private void addRemoveColumn() {
        col5 = new TableColumn(REMOVE_COLUMN_NAME);
        col5.setMinWidth(PAY_COLUMN_WIDTH);
        col5.setStyle(TABLE_COLUMN_STYLE);
        addRemoveButtonsToColumn(col5);
        tableview.getColumns().addAll(col5);
    }

    private void addCheckboxesToColumn(TableColumn tableColumn){
        tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<OrderItemEntity, CheckBox>, ObservableValue<CheckBox>>() {
            public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<OrderItemEntity, CheckBox> arg0) {
                OrderItemEntity orderItemEntity = arg0.getValue();
                CheckBox checkBox = new CheckBox();
                checkBox.selectedProperty().setValue(orderItemEntity.getCheckbox());
                addListenersToCheckbox(checkBox, orderItemEntity);
                checkBox.setAlignment(Pos.CENTER);
                return new SimpleObjectProperty<CheckBox>(checkBox);
            }
        });
    }

    private void addListenersToCheckbox(CheckBox checkbox, OrderItemEntity orderItemEntity){
        checkbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
                orderItemEntity.setCheckbox(new_val);
                updateTotal(getDividedOrders());
            }
        });
    }

    private void addRemoveButtonsToColumn(TableColumn tableColumn) {
        tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<OrderItemEntity, Button>, ObservableValue<Button>>() {
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<OrderItemEntity, Button> arg0) {
                OrderItemEntity orderItemEntity = arg0.getValue();
                Button button = new Button(REMOVE_COLUMN_NAME);
                button.setId(Integer.toString(orderItemEntity.getOrderId()));
                button.setAlignment(Pos.CENTER);
                button.setCursor(Cursor.HAND);
                button.setOnMouseClicked(event ->
                        removeOrderItem(orderItemEntity.getOrderId()));
                return new SimpleObjectProperty<Button>(button);
            }
        });
    }

    private void removeOrderItem(int orderId) {
        HibernateQueries.deleteOrderById(orderId);
        fillTableView();
    }


    private void fillTableView(){
        orderItemEntities = HibernateQueries.getUnpaidOrderItemsEntitiesByTable(table);
        tableview.setItems(orderItemEntities);
    }

    private void setTableEntity(){
        table = HibernateQueries.getTableById(tableId);
    }

    public void setTableInfo() {
        String id = MainWindowController.getClickedTable();
        tableId = Character.getNumericValue(getLastCharacter(id)) - 1;
        tableLabel.setText("TABLE " + (tableId + 1));
    }

    private List<OrdersEntity> getOrdersByDividePayment(){
        if (dividePayment) {
            return getDividedOrders();
        }
        return getOrders();
    }

    private List<OrderItemEntity> getOrderItemsEntitiesByDividePayment(){
        if (dividePayment) {
            return getDividedOrderItems();
        }
        return orderItemEntities;
    }

    private void setDefaultDivideInfo(){
        divideButton.setText(BUTTON_TEXT_DIVIDE);
        dividePayment = false;
    }

    public char getLastCharacter(String input) {
        return input.length() > 0 ? input.charAt(input.length() - 1) : ' ';
    }

    public static int getTableId(){
        return tableId;
    }

    public void updateTotal(List<OrdersEntity> orders) {
        total = getTotalCountOfOrders(orders);
        priceTotal.setText(total + " €");
    }

    public double getTotalCountOfOrders(List<OrdersEntity> orders) {
        double sum = 0;
        for (OrdersEntity order : orders) {
            sum += order.getPrice() * order.getQuantity();
        }
        sum = Math.round(sum * 100.0) / 100.0;
        return sum;
    }

    public List<OrdersEntity> getOrders() {
        return HibernateQueries.getUnpaidOrdersByTable(table);
    }

    public void updateOrderByQuantity(OrderItemEntity orderItemEntity) {
        HibernateQueries.updateOrderByQuantity(orderItemEntity);
        reload();
    }

    private void reload() {
        redirect(Scenes.TABLE_WINDOW);
    }

    private void removeDividePaymentColumn() {
        tableview.getColumns().remove(3);
    }

    private List<OrdersEntity> getDividedOrders() {
        List<OrdersEntity> orders = new ArrayList<>();
        for (Object row : tableview.getItems()) {
            OrderItemEntity orderItem = (OrderItemEntity) row;
            if (orderItem.getCheckbox()) {
                OrdersEntity orderEntity = HibernateQueries.getOrderById(orderItem.getOrderId());
                orders.add(orderEntity);
            }
        }
        return orders;
    }

    private List<OrderItemEntity> getDividedOrderItems() {
        List<OrderItemEntity> orderItems = new ArrayList<>();
        for (Object row : tableview.getItems()) {
            OrderItemEntity orderItem = (OrderItemEntity) row;
            if (orderItem.getCheckbox()) {
                orderItems.add(orderItem);
            }
        }
        return orderItems;
    }

    private void showPopupWindow(List<OrdersEntity> ordersToPay) {
        FXMLLoader loader = getSceneLoader(Scenes.POP_UP_WINDOW);
        Parent root = getParent(loader);
        PopUpController popupController = loader.getController();

        Scene scene = new Scene(root);
        Stage popupStage = new Stage();

        popupStage.initStyle(StageStyle.UNDECORATED);
        if (this.main != null) {
            popupStage.initOwner(main.getPrimaryStage());
        }
        popupController.setStage(popupStage);
        popupController.setPriceToPay(this.total);
        popupController.setOrders(ordersToPay);
        List<OrderItemEntity> receipt = getOrderItemsEntitiesByDividePayment();

        popupController.setOrderItems(receipt);
        popupController.setTableId(tableId);
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

}