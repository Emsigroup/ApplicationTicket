package controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import DbTest.ConnectionDb;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Utilisateur;
import utils.Session;


public class Login implements Initializable  {

	  @FXML
	    private FontAwesomeIcon close;

	    @FXML
	    private Button loginbtn;

	    @FXML
	    private AnchorPane main_form;

	    @FXML
	    private PasswordField password;

	    @FXML
	    private StackPane stack_form;

	    @FXML
	    private TextField email;
	    
	    private Connection  connect;
	    private PreparedStatement  prepare;
	    private ResultSet result;
	    
	    private double x=0;
	    private double y=0;
	    public void login() {
	        String user = email.getText();
	        String pass = password.getText();
	        String sql = "SELECT * FROM utilisateur WHERE email = ? AND motdepasse = ?";
	        connect = ConnectionDb.ConnectDb();

	        try {
	            prepare = connect.prepareStatement(sql);
	            prepare.setString(1, user);
	            prepare.setString(2, pass);

	            result = prepare.executeQuery();

	            Alert alert;

	            if (result.next()) {
	                getData.email = email.getText();

	              
	                int userId = result.getInt("id");  
	                String role = result.getString("role");

	                if (role.equalsIgnoreCase("Admin")) {
	                    Session.adminId = userId;
	                    System.out.println("ID admin connecté = " + Session.adminId); 
	                    loginbtn.getScene().getWindow().hide();

		                Parent root = FXMLLoader.load(getClass().getResource("/fxml/admin.fxml"));
		                Stage stage = new Stage();
		                Scene scene = new Scene(root);

		                root.setOnMousePressed((MouseEvent event) -> {
		                    x = event.getSceneX();
		                    y = event.getSceneY();
		                });
		                root.setOnMouseDragged((MouseEvent event) -> {
		                    stage.setX(event.getScreenX() - x);
		                    stage.setY(event.getScreenY() - y);
		                    stage.setOpacity(.8);
		                });
		                stage.initStyle(StageStyle.TRANSPARENT);

		                stage.setScene(scene);
		                stage.show();
	                }else if (role.equalsIgnoreCase("Client")) {
	                	Session.clientId = userId;
	                    System.out.println("ID client connecté = " + Session.clientId);
	                	 loginbtn.getScene().getWindow().hide();

	 	                Parent root = FXMLLoader.load(getClass().getResource("/fxml/client.fxml"));
	 	                Stage stage = new Stage();
	 	                Scene scene = new Scene(root);

	 	                root.setOnMousePressed((MouseEvent event) -> {
	 	                    x = event.getSceneX();
	 	                    y = event.getSceneY();
	 	                });
	 	                root.setOnMouseDragged((MouseEvent event) -> {
	 	                    stage.setX(event.getScreenX() - x);
	 	                    stage.setY(event.getScreenY() - y);
	 	                    stage.setOpacity(.8);
	 	                });
	 	                stage.initStyle(StageStyle.TRANSPARENT);

	 	                stage.setScene(scene);
	 	                stage.show();
	                }
	                else {
	                alert = new Alert(AlertType.INFORMATION);
	                alert.setTitle("Information Message");
	                alert.setHeaderText(null);
	                alert.setContentText("Succès Login !");
	                alert.showAndWait();

	                loginbtn.getScene().getWindow().hide();

	                Parent root = FXMLLoader.load(getClass().getResource("/fxml/client.fxml"));
	                Stage stage = new Stage();
	                Scene scene = new Scene(root);

	                root.setOnMousePressed((MouseEvent event) -> {
	                    x = event.getSceneX();
	                    y = event.getSceneY();
	                });
	                root.setOnMouseDragged((MouseEvent event) -> {
	                    stage.setX(event.getScreenX() - x);
	                    stage.setY(event.getScreenY() - y);
	                    stage.setOpacity(.8);
	                });
	                stage.initStyle(StageStyle.TRANSPARENT);

	                stage.setScene(scene);
	                stage.show();}
	            } else {
	                alert = new Alert(AlertType.ERROR);
	                alert.setTitle("Error Message");
	                alert.setHeaderText(null);
	                alert.setContentText("Wrong email ou mot de passe !");
	                alert.showAndWait();
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    
	    public void exit() {
	    	System.exit(0);
	    }
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}

  
}
