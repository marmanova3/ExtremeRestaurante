package controller;

import app.Main;
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

import java.net.URL;
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
        //TO DO
        dateTime.setText("");
    }

    public void initialize(URL location, ResourceBundle resources) {
        setDateTime();
        //TO DO table colours
    }
}