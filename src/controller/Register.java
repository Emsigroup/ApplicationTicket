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
    void register() {
        String pre = prenom.getText().trim();
        String name = nom.getText().trim();
        String userEmail = email.getText().trim();
        String pass = password.getText().trim();

        // Déterminer le rôle
        String role = "";
        if (radioclient.isSelected()) {
            role = "CLIENT";
        } else if (radiotechnicien.isSelected()) {
            role = "TECHNICIEN";
        } else if (radioadmin.isSelected()) {
            role = "ADMIN";
        }
    
        connect = ConnectionDb.ConnectDb();
        PreparedStatement prepareUser = null;

        try {
            // 1️⃣ Insérer dans la table Utilisateur
            String sqlUser = "INSERT INTO utilisateur (prenom, nom, email, motdepasse, role, idAdmin) VALUES (?, ?, ?, ?, ?, ?)";
            prepareUser = connect.prepareStatement(sqlUser, PreparedStatement.RETURN_GENERATED_KEYS);

            prepareUser.setString(1, pre);
            prepareUser.setString(2, name);
            prepareUser.setString(3, userEmail);
            prepareUser.setString(4, pass);
            prepareUser.setString(5, role);

            if (role.equalsIgnoreCase("admin")) {
                prepareUser.setNull(6, java.sql.Types.INTEGER); // Admin → idAdmin = NULL
            } else {
                prepareUser.setInt(6, Session.adminId); // Client ou Technicien
            }

            int row = prepareUser.executeUpdate();
            if (row > 0) {
                ResultSet rs = prepareUser.getGeneratedKeys();
                int userId = 0;
                if (rs.next()) {
                    userId = rs.getInt(1); // ID du nouvel utilisateur
                }

                // 2️⃣ Insertion spécifique selon le rôle
                insertByRole(userId, role);

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText(role + " créé avec succès !");
                alert.showAndWait();

                // 3️⃣ Retour au login
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'enregistrement : " + e.getMessage());
            alert.showAndWait();
        }
    }

    // Méthode pour créer Admin, Client ou Technicien
    private void insertByRole(int userId, String role) throws Exception {
        String sql = "";
        PreparedStatement ps = null;

        if (role.equalsIgnoreCase("client")) {
            sql = "INSERT INTO client (id) VALUES (?)";
            ps = connect.prepareStatement(sql);
            ps.setInt(1, userId);

        } else if (role.equalsIgnoreCase("technicien")) {
            sql = "INSERT INTO technicien (id, specialite) VALUES (?, ?)";
            ps = connect.prepareStatement(sql);
            ps.setInt(1, userId);
            String spec = specialite.getText().trim();
            if (spec.isEmpty()) spec = "Non définie";
            ps.setString(2, spec);

        } else if (role.equalsIgnoreCase("admin")) {
            sql = "INSERT INTO admin (id) VALUES (?)";
            ps = connect.prepareStatement(sql);
            ps.setInt(1, userId);
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
