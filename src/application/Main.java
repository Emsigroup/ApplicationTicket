package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class Main extends Application {
	@Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        

        Scene scene = new Scene(root, 600, 400); // largeur 600, hauteur 400
       String css =this.getClass().getResource("application.css").toExternalForm();
       scene.getStylesheets().add(css);
        stage.setTitle("Mon Application JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
