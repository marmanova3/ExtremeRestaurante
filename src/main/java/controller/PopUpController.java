package controller;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.OrdersEntity;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import utils.HibernateUtil;


public class PopUpController  extends AbstractController implements Initializable {

    @FXML private Button confirmBtn, cancelBtn;
    @FXML private TextField cashInput;
    @FXML private Label outlayOutput, message;
    private Stage stage;
    private double priceToPay;
    private List<OrdersEntity> orders;
    private HashMap<String, Object> result = new HashMap<String, Object>();

    public void initialize(URL url, ResourceBundle rb) {

        message.setVisible(false);

        cancelBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                closeStage();
            }
        });

        // force the field to be numeric only
        // TODO: upravit aby bralo aj desatinne, teraz berie iba cele :(
        cashInput.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    cashInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    /**
     * setting the stage of this view
     * @param stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        System.out.println("nastavujem stage");
        System.out.println("stage is " + this.stage);
    }

    /**
     * Closes the stage of this view
     */
    private void closeStage() {
        if(this.stage!=null) {
            this.stage.close();
        }
    }

    /**
     * Sets the price
     */
    public void setPriceToPay(double priceToPay){
        this.priceToPay = priceToPay;
    }

    /**
     * Sets list of orders (so it can change soft_delete attribute and commit to db)
     */
    public void setOrders(List<OrdersEntity> orders){
        this.orders = orders;
    }

    @FXML
    private void confirm() throws Exception {
        if (receivedCasIsValid()){

            if (payAll()){
                showSuccessMessage();
                outlayOutput.setText(String.format("%.2f", getOutlay()));
                confirmBtn.setDisable(true);

            } else {
                showErrorMessage();
            }
        } else {
            showErrorMessage();
        }
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

        // returns successfull if payment was successful
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
    }

}