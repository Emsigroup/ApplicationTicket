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
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import model.Utilisateur;
import utils.Session;


public class Register implements Initializable  {
    @FXML
    private TextField specialite;
    @FXML
    private RadioButton radiotechnicien;
	@FXML
	private RadioButton radioclient;
   
	@FXML
	private RadioButton radioadmin;
	   @FXML
    private TextField nom;

    @FXML
    private PasswordField password;

    @FXML
    private TextField prenom;
    @FXML
    private TextField email;
    @FXML
    private Button registerbtn;

    @FXML
    private StackPane stack_form;

   
    private Connection  connect;
    private PreparedStatement  prepare;
    
    @FXML
    
    void register( ) {
    	String pre=prenom.getText();
    	String name=nom.getText();
    	String user=email.getText();
    	String pass=password.getText();
    	
    	 // Récupérer le rôle sélectionné
       // String role = radioclient.isSelected() ? "Client" : "Admin" ;
    	String role="";
        if(radioclient.isSelected()) {
        	role = "client";
        }else if (radiotechnicien.isSelected()){
        	role = "Technicien";
        }else if ( radioadmin.isSelected()) {
        	role = "Admin";
        }

     String sqlUser = "INSERT INTO utilisateur (prenom, nom, email, motdepasse, role, idAdmin) VALUES (?, ?, ?, ?, ?,?)";

connect = ConnectionDb.ConnectDb();

try {
    prepare = connect.prepareStatement(sqlUser, PreparedStatement.RETURN_GENERATED_KEYS);

    prepare.setString(1, pre);
    prepare.setString(2, name);
    prepare.setString(3, user);
    prepare.setString(4, pass);
    prepare.setString(5, role);
    prepare.setInt(6, Session.adminId); 


    int row = prepare.executeUpdate();

    if (row > 0) {

       
        ResultSet rs = prepare.getGeneratedKeys();
        int userId = 0;
        if (rs.next()) {
            userId = rs.getInt(1);
        }

        // 🔽 Insérer selon le rôle
        insertByRole(userId, role);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Utilisateur enregistré avec succès !");
        alert.showAndWait();

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }

} catch (Exception e) {
    e.printStackTrace();
}

}
   
    private void insertByRole(int userId, String role) throws Exception {
        String sql = "";

        if (role.equalsIgnoreCase("client")) {
            sql = "INSERT INTO client (id) VALUES (?)";

        } else if (role.equalsIgnoreCase("technicien")) {
            sql = "INSERT INTO technicien (id, specialite) VALUES (?, ?)";

        } else if (role.equalsIgnoreCase("admin")) {
            sql = "INSERT INTO admin (id) VALUES (?)";
        }

        PreparedStatement ps = connect.prepareStatement(sql);
        ps.setInt(1, userId);

        // cas technicien → champ spécialité
        if (role.equalsIgnoreCase("technicien")) {
            String spec = specialite.getText().trim();
            if (spec.isEmpty()) {
                spec = "Non définie"; // valeur par défaut si vide
            }
            ps.setString(2, spec);
        }

        ps.executeUpdate();
    }

    public void exit() {
    	System.exit(0);
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        ToggleGroup group = new ToggleGroup();
        radioclient.setToggleGroup(group);
        radioadmin.setToggleGroup(group);
        radiotechnicien.setToggleGroup(group);

        radioclient.setSelected(true);
        specialite.setVisible(false); // caché par défaut

        // Listener sur le ToggleGroup
        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (radiotechnicien.isSelected()) {
                specialite.setVisible(true); // afficher si Technicien
            } else {
                specialite.setVisible(false); // cacher sinon
            }
        });
    }


  
}
