package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import DbTest.ConnectionDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.Session;
import model.Priorite;
import model.Status;
import model.Ticket;
import model.Utilisateur;
import model.Client;
import model.Technicien;

public class Clients  implements Initializable{
	//FXML ATTRIBUT 

	 @FXML
	    private Button add_ticket;

	    @FXML
	    private TableColumn<Ticket, String> client_col;

	    @FXML
	    private Button close;

	    @FXML
	    private Button dashboard_btn;

	    @FXML
	    private TextArea description;

	    @FXML
	    private TableColumn<Ticket, String> description_col;

	    @FXML
	    private Button logout;

	    @FXML
	    private AnchorPane main_form;

	    @FXML
	    private Button minimize;

	    @FXML
	    private Label nom_client;

	    @FXML
	    private TableColumn<Ticket, String> priorite_col;

	    @FXML
	    private TableColumn<Ticket, String> status_col;

	    @FXML
	    private TableView<Ticket> table_ticket;

	    @FXML
	    private TableColumn<Ticket, String> technicien_col;

	    @FXML
	    private Button ticket_btn;

	    @FXML
	    private TableColumn<Ticket, Integer> ticket_col_id;

	    @FXML
	    private AnchorPane ticket_form;

	    @FXML
	    private ComboBox<String> titre;

	    @FXML
	    private TableColumn<?, ?> titre_col;

	    @FXML
	    private Button update_ticket;


    private double x=0;
    private double y=0;
    
  //METHODE CONTROLLER 
    
    
    private Connection  connect;
    private PreparedStatement  prepare;
    Date date = new Date(0);
    private ObservableList<Ticket> listTickets;

    
    private void showTickets() {
        listTickets = listTicket();

        ticket_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        technicien_col.setCellValueFactory(cellData -> {
            Technicien tech = cellData.getValue().getTechnicien();
            if (tech == null || tech.getNom() == null || tech.getNom().isEmpty()) {
                return new javafx.beans.property.SimpleStringProperty("");
            }
            return new javafx.beans.property.SimpleStringProperty(tech.getNom());
        });
        titre_col.setCellValueFactory(new PropertyValueFactory<>("titre"));
        description_col.setCellValueFactory(new PropertyValueFactory<>("description"));
        priorite_col.setCellValueFactory(new PropertyValueFactory<>("priorite"));
        status_col.setCellValueFactory(new PropertyValueFactory<>("status"));

        table_ticket.setItems(listTickets);
    }

    
    
    public ObservableList<Ticket> listTicket() {
        ObservableList<Ticket> tickets = FXCollections.observableArrayList();

        String sql = "SELECT t.id, t.idTechnicien, t.titre, t.description, t.priorite, t.status " +
                     "FROM ticket t WHERE t.idClient = ?";

        try {
            connect = ConnectionDb.ConnectDb();
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, Session.clientId);

            ResultSet rs = prepare.executeQuery();

            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getInt("id"));
                ticket.setTitre(rs.getString("titre"));
                ticket.setDescription(rs.getString("description"));
                ticket.setPriorite(Priorite.valueOf(rs.getString("priorite")));
                ticket.setStatus(Status.valueOf(rs.getString("status")));

                ticket.setClient(null);

                int idTech = rs.getInt("idTechnicien");

                if (idTech == 0) {
                    Technicien tech = new Technicien();
                    tech.setNom(""); 
                    ticket.setTechnicien(tech);
                } else {
                    ticket.setTechnicien(getTechnicienById(idTech));
                }

                tickets.add(ticket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tickets;
    }


