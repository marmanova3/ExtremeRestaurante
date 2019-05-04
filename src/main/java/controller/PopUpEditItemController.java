package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.ItemsEntity;
import utils.HibernateQueries;
import utils.NumberUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class PopUpEditItemController extends AbstractController {

    private static final String EMPTY_NAME = "Food name cannot be empty.";
    private static final String PRICE_INVALID = "Price is invalid";
    @FXML
    public TextField foodNameEditInput;
    @FXML
    public TextField priceEditItemInput;
    @FXML
    public Label editItemErrorMessage;

    private Stage stage;
    private int itemId;

    @Override
    public void initialize(URL location, ResourceBundle resources) { }

    @FXML
    public void saveEditedItem() {
        if ("".equals(foodNameEditInput.getText())) {
            setErrorMessage(EMPTY_NAME);
            return;
        }
        if (!NumberUtils.isValidDoubleFormat(priceEditItemInput.getText())) {
            setErrorMessage(PRICE_INVALID);
            return;
        }

        String foodName = foodNameEditInput.getText();
        Double price = Double.parseDouble(priceEditItemInput.getText());

        HibernateQueries.updateEntityItem(itemId, foodName, price);
        closeStage();

    }

    @FXML
    public void deleteItem() {
        HibernateQueries.softDeleteItemById(itemId);
        closeStage();
    }

    @FXML
    public void closeWindow() {
        closeStage();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setId(int id) { this.itemId = id; }

    public void setNameText(String name) { foodNameEditInput.setText(name); }

    public  void setPrice(Double price) { priceEditItemInput.setText(price.toString()); }

    private void closeStage() {
        if (this.stage != null) {
            this.stage.close();
        }
    }

    private void setErrorMessage(String message) {
        editItemErrorMessage.setText(message);
        editItemErrorMessage.setVisible(true);
    }

}
