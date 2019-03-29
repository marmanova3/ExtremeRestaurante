package controller;

import app.Main;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.OrdersEntity;
import model.TablesEntity;
import org.hibernate.Session;
import org.hibernate.query.Query;
import sun.security.x509.FreshestCRLExtension;
import utils.HibernateQueries;
import utils.HibernateUtil;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class MainWindowController implements Initializable {

    @FXML
    private Label dateTime;

    @FXML
    private Pane groupsPane;

    final List<String> occupiedTables = HibernateQueries.findOccupiedTables();
    public static String clickedTable;

    private String FREE_TABLE_COLOR = "#288e28";
    private String OCCUPIED_TABLE_COLOR = "#e1901e";


    @FXML
    private void handleTableClickAction(MouseEvent event) throws Exception {
        Group table = (Group) event.getSource();

        clickedTable = table.getId();

        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/windows/tableWindow.fxml"));
        Parent root = (Parent) loader.load();

        Stage stage = Main.mainStage;
        stage.setScene(new Scene(root));
        stage.show();

        System.out.println(table.getId() + " was clicked.");
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

    private boolean tableIsOccupied(String tableNumber){
        return occupiedTables.contains(tableNumber);
    }

    private String parseGroupName(String groupId){
        return groupId.substring(3);
    }

    private void setRectangleTableColor(Rectangle rectangle, String tableName){
        if(tableIsOccupied(parseGroupName(tableName))){
            rectangle.setFill(Color.web(OCCUPIED_TABLE_COLOR));
        }
        else{
            rectangle.setFill(Color.web(FREE_TABLE_COLOR));
        }
    }

    private void setCircleTableColor(Circle circle, String tableName) {
        if (tableIsOccupied(parseGroupName(tableName))) {
            circle.setFill(Color.web(OCCUPIED_TABLE_COLOR));
        } else {
            circle.setFill(Color.web(FREE_TABLE_COLOR));
        }
    }

    private void setTableColor(Group tableGroup){
        Node tableShape = tableGroup.getChildren().get(0);
        if (tableShape != null) {
            if (tableShape instanceof Rectangle) {
                setRectangleTableColor((Rectangle) tableShape, parseGroupName(tableGroup.getId()));
            } else if (tableShape instanceof Circle) {
                setCircleTableColor((Circle) tableShape, parseGroupName(tableGroup.getId()));
            }
        }
    }

    private void initTablesColor(){
        for (Node tableGroup: groupsPane.getChildren()){
            if (tableGroup instanceof Group){
                setTableColor((Group)tableGroup);
            }
        }
    }

    public void initialize(URL location, ResourceBundle resources)  {
        setDateTime();
        initTablesColor();
    }
}