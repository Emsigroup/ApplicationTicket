package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        // exemple simple : ajouter un label pour voir quelque chose
        Label lbl = new Label("Hello JavaFX");
        lbl.setLayoutX(50);
        lbl.setLayoutY(50);
        root.getChildren().add(lbl);

        // créer la scène (largeur 800, hauteur 600) avec fond noir
        Scene scene = new Scene(root, 800, 600);

        // utiliser le primaryStage fourni et l'afficher
        primaryStage.setTitle("Mon Application JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
