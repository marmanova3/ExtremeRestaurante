//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static Stage mainStage;

    public Main() {
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;

        System.out.println("App started");

        Parent root = (Parent) FXMLLoader.load(this.getClass().getResource(Scenes.MAIN_WINDOW.toString()));
        primaryStage.setTitle("Extreme Restaurante");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return mainStage;
    }

}
