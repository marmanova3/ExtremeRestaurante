package controller;

import app.Main;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.ResourceBundle;


public class MainWindowController implements Initializable {

    public static String clickedTable;

    @FXML
    private Rectangle table1, table2, table3, table4, table5, table6;

    @FXML
    private Circle table7, table8, table9;

    @FXML
    private Label dateTime;

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

    public void initialize(URL location, ResourceBundle resources) {
        setDateTime();
        //TO DO table colours
    }
}