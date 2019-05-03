package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //nacitaj do inputov texty a idcko itemy
    }

    @FXML
    public void saveEditedItem(MouseEvent mouseEvent) {
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

        //updateItem
        closeStage();

    }

    @FXML
    public void deleteItem(MouseEvent mouseEvent) {
        //tu treba id
        //HibernateQueries.softDeleteItemById();
        closeStage();
    }

    @FXML
    public void closeWindow(MouseEvent mouseEvent) {
        closeStage();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

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
