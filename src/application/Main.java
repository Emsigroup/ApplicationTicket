package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.input.MouseEvent;

public class Main extends Application {
	
	private double x=0;
	private double y=0;
	@Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
      
        Scene scene = new Scene(root); 
       root.setOnMousePressed( (MouseEvent event) -> {
    	   x = event.getSceneX(); 
    	   y = event.getSceneY(); 
       });
       root.setOnMouseDragged((MouseEvent event) -> {
    	   
    	   stage.setX(event.getScreenX() - x);
    	   stage.setY(event.getScreenY() - y);
    	   stage.setOpacity(.8);
       });
     root.setOnMouseReleased((MouseEvent event)->{
    	 stage.setOpacity(1);
     });
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
