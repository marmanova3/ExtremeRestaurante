package controller;

import app.Scenes;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ItemsEntity;
import model.TablesEntity;
import utils.HibernateQueries;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChooseItemsController extends AbstractController implements Initializable {

    private static final int CATEGORY_ID_SOUPS = 0;
    private static final int CATEGORY_ID_MAIN_COURSES = 2;
    private static final int CATEGORY_ID_DESSERTS = 3;
    private static final int CATEGORY_ID_SIDE_DISHES = 4;
    private static final int CATEGORY_ID_PASTA = 5;
    private static final int CATEGORY_ID_PIZZA = 6;
    private static final int CATEGORY_ID_DRINKS = 7;

    private static final String COLOR_DARK = "#333333";
    private static final String COLOR_GRAY = "#565656";
    private static final String COLOR_LIGHT_GRAY = "#7c7c7c";
    private static final String COLOR_WHITE = "#ffffff";

    private static final int ITEM_BUTTON_WIDTH = 180;
    private static final int ITEM_BUTTON_HEIGHT = 100;

    private int tableId;
    private TablesEntity table;
    private int categoryId;
    private Button categoryButtonClicked;
    private List<ItemsEntity> menuItems = new ArrayList<>();
    private ArrayList<Button> categoryButtons;

    @FXML
    private Label tableLabel;
    @FXML
    private FlowPane flowPane;
    @FXML
    private Button drinks, mainCourse, soups, desserts, pasta, pizza, sideDishes;

    public void initialize(URL location, ResourceBundle resources) {
        setDefaultCategory();
        setTableInfo();
        initializeCategoryButtons();
        setItemsByClickedCategory();
    }

    @FXML
    private void handleBackAction() {
        redirect(Scenes.TABLE_WINDOW);
    }

    @FXML
    private void handleMenuItemsDrinks() {
        categoryId = CATEGORY_ID_DRINKS;
        categoryButtonClicked = drinks;
        setItemsByClickedCategory();
    }

    @FXML
    private void handleMenuItemsMainCourse() {
        categoryId = CATEGORY_ID_MAIN_COURSES;
        categoryButtonClicked = mainCourse;
        setItemsByClickedCategory();
    }

    @FXML
    private void handleMenuItemsSoups() {
        categoryId = CATEGORY_ID_SOUPS;
        categoryButtonClicked = soups;
        setItemsByClickedCategory();
    }

    @FXML
    private void handleMenuItemsDesserts() {
        categoryId = CATEGORY_ID_DESSERTS;
        categoryButtonClicked = desserts;
        setItemsByClickedCategory();
    }

    @FXML
    private void handleMenuItemsPasta() {
        categoryId = CATEGORY_ID_PASTA;
        categoryButtonClicked = pasta;
        setItemsByClickedCategory();
    }

    @FXML
    private void handleMenuItemsPizza() {
        categoryId = CATEGORY_ID_PIZZA;
        categoryButtonClicked = pizza;
        setItemsByClickedCategory();
    }

    @FXML
    private void handleMenuItemsSideDish() {
        categoryId = CATEGORY_ID_SIDE_DISHES;
        categoryButtonClicked = sideDishes;
        setItemsByClickedCategory();
    }

    @FXML
    private void addItemToOrders(MouseEvent event) {
        String clickedItemIdAsString = ((Button) event.getSource()).getId();
        Integer itemId = Integer.parseInt(clickedItemIdAsString);
        HibernateQueries.addItemToTableOrders(itemId, table);
    }

    @FXML
    private void editItem(MouseEvent event) {
        String clickedItemIdAsString = ((Button) event.getSource()).getId();
        System.out.println(clickedItemIdAsString);
    }

    @FXML
    private void addNewItem(MouseEvent event) {
        showPopupNewItemWindow();
    }

    private void setDefaultCategory() {
        categoryId = CATEGORY_ID_DRINKS;
        categoryButtonClicked = drinks;
    }

    private void setTableInfo() {
        tableId = TableWindowController.getTableId();
        tableLabel.setText("TABLE " + (tableId + 1));
        table = HibernateQueries.getTableById(tableId);
    }

    private void initializeCategoryButtons() {
        categoryButtons = new ArrayList<>();

        categoryButtons.add(drinks);
        categoryButtons.add(mainCourse);
        categoryButtons.add(soups);
        categoryButtons.add(desserts);
        categoryButtons.add(pasta);
        categoryButtons.add(pizza);
        categoryButtons.add(sideDishes);
    }

    private void setButtonBackgroundLight(Button button) {
        Background background = new Background(
                new BackgroundFill(Color.web(COLOR_GRAY),
                        CornerRadii.EMPTY,
                        Insets.EMPTY));
        button.setBackground(background);
    }

    private void setButtonBackgroundDark(Button button) {
        Background background = new Background(
                new BackgroundFill(Color.web(COLOR_DARK),
                        CornerRadii.EMPTY,
                        Insets.EMPTY));
        button.setBackground(background);
    }

    private void toggleCategoryActiveState() {
        for (Button categoryButton : categoryButtons) {
            categoryButton.setStyle("-fx-background-color: " + getColorOfCategoryButton(categoryButton));
        }
    }

    private String getColorOfCategoryButton(Button categoryButton) {
        return categoryButton == categoryButtonClicked ? COLOR_GRAY : COLOR_DARK;
    }

    private void setItemsByClickedCategory() {
        menuItems = HibernateQueries.getItemsByCategoryId(categoryId);
        toggleCategoryActiveState();
        setItemsButtons();
    }

    private void setItemsButtons() {
        flowPane.getChildren().clear();


        for (ItemsEntity item : menuItems) {
            // Edit
            ImageView editIcon = new ImageView("windows/assets/edit.png");
            editIcon.setFitHeight(20);
            editIcon.setFitWidth(20);
            Button editButton = new Button("", editIcon);
            editButton.setCursor(Cursor.HAND);
            editButton.setPrefSize(ITEM_BUTTON_WIDTH, ITEM_BUTTON_HEIGHT / 5);
            editButton.setContentDisplay(ContentDisplay.RIGHT);
            setButtonBackgroundDark(editButton);
            editButton.setId(String.valueOf(item.getId()));
            editButton.setOnMouseClicked(event -> editItem(event));
            // ADD
            Button button = new Button(
                    item.getName() + '\n' + item.getPrice() + '€',
                    editButton
            );
            button.setContentDisplay(ContentDisplay.TOP);

            button.wrapTextProperty().setValue(true);
            button.setTextAlignment(TextAlignment.CENTER);
            button.setPrefSize(ITEM_BUTTON_WIDTH, ITEM_BUTTON_HEIGHT);
            button.setBackground(
                    new Background(
                            new BackgroundFill(Color.web(COLOR_DARK),
                                    CornerRadii.EMPTY,
                                    Insets.EMPTY)));
            button.setTextFill(Paint.valueOf(COLOR_WHITE));
            button.setFont(Font.font(18));
            button.setId(String.valueOf(item.getId()));
            button.setCursor(Cursor.HAND);
            button.setOnMouseClicked(event ->
                    addItemToOrders(event));
            button.setOnMouseEntered(event ->
            {
                setButtonBackgroundLight(button);
                setButtonBackgroundLight(editButton);
            });
            button.setOnMouseExited(event ->
            {
                setButtonBackgroundDark(button);
                setButtonBackgroundDark(editButton);
            });
            button.setOnMousePressed(event ->
                    button.setBackground(
                            new Background(
                                    new BackgroundFill(Color.web(COLOR_LIGHT_GRAY),
                                            CornerRadii.EMPTY,
                                            Insets.EMPTY))));
            flowPane.setMargin(button, new Insets(0, 0, 10, 10));
            flowPane.getChildren().addAll(button);
        }

    }

    private void showPopupNewItemWindow() {
        FXMLLoader loader = getSceneLoader(Scenes.POP_UP_NEW_ITEM_WINDOW);
        Parent root = getParent(loader);
        PopUpNewItemController popUpNewItemController = loader.getController();
        Scene scene = new Scene(root);
        Stage popupStage = createPopUpStage();
        popUpNewItemController.setStage(popupStage);
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

}
