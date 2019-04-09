package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.OrderItemEntity;
import model.OrdersEntity;
import utils.HibernateQueries;
import utils.NumberUtils;
import utils.ReceiptPrinter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PopUpController extends AbstractController {

    private static final String COLOR_GREEN = "#2ec60b";
    private static final String COLOR_RED = "#c90b0b";

    private static final String SUCCESS_MESSAGE = "Payment successful";
    private static final String ERROR_MESSAGE = "Payment denied";
    private static final String DISCOUNT_ERROR_MESSAGE = "Maximum discount is 100%";

    @FXML
    private Button confirmBtn, printBtn;
    @FXML
    private TextField cashInput, discountInput;
    @FXML
    private Label outlayOutput, message, tableNumber;
    private Stage stage;
    private double priceToPay;
    private List<OrdersEntity> orders;
    private List<OrderItemEntity> orderItems;
    private int tableId;
    private TableWindowController controller;

    public void initialize(URL url, ResourceBundle rb) {
        message.setVisible(false);
        setTableInfo();
        setCashInputListener();
        setDiscountInputListener();
    }

    private void setCashInputListener() {
        cashInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!NumberUtils.hasDoubleFormat(newValue)) {
                    cashInput.setText(oldValue);
                }
            }
        });
    }

    private void setDiscountInputListener() {
        discountInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!NumberUtils.hasDoubleFormat(newValue)) {
                    discountInput.setText(oldValue);
                }
                if (!"".equals(newValue)) {
                    Double discountedPrice = discountPriceToPay(getDiscountInput());
                    discountedPrice = NumberUtils.getRoundedDecimalNumber(discountedPrice, 2);
                    controller.setPriceTotal((String.valueOf(discountedPrice)));
                } else {
                    controller.setPriceTotal((String.valueOf(priceToPay)));
                }
            }
        });
    }

    @FXML
    private void closeWindow() {
        closeStage();
    }

    @FXML
    private void confirm() {
        if (!discoutIsValid(getDiscountInput())) {
            showErrorMessage(DISCOUNT_ERROR_MESSAGE);
            return;
        }
        if (!NumberUtils.isValidDoubleFormat(cashInput.getText())) {
            showErrorMessage(ERROR_MESSAGE);
            return;
        }

        Double discountedPrice = discountPriceToPay(getDiscountInput());
        Double receivedCashFromCustomer = Double.parseDouble(cashInput.getText());
        if (!NumberUtils.isGreaterOrEqual(receivedCashFromCustomer, discountedPrice)) {
            showErrorMessage(ERROR_MESSAGE);
            return;
        }

        setPriceToPay(discountedPrice);
        payOrders();
        showSuccessMessage();
        String roundedOutlayString = NumberUtils.getRoundedDecimalNumber(getOutlay(priceToPay, getCashInput()), 2).toString();
        outlayOutput.setText(roundedOutlayString);
        confirmBtn.setDisable(true);
        printBtn.setDisable(false);
        cashInput.setDisable(true);
        discountInput.setDisable(true);
    }

    public boolean discoutIsValid(Double percentage) {
        return percentage <= 100 && percentage >= 0;
    }

    public Double discountPriceToPay(Double percentage) {
        Double newPrice = priceToPay;
        if (percentage != 0) {
            newPrice -= priceToPay * (percentage / 100);
        }
        return newPrice;
    }

    //TODO add discount to items in receipt
    @FXML
    private void printReceipt() {
        String total = NumberUtils.getRoundedDecimalNumber(priceToPay, 2).toString();
        String cash = NumberUtils.getRoundedDecimalNumber(getCashInput(), 2).toString();
        String outlay = NumberUtils.getRoundedDecimalNumber(getOutlay(priceToPay, getCashInput()), 2).toString();
        ReceiptPrinter.print(total, cash, outlay, orderItems);
    }

    private Double getCashInput() {
        return Double.parseDouble(cashInput.getText());
    }

    private Double getDiscountInput() {
        String discount = discountInput.getText();
        if ("".equals(discount)) {
            return Double.valueOf(0);
        }
        return Double.parseDouble(discount);
    }

    private void closeStage() {
        if (this.stage != null) {
            this.stage.close();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    private void setTableInfo() {
        tableNumber.setText("TABLE " + (tableId + 1));
    }

    public void setPriceToPay(double priceToPay) {
        this.priceToPay = priceToPay;
    }

    public void setTableWindowController(TableWindowController controller) {
        this.controller = controller;
    }

    public void setOrders(List<OrdersEntity> orders) {
        this.orders = orders;
    }

    public void setOrderItems(List<OrderItemEntity> orderItems) {
        this.orderItems = orderItems;
    }

    private void showSuccessMessage() {
        message.setText(SUCCESS_MESSAGE);
        message.setTextFill(Color.web(COLOR_GREEN));
        message.setVisible(true);
    }

    private void showErrorMessage(String errorMessage) {
        message.setText(errorMessage);
        message.setTextFill(Color.web(COLOR_RED));
        message.setVisible(true);
    }

    public Double getOutlay(Double priceToPay, Double receivedCash) {
        return receivedCash - priceToPay;
    }

    private void payOrders() {
        HibernateQueries.payOrders(orders);
        HibernateQueries.updateCurrentStateOfCashRegister(priceToPay);
    }
}