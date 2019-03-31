package controller;

import app.Scenes;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import model.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import utils.HibernateUtil;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.List;

public class ChooseItemsController extends AbstractController implements Initializable {

    private int tableId;
    private TablesEntity table;
    private int categoryId = 7;
    private Button clickedCategory;
    private List<CategoriesEntity> categories = new ArrayList<>();
    private List<ItemsEntity> menuItems = new ArrayList<>();
    private  ArrayList<Button> categoryButtons = new ArrayList<>();

    @FXML
    private Label tableLabel;

    @FXML
    private FlowPane flowPane;

    @FXML
    private Button drinks;

    @FXML
    private Button mainCourse;

    @FXML
    private Button soups;

    @FXML
    private Button desserts;

    @FXML
    private Button pasta;

    @FXML
    private Button pizza;

    @FXML
    private Button sideDishes;

    @FXML
    private void handleBackAction() {
        redirect(Scenes.TABLE_WINDOW);
    }

    @FXML
    private void handleMenuItemsDrinks() {
        categoryId = 7;
        clickedCategory = drinks;
        getCategoryMenu();
    }

    @FXML
    private void handleMenuItemsMainCourse() {
        categoryId = 2;
        clickedCategory = mainCourse;
        getCategoryMenu();
    }

    @FXML
    private void handleMenuItemsSoups() {
        categoryId = 0;
        clickedCategory = soups;
        getCategoryMenu();
    }

    @FXML
    private void handleMenuItemsDesserts() {
        categoryId = 3;
        clickedCategory = desserts;
        getCategoryMenu();
    }

    @FXML
    private void handleMenuItemsPasta() {
        categoryId = 5;
        clickedCategory = pasta;
        getCategoryMenu();
    }

    @FXML
    private void handleMenuItemsPizza() {
        categoryId = 6;
        clickedCategory = pizza;
        getCategoryMenu();
    }

    @FXML
    private void handleMenuItemsSideDish() {
        categoryId = 4;
        clickedCategory = sideDishes;
        getCategoryMenu();
    }

    public void initialize(URL location, ResourceBundle resources) {
        drinks.setStyle("-fx-background-color: #565656;");
        setTable();
        getCategories();
        getCategoryMenu();
        initializeCategoryButtons();
    }

    private void setTable() {
        tableId = TableWindowController.tableId;
        tableLabel.setText("TABLE " + (this.tableId + 1));

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from TablesEntity where id =: tableId");
        query.setParameter("tableId", tableId);
        List<?> list = query.list();

        table = (TablesEntity)list.get(0);

        session.getTransaction().commit();
        session.close();
    }

    private  void initializeCategoryButtons() {
        categoryButtons.add(drinks);
        categoryButtons.add(mainCourse);
        categoryButtons.add(soups);
        categoryButtons.add(desserts);
        categoryButtons.add(pasta);
        categoryButtons.add(pizza);
        categoryButtons.add(sideDishes);
    }

    private void toggleCategoryAcitveState() {
        for (Button button : categoryButtons) {
            if (clickedCategory == button) {
                button.setStyle("-fx-background-color: #565656;");
            } else {
                button.setStyle("-fx-background-color: #333333;");
            }
        }
    }

    private void getCategories() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("select id, name from CategoriesEntity");

        List queryList = query.list();

        for (Object o : queryList) {
            Object[] object = (Object[]) o;
            CategoriesEntity category = new CategoriesEntity();
            category.setId((int) object[0]);
            category.setName(object[1].toString());
            categories.add(category);
        }

        session.getTransaction().commit();
        session.close();

    }

    private void getCategoryMenu() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query = session.createQuery("select id, name, price, category from ItemsEntity where category =: categoryId");
        query.setParameter("categoryId", categoryId);

        List queryList = query.list();
        Iterator iterator = queryList.iterator();

        menuItems.removeAll(menuItems);

        while (iterator.hasNext()) {
            Object[] object = (Object[]) iterator.next();
            ItemsEntity menuItem = new ItemsEntity();
            menuItem.setId((int) object[0]);
            menuItem.setName(object[1].toString());

            menuItem.setPrice((double) object[2]);
            CategoriesEntity category = (CategoriesEntity) object[3];
            menuItem.setCategory(category);
            menuItems.add(menuItem);
        }

        session.getTransaction().commit();
        session.close();

        toggleCategoryAcitveState();
        setMenuItems();
    }

    private void setMenuItems() {
        flowPane.getChildren().clear();
        for (ItemsEntity item : menuItems) {
            Button btn = new Button(item.getName());
            btn.setPrefSize(180, 100);
            btn.setBackground(new Background(new BackgroundFill(Color.web("#333333"), CornerRadii.EMPTY, Insets.EMPTY)));
            btn.setTextFill(Paint.valueOf("#fff"));
            btn.setFont(Font.font(18));
            btn.setOnAction(event -> addMenuItemToOrder(item));
            btn.setCursor(Cursor.HAND);
            flowPane.setMargin(btn, new Insets(0, 0, 10, 10));
            flowPane.getChildren().addAll(btn);
        }

    }

// TODO: nejaka metoda uz nato zevraj je spystaj sa dievcat toto pada
    private void addMenuItemToOrder(ItemsEntity item) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        OrdersEntity order = new OrdersEntity();
        order.setItem(item);
        order.setTable(table);
        order.setPrice(item.getPrice());
        order.setPaid(false);
        order.setQuantity(1);

        session.save(order);
        session.getTransaction().commit();
        session.close();
    }
}
