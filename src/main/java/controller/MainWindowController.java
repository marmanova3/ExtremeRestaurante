package controller;

import app.Scenes;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import utils.HibernateQueries;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;


public class MainWindowController extends AbstractController {

    @FXML
    private Label dateTime;

    @FXML
    private Pane groupsPane;

    private static String clickedTable;

    private String FREE_TABLE_COLOR = "#288e28";
    private String OCCUPIED_TABLE_COLOR = "#e1901e";


    public static String getClickedTable() {
        return clickedTable;
    }

    @FXML
    private void handleTableClickAction(MouseEvent event) {
        Group table = (Group) event.getSource();

        clickedTable = table.getId();

        redirect(Scenes.TABLE_WINDOW);
    }

    private void setDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO, new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent e) {
                        dateTime.setText(LocalDateTime.now().format(formatter));
                    }
                }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    public void initialize(URL location, ResourceBundle resources) {
        setDateTime();
        List<String> occupiedTables = HibernateQueries.getOccupiedTables();
        initTablesColor(occupiedTables);
    }

    public boolean tableIsOccupied(String tableName, List<String> occupiedTables) {
        return occupiedTables.contains(tableName);
    }

    public String parseTableName(String groupId) {
        if (groupId.length() < 4) {
            return "";
        }
        return groupId.substring(3);
    }

    private void setRectangleTableColor(Rectangle rectangle, String tableName, List<String> occupiedTables) {
        if (tableIsOccupied(tableName, occupiedTables)) {
            rectangle.setFill(Color.web(OCCUPIED_TABLE_COLOR));
        } else {
            rectangle.setFill(Color.web(FREE_TABLE_COLOR));
        }
    }

    private void setCircleTableColor(Circle circle, String tableName, List<String> occupiedTables) {
        if (tableIsOccupied(tableName, occupiedTables)) {
            circle.setFill(Color.web(OCCUPIED_TABLE_COLOR));
        } else {
            circle.setFill(Color.web(FREE_TABLE_COLOR));
        }
    }

    private void setTableColor(Group tableGroup, List<String> occupiedTables) {
        Node tableShape = tableGroup.getChildren().get(0);
        if (tableShape != null && tableNameIsValid(tableGroup.getId())) {
            if (tableShape instanceof Rectangle) {
                setRectangleTableColor((Rectangle) tableShape, parseTableName(tableGroup.getId()), occupiedTables);
            } else if (tableShape instanceof Circle) {
                setCircleTableColor((Circle) tableShape, parseTableName(tableGroup.getId()), occupiedTables);
            }
        }
    }

    public boolean tableNameIsValid(String groupId) {
        String tableName = parseTableName(groupId);
        return tableName != "";
    }

    private void initTablesColor(List<String> occupiedTables) {
        for (Node tableGroup : groupsPane.getChildren()) {
            if (tableGroup instanceof Group) {
                setTableColor((Group) tableGroup, occupiedTables);
            }
        }
    }

}