package controller;

import app.Scenes;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.net.URL;
import java.util.ResourceBundle;

public class ChooseItemsController extends AbstractController implements Initializable {

    private int tableId;

    @FXML
    private Label tableLabel;

    @FXML
    private void handleBackAction() {
        redirect(Scenes.TABLE_WINDOW);
    }

    public void initialize(URL location, ResourceBundle resources) {
        setTableId();
    }

    public void setTableId() {
        this.tableId = TableWindowController.tableId;
        tableLabel.setText("TABLE " + (this.tableId + 1));
    }
}
