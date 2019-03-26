//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import model.ItemsEntity;

public class Controller {

    @FXML
    private Button myButton;

    @FXML
    private TableView tableview;

    private ObservableList<ItemsEntity> data = FXCollections.observableArrayList();

    public Controller() {
    }

    @FXML
    private void handleButtonAction() {
        // Button was clicked, do something…
        System.out.println("I was clicked");
    }
}