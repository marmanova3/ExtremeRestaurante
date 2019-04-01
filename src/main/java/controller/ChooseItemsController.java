package controller;

import app.Scenes;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import model.CategoriesEntity;
import model.ItemsEntity;
import model.OrdersEntity;
import model.TablesEntity;
import org.hibernate.Session;
import org.hibernate.query.Query;
import utils.HibernateQueries;
import utils.HibernateUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

public class ChooseItemsController extends AbstractController implements Initializable {

    private static final int drinksCategoryId = 7;
    private static final int mainCoursesCategoryId = 2;
    private static final int soupsCategoryId = 0;
    private static final int dessertsCategoryId = 3;
    private static final int pastaCategoryId = 5;
    private static final int pizzaCategoryId = 6;
    private static final int sideDishCategoryId = 4;

    private int tableId;
    private TablesEntity table;
    private int categoryId = 7;
    private Button clickedCategory;
    private List<ItemsEntity> menuItems = new ArrayList<>();
    private ArrayList<Button> categoryButtons = new ArrayList<>();

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
        categoryId = drinksCategoryId;
        clickedCategory = drinks;
        getCategoryMenu();
    }

    @FXML
    private void handleMenuItemsMainCourse() {
        categoryId = mainCoursesCategoryId;
        clickedCategory = mainCourse;
        getCategoryMenu();
    }

    @FXML
    private void handleMenuItemsSoups() {
        categoryId = soupsCategoryId;
        clickedCategory = soups;
        getCategoryMenu();
    }

    @FXML
    private void handleMenuItemsDesserts() {
        categoryId = dessertsCategoryId;
        clickedCategory = desserts;
        getCategoryMenu();
    }

    @FXML
    private void handleMenuItemsPasta() {
        categoryId = pastaCategoryId;
        clickedCategory = pasta;
        getCategoryMenu();
    }

    @FXML
    private void handleMenuItemsPizza() {
        categoryId = pizzaCategoryId;
        clickedCategory = pizza;
        getCategoryMenu();
    }

    @FXML
    private void handleMenuItemsSideDish() {
        categoryId = sideDishCategoryId;
        clickedCategory = sideDishes;
        getCategoryMenu();
    }

    @FXML
    private void addItemToOrders(MouseEvent event) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Button clickedItem = (Button) event.getSource();
        String clickedItemId = clickedItem.getId();

        ItemsEntity item = session.load(ItemsEntity.class, Integer.parseInt(clickedItemId));

        boolean newOrder = true;
        for (OrdersEntity order : HibernateQueries.getOrders(table)) {
            if (order.getItem().getId() == Integer.parseInt(clickedItemId)) {
                order.setQuantity(order.getQuantity() + 1);
                newOrder = false;
                session.update(order);
            }
        }
        if (newOrder) {
            OrdersEntity order = new OrdersEntity();
            order.setPaid(false);
            order.setItem(item);
            order.setPrice(item.getPrice());
            order.setTable(table);
            order.setQuantity(1);
            session.save(order);
        }
        session.getTransaction().commit();
        session.close();
    }

    public void initialize(URL location, ResourceBundle resources) {
        drinks.setStyle("-fx-background-color: #565656;");
        setTable();
        getCategoryMenu();
        initializeCategoryButtons();
    }

    private void setTable() {
        tableId = TableWindowController.tableId;
        tableLabel.setText("TABLE " + (this.tableId + 1));

        Session session = HibernateUtil.getSessionFactory().openSession();

        System.out.println("table: " + tableId);
        table = session.load(TablesEntity.class, tableId);

        session.close();
    }

    private void initializeCategoryButtons() {
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
            Button button = new Button(item.getName());
            button.setPrefSize(180, 100);
            button.setBackground(new Background(new BackgroundFill(Color.web("#333333"), CornerRadii.EMPTY, Insets.EMPTY)));
            button.setTextFill(Paint.valueOf("#fff"));
            button.setFont(Font.font(18));
            button.setId(String.valueOf(item.getId()));
            button.setCursor(Cursor.HAND);
            button.setOnMouseClicked(event -> addItemToOrders(event));
            flowPane.setMargin(button, new Insets(0, 0, 10, 10));
            flowPane.getChildren().addAll(button);
        }

    }
}
