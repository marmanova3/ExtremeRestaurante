package controller;

import app.Scenes;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.CashRegisterEntity;
import utils.HibernateQueries;

import java.net.URL;
import java.util.ResourceBundle;

public class CashRegisterController extends AbstractController implements Initializable {

    // REFAKT - tento pattern je aj v PopUpControlleri, treba jeden vyhodit
    private static final String DOUBLE_PATTERN = "\\d{0,7}([\\.]\\d{0,2})?";

    private CashRegisterEntity initialState;
    private CashRegisterEntity currentState;

    @FXML
    private TextField initStateInput;
    @FXML
    private Label initialStateLabel, currentStateLabel, profitLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setNewInitStateInputListener();
        setInitialStateValue();
        setCurrentStateValue();
        setProfilValue();
    }

    @FXML
    private void handleBackAction() {
        redirect(Scenes.MAIN_WINDOW);
    }

    @FXML
    private void setNewInitialState() {
        if (newStateIsValid(initStateInput.getText())) {
            Double newState = Double.parseDouble(initStateInput.getText());
            HibernateQueries.updateInitialStateOfCashRegister(newState);
            setInitialStateValue();
            setCurrentStateValue();
            setProfilValue();
        }
    }

    private void setNewInitStateInputListener() {
        initStateInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!hasDoubleFormat(newValue)) {
                    initStateInput.setText(oldValue);
                }
            }
        });
    }

    // REFAKT - tato metoda je rovnaka ako v PopUpController - pri refaktore treba len jednu nechat!
    public boolean hasDoubleFormat(String value) {
        return value.matches(DOUBLE_PATTERN);
    }

    // REFAKT - to iste, aj takato podobna uz je
    public boolean newStateIsValid(String cash) {
        try {
            Double.parseDouble(cash);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private void setInitialStateValue() {
        initialState = HibernateQueries.getCashRegisterState(true);
        initialStateLabel.setText(initialState.getCashStatus().toString());
    }

    private void setCurrentStateValue() {
        currentState = HibernateQueries.getCashRegisterState(false);
        currentStateLabel.setText(currentState.getCashStatus().toString());
    }

    private void setProfilValue() {
        profitLabel.setText(String.format("%.2f", getProfitValue()));
    }

    private Double getProfitValue() {
        return currentState.getCashStatus() - initialState.getCashStatus();
    }
}
