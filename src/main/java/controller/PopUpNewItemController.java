package controller;

import app.Scenes;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.CategoriesEntity;
import utils.HibernateQueries;
import utils.NumberUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class PopUpNewItemController extends AbstractController {

    private static final String EMPTY_NAME = "Food name cannot be empty.";
    private static final String PRICE_INVALID = "Price is invalid";
    @FXML
    public Label newItemErrorMessage;
    @FXML
    private ChoiceBox categorySelect;
    @FXML
    private TextField priceNewItemInput, foodNameInput;
    private Stage stage;

    public void initialize(URL url, ResourceBundle rb) {
        setCategories();
    }

    @FXML
    public void saveNewItem(MouseEvent mouseEvent) {
        if (!NumberUtils.isValidDoubleFormat(priceNewItemInput.getText())) {
            setErrorMessage(PRICE_INVALID);
            return;
        }
        if ("".equals(foodNameInput.getText())) {
            setErrorMessage(EMPTY_NAME);
            return;
        }

        String foodName = foodNameInput.getText();
        Double price = Double.parseDouble(priceNewItemInput.getText());
        CategoriesEntity selectedCategory = (CategoriesEntity) categorySelect.getSelectionModel().getSelectedItem();
        HibernateQueries.insertNewitem(foodName, price, selectedCategory);
        reload();
        closeStage();
    }


    @FXML
    public void cancelNewItem(MouseEvent mouseEvent) {
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

    private void setCategories() {
        categorySelect.setItems(HibernateQueries.getCategories());
        categorySelect.getSelectionModel().selectFirst();
    }

    private void setErrorMessage(String message) {
        newItemErrorMessage.setText(message);
        newItemErrorMessage.setVisible(true);
    }

    private void reload() {
        redirect(Scenes.CHOOSE_ITEMS_WINDOW);
    }

}
