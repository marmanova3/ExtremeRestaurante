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
    private static final String DOUBLE_PATTERN = "\\d{0,7}([\\.]\\d{0,2})?";


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
                if (!hasDoubleFormat(newValue)) {
                    cashInput.setText(oldValue);
                }
            }
        });
    }

    private void setDiscountInputListener() {
        discountInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!hasDoubleFormat(newValue)) {
                    discountInput.setText(oldValue);
                }
                if (!"".equals(newValue)) {
                    controller.setPriceTotal((String.valueOf(discountPriceToPay())));
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
        if (!discoutIsValid()) {
            showErrorMessage(DISCOUNT_ERROR_MESSAGE);
            return;
        }
        Double discountedPrice = discountPriceToPay();
        if (receivedCashIsValid(cashInput.getText(), discountedPrice)) {
            setPriceToPay(discountedPrice);
            payOrders();
            showSuccessMessage();
            outlayOutput.setText(getOutlay(priceToPay, getCashInput()));
            confirmBtn.setDisable(true);
            printBtn.setDisable(false);
            cashInput.setDisable(true);
            discountInput.setDisable(true);

        } else {
            showErrorMessage(ERROR_MESSAGE);
        }
    }

    private boolean discoutIsValid() {
        Double percentage = getDiscountInput();
        return percentage <= 100;
    }

    private Double discountPriceToPay() {
        Double percentage = getDiscountInput();
        Double newPrice = priceToPay;
        if (percentage != 0) {
            newPrice -= priceToPay * (percentage / 100);
        }
        return newPrice;
    }

    //TODO add discount to items in receipt
    @FXML
    private void printReceipt() {
        String total = Double.toString(priceToPay);
        String cash = Double.toString(getCashInput());
        String outlay = getOutlay(priceToPay, getCashInput());
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

    public boolean hasDoubleFormat(String value) {
        return value.matches(DOUBLE_PATTERN);
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

    public boolean receivedCashIsValid(String cash, Double priceToPay) {
        try {
            Double receivedCashFromCustomer = Double.parseDouble(cash);
            return isGreaterOrEqual(receivedCashFromCustomer, priceToPay);
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isGreaterOrEqual(Double comparingValue, Double desiredValue) {
        return desiredValue.compareTo(comparingValue) <= 0;
    }

    public String getOutlay(Double priceToPay, Double receivedCash) {
        return String.format("%.2f", receivedCash - priceToPay);
    }

    private void payOrders() {
        HibernateQueries.payOrders(orders);
        // TODO update cash_register + priceTotal ... toto este nie je!!!
    }
}