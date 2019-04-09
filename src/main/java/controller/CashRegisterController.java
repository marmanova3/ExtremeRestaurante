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
import utils.NumberUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class CashRegisterController extends AbstractController implements Initializable {

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
        if (NumberUtils.isValidDoubleFormat(initStateInput.getText())) {
            Double newState = Double.parseDouble(initStateInput.getText());
            Double newStateRounded = NumberUtils.getRoundedDecimalNumber(newState, 2);
            HibernateQueries.updateInitialStateOfCashRegister(newStateRounded);
            setInitialStateValue();
            setCurrentStateValue();
            setProfilValue();
        }
    }

    private void setNewInitStateInputListener() {
        initStateInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!NumberUtils.hasDoubleFormat(newValue)) {
                    initStateInput.setText(oldValue);
                }
            }
        });
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
        profitLabel.setText(NumberUtils.getRoundedDecimalNumber(getProfitValue(), 2).toString());
    }

    private Double getProfitValue() {
        return currentState.getCashStatus() - initialState.getCashStatus();
    }
}