    public Client getClientById(int id) {
        Client client = null; 
        String sql = "SELECT * FROM client WHERE id = ?";

        try {
            Connection connect = ConnectionDb.ConnectDb();
            PreparedStatement ps = connect.prepareStatement(sql);
            ps.setInt(1, id);
            var rs = ps.executeQuery();

            if(rs.next()) {
                client = new Client();
                client.setId(rs.getInt("id"));          
                client.setNom(rs.getString("nom"));     
                client.setPrenom(rs.getString("prenom"));
                client.setEmail(rs.getString("email"));
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return client;
    }

    private Technicien getTechnicienById(int id) {
        Technicien tech = null;
        String sql = "SELECT u.id, u.nom, u.prenom, u.email, t.specialite " +
                     "FROM utilisateur u " +
                     "JOIN technicien t ON u.id = t.id " +
                     "WHERE u.role = 'technicien' AND u.id = ?";
        try {
            connect = ConnectionDb.ConnectDb();
            PreparedStatement ps = connect.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                tech = new Technicien();
                tech.setId(rs.getInt("id"));
                tech.setNom(rs.getString("nom"));
                tech.setPrenom(rs.getString("prenom"));
                tech.setEmail(rs.getString("email"));  // <- email récupéré depuis utilisateur
                tech.setSpecialite(rs.getString("specialite"));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return tech;
    }


    public void AddTicket() {
        String sql = "INSERT INTO ticket (datecreation, description, priorite, status, titre, idClient, idTechnicien) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        connect = ConnectionDb.ConnectDb();

        try {
        	
        	
        	
        	if(description.getText().isEmpty() || titre.getValue() == null || titre.getValue().isEmpty() ) {
        		Alert alert = new Alert(AlertType.ERROR);
        		alert.setTitle("Error Message");
        		alert.setHeaderText(null);
        		alert.setContentText("Remplie tu les champs sont obligatoir ");
        		return;
        	}else {
            prepare = connect.prepareStatement(sql);

           
            java.sql.Date today = java.sql.Date.valueOf(LocalDate.now());
            prepare.setDate(1, today);

           
            prepare.setString(2, description.getText());

            Priorite prioriteDefaut = Priorite.NORMAL;
            prepare.setString(3, prioriteDefaut.name());

            Status statusDefaut = Status.OUVERT;
            prepare.setString(4, statusDefaut.name()); 

            String selectedSpecialite = titre.getValue(); 
            if(selectedSpecialite == null || selectedSpecialite.isEmpty()) {
                selectedSpecialite = "Non défini";
            }
            prepare.setString(5, selectedSpecialite);

            prepare.setInt(6, Session.clientId); 

            prepare.setInt(7, 0); 

            int row = prepare.executeUpdate();
            if(row > 0) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setContentText("Ticket ajouté !");
                alert.showAndWait();
                showTickets();

            } else {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setContentText("Erreur d'ajout");
                alert.showAndWait();
            }
        	}
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    
    private void loadSpecialites() {
        String sql = "SELECT DISTINCT specialite FROM technicien";
        connect = ConnectionDb.ConnectDb();

        try {
            prepare = connect.prepareStatement(sql);
            var rs = prepare.executeQuery();

            while(rs.next()) {
                titre.getItems().add(rs.getString("specialite"));
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    
	    public void displayEmail() {
	    	String data=getData.email;
	    	nom_client.setText(data.substring(0 , 1).toUpperCase() + data.substring(1) );
	    }
	    
	   public void minimize() {
	    	
	    	Stage stage =(Stage)main_form.getScene().getWindow();
	    	stage.setIconified(true);
	    }
	    public void close() {
	    	System.exit(0);
	    }
	    
	    
	    
	    public void logout() throws IOException {
	    	
	    	try {
					    Alert alert= new Alert (AlertType.CONFIRMATION);
					    alert.setTitle("Confoirmation Message");
					    alert.setHeaderText(null);
					    alert.setContentText("Vous êtes sûre pour le logout ? ");
					    Optional<ButtonType> option =alert.showAndWait();
					    if(option.get().equals(ButtonType.OK)) {
					    	
					    	logout.getScene().getWindow().hide();
						     Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
						     Stage stage= new Stage();
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
	    		}catch (Exception e) {
	    		    e.printStackTrace();
	    		}
	    	
	    
	    												
	    }

		public void initialize(URL arg0, ResourceBundle arg1) {
			displayEmail();			
		    loadSpecialites(); 
		    showTickets(); 

		}
}
