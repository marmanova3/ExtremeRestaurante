package controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.OrderItemEntity;
import model.OrdersEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import utils.HibernateUtil;
import utils.ReceiptPrinter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PopUpController extends AbstractController {

    @FXML
    private Button confirmBtn, cancelBtn, printBtn;
    @FXML private TextField cashInput;
    @FXML private Label outlayOutput, message, tableNumber;
    private Stage stage;
    private double priceToPay;
    private List<OrdersEntity> orders;
    private List<OrderItemEntity> orderItems;
    private int tableId;

    public void initialize(URL url, ResourceBundle rb) {

        message.setVisible(false);

        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                closeStage();
            }
        });

        cashInput.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    cashInput.setText(oldValue);
                }
            }
        });
    }

    /**
     * sets the stage of this view
     * @param stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Closes the stage of this view
     */
    private void closeStage() {
        if(this.stage!=null) {
            this.stage.close();
        }
    }

    public void setTableId(int tableId){
        this.tableId = tableId;
        tableNumber.setText("TABLE " + (tableId + 1));
    }


    public void setPriceToPay(double priceToPay){
        this.priceToPay = priceToPay;
    }

    /**
     * Sets list of orders (so it can change soft_delete attribute and commit to db)
     */
    public void setOrders(List<OrdersEntity> orders){
        this.orders = orders;
    }

    public void setOrderItems(List<OrderItemEntity> orderItems) {
        this.orderItems = orderItems;
    }

    @FXML
    private void confirm() throws Exception {
        if (receivedCasIsValid()){

            if (payAll()){
                showSuccessMessage();
                outlayOutput.setText(String.format("%.2f", getOutlay()));
                confirmBtn.setDisable(true);
                printBtn.setDisable(false);
                cashInput.setDisable(true);
            } else {
                showErrorMessage();
            }
        } else {
            showErrorMessage();
        }
    }

    @FXML
    private void printReceipt() throws Exception {
        String total = Double.toString(priceToPay);
        String cash = Double.toString(Double.parseDouble(cashInput.getText()));
        String outlay = String.format("%.2f", getOutlay());
        ReceiptPrinter.print(total, cash, outlay, orderItems);
    }

    private void showSuccessMessage(){
        message.setText("Payment successful");
        message.setTextFill(Color.web("#2ec60b"));
        message.setVisible(true);
    }
    private void showErrorMessage(){
        message.setText("Payment denied");
        message.setTextFill(Color.web("#c90b0b"));
        message.setVisible(true);
    }

    private boolean receivedCasIsValid(){
        try {
            double receivedCashFromCustomer = Double.parseDouble(cashInput.getText());
            Double priceToPayDouble = new Double(priceToPay);
            Double receivedCashFromCustomerDouble = new Double(receivedCashFromCustomer);
            return priceToPayDouble.compareTo(receivedCashFromCustomerDouble)<= 0;
        } catch (NumberFormatException nfe) {
            return false; //ak sa neda pretypovat, tak vrati false;
        }
    }

    private Double getOutlay(){
        Double priceToPayDouble = new Double(priceToPay);
        Double receivedCashFromCustomerDouble = new Double(Double.parseDouble(cashInput.getText()));
        return receivedCashFromCustomerDouble - priceToPayDouble;
    }

    private boolean payAll(){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        for (OrdersEntity order : orders) {
            order.setPaid(true);
            session.update(order);
            session.save(order);
        }

        // returns true if payment was successful
        boolean successful = false;
        try {
            session.flush();
            session.getTransaction().commit();
            successful = true;
        } catch (HibernateException r) {
            successful = false;
        } finally {
            session.close();
            session = null;
        }
        return successful;

        //TO DO update cash_register + priceTotal ... toto este nie je!!!
    }
}