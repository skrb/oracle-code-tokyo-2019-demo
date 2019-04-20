package net.javainthebox.imageeffector;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ImageEffector extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        AnchorPane root = FXMLLoader.load(this.getClass().getResource("ImageViewer.fxml"));
        var scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String... args) {
        launch(args);
    }
}
