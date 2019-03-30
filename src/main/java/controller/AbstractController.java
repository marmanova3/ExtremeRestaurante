package controller;

import app.Main;
import app.Scenes;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class AbstractController implements Initializable {

    protected Main main;

    public void setMainApp(Main main) {
        this.main = main;
    }

    public FXMLLoader getSceneLoader(Scenes scene) {
        return new FXMLLoader(this.getClass().getResource(scene.toString()));
    }

    public void redirect(Scenes scene) {
        FXMLLoader loader = getSceneLoader(scene);
        Parent root = null;
        try {
            root = (Parent) loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage stage = Main.mainStage;
        stage.setScene(new Scene(root));
        stage.show();
    }
}