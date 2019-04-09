package controller;

import app.Scenes;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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
    private static final String TABLE_COLUMN_STYLE1 = "-fx-background-color: black; -fx-text-fill: white;";
    private static final String TABLE_COLUMN_STYLE2 = "-fx-background-color: black; -fx-text-fill: white; -fx-alignment: CENTER;";
    private static final String TABLE_VIEW_STYLE = "-fx-background-color: black";
    private static final String COLOR_WHITE = "#ffffff";
    private static final String COLOR_DARK = "#333333";

    private static final String QUANTITY_COLUMN_NAME = "Quantity";
    private static final String PAY_COLUMN_NAME = "Pay";
    private static final String QTY_COLUMN_NAME = "Qty";
    private static final String REMOVE_COLUMN_NAME = "Remove";
    private static final int QUANTITY_COLUMN_WIDTH = 75;
    private static final int PAY_COLUMN_WIDTH = 50;
    private static final int REMOVE_COLUMN_WIDTH = 85;
    private static final int NAME_COLUMN_WIDTH = 256;
    private static final int PRICE_COLUMN_WIDTH = 65;

    private static int tableId;
    private double total;
    private boolean dividePayment;
    ObservableList<OrderItemEntity> orderItemEntities;
    private TablesEntity table;

    @FXML private Label tableLabel, priceTotal;
    @FXML private TableView tableview;
    @FXML
    private TableColumn col1, col2, col3, col4, col5, col6, emptyColumn, emptyColumn2;
    @FXML private Button divideButton;


    public void initialize(URL location, ResourceBundle resources) {
        setTableInfo();
        setTableEntity();
        setDefaultDivideInfo();
        addQuantityColumn();
        addRemoveColumn();
        addEmptyColumns();
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
        removeLastColumns();
        if (dividePayment) {
            divideButton.setText(BUTTON_TEXT_CANCEL);
            addDividePaymentColumns();
            setPriceTotal(getDividedOrdersPrice());
        } else {
            divideButton.setText(BUTTON_TEXT_DIVIDE);
            addEmptyColumns();
            updateTotal(getOrders());
        }
    }

    private void setTableViewAttributes(){
        tableview.setEditable(true);
        tableview.setStyle(TABLE_VIEW_STYLE);
        col1.setStyle(TABLE_COLUMN_STYLE1);
        col1.setPrefWidth(NAME_COLUMN_WIDTH);
        col2.setPrefWidth(PRICE_COLUMN_WIDTH);
        col2.setStyle(TABLE_COLUMN_STYLE1);
        col3.setStyle(TABLE_COLUMN_STYLE1);
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

    private void setOnEditCommitWithCheckToColumn(TableColumn tableColumn) {
        tableColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<OrderItemEntity, Integer>>() {
                    public void handle(TableColumn.CellEditEvent<OrderItemEntity, Integer> t) {
                        OrderItemEntity orderItemEntity = (OrderItemEntity) t.getTableView().getItems().get(
                                t.getTablePosition().getRow());
                        if (t.getNewValue() >= 0 && t.getNewValue() <= orderItemEntity.getQuantity()) {
                            orderItemEntity.setDividedQuantity(t.getNewValue());
                            setPriceTotal(getDividedOrdersPrice());
                        }
                        //TODO oldvalue
                    }
                });
    }

    private void addDividePaymentColumns() {
        col4 = new TableColumn(PAY_COLUMN_NAME);
        col4.setMinWidth(PAY_COLUMN_WIDTH);
        col4.setStyle(TABLE_COLUMN_STYLE2);
        addCheckboxesToColumn(col4);

        Callback<TableColumn, TableCell> cellFactory = p -> new EditingCell();
        col6 = new TableColumn(QTY_COLUMN_NAME);
        col6.setMinWidth(PAY_COLUMN_WIDTH);
        col6.setStyle(TABLE_COLUMN_STYLE2);
        col6.setCellFactory(cellFactory);
        col6.setCellValueFactory(new PropertyValueFactory<OrderItemEntity, Integer>("dividedQuantity"));
        setOnEditCommitWithCheckToColumn(col6);

        tableview.getColumns().addAll(col4, col6);
    }

    private void addEmptyColumns() {
        emptyColumn = new TableColumn(PAY_COLUMN_NAME);
        emptyColumn.setMinWidth(PAY_COLUMN_WIDTH);
        emptyColumn.setStyle(TABLE_COLUMN_STYLE2);

        emptyColumn2 = new TableColumn(QTY_COLUMN_NAME);
        emptyColumn2.setMinWidth(PAY_COLUMN_WIDTH);
        emptyColumn2.setStyle(TABLE_COLUMN_STYLE2);

        tableview.getColumns().addAll(emptyColumn, emptyColumn2);
    }

    private void addRemoveColumn() {
        col5 = new TableColumn(REMOVE_COLUMN_NAME);
        col5.setMinWidth(REMOVE_COLUMN_WIDTH);
        col5.setStyle(TABLE_COLUMN_STYLE2);
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
                setPriceTotal(getDividedOrdersPrice());
            }
        });
    }

    private void addRemoveButtonsToColumn(TableColumn tableColumn) {
        tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<OrderItemEntity, Button>, ObservableValue<Button>>() {
            public ObservableValue<Button> call(TableColumn.CellDataFeatures<OrderItemEntity, Button> arg0) {
                OrderItemEntity orderItemEntity = arg0.getValue();
                Button button = new Button(REMOVE_COLUMN_NAME);
                button.setTextFill(Paint.valueOf(COLOR_WHITE));
                button.setBackground(
                        new Background(
                                new BackgroundFill(Color.web(COLOR_DARK),
                                        CornerRadii.EMPTY,
                                        Insets.EMPTY)));
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
        if (!dividePayment) {
            updateTotal(getOrders());
        } else {
            setPriceTotal(getDividedOrdersPrice());
        }
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

    public void setPriceTotal(Double total) {
        this.total = total;
        priceTotal.setText(String.format("%s €", total));
    }

    private List<OrdersEntity> getOrdersByDividePayment(){
        if (dividePayment) {
            return dividedOrders();
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
        reload(); //je nutny?
    }

    private void reload() {
        redirect(Scenes.TABLE_WINDOW);
    }

    private void removeLastColumns() {
        tableview.getColumns().remove(5);
        tableview.getColumns().remove(4);
    }

    private Double getDividedOrdersPrice() {
        Double price = 0.0;
        for (Object row : tableview.getItems()) {
            OrderItemEntity orderItem = (OrderItemEntity) row;
            if (orderItem.getCheckbox()) {
                price += (orderItem.getPrice() * orderItem.getDividedQuantity());
            }
        }
        price = Math.round(price * 100.0) / 100.0;
        return price;
    }

    private List<OrdersEntity> dividedOrders() {
        List<OrdersEntity> orders = new ArrayList<>();
        for (Object row : tableview.getItems()) {
            OrderItemEntity orderItem = (OrderItemEntity) row;
            if (orderItem.getCheckbox()) {
                OrdersEntity orderEntity = HibernateQueries.getOrderById(orderItem.getOrderId());
                if (orderItem.getDividedQuantity() < orderItem.getQuantity()) {
                    //update order quantity
                    int newQuantity = orderEntity.getQuantity() - orderItem.getDividedQuantity();
                    orderItem.setQuantity(newQuantity);
                    HibernateQueries.updateOrderByQuantity(orderItem);
                    //create new order
                    OrdersEntity newOrderEntity = HibernateQueries.createOrderEntity(orderEntity, orderItem.getDividedQuantity());
                    orders.add(newOrderEntity);
                } else {
                    orders.add(orderEntity);
                }
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
        popupController.setTableWindowController(this);
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
