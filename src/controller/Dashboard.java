package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import com.mysql.cj.xdevapi.Statement;
import DbTest.ConnectionDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Client;
import model.Technicien;
import model.Rapport;
import model.Ticket;
import model.Status;
import model.Priorite;
import java.awt.Desktop;
import java.io.File;

public class Dashboard implements Initializable {
		@FXML
		private AreaChart<String, Number> dashboard_chart;
	
		@FXML
		private CategoryAxis xAxis;
	
		@FXML
		private NumberAxis yAxis;
		@FXML
		private Label labelTicketFerme;
	
		@FXML
		private Label labelTicketNonTraite;
	
		@FXML
		private Label labelTotalTicket;

		@FXML
		private TableColumn<Ticket, Void> action_col;

		@FXML
		private TableColumn<Ticket, String> rapport_col;
	    @FXML
	    private TextField specialite;
	    
	    @FXML
	    private Button Add_members_Btn;

	    @FXML
	    private TextField client;

	    @FXML
	    private TableColumn<Ticket, String> client_col;

	    @FXML
	    private Button close;

	    @FXML
	    private Button dashboard_btn;

	  

	    @FXML
	    private AnchorPane dashboard_form;

	    @FXML
	    private TableColumn<Ticket, Date> date_creation_col;

	    @FXML
	    private Button delete_ticket;

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
	    private Label nom_admin;

	    @FXML
	    private ComboBox<Priorite> priorite;

	    @FXML
	    private TableColumn<Ticket, String> priorite_col;

	    @FXML
	    private TextField search;
	    @FXML
	    private TableView<Ticket> table_data;

	    @FXML
	    private ComboBox<Status> status;

	    @FXML
	    private TableColumn<Ticket, Status> status_col;

	    @FXML
	    private ComboBox<Technicien> technicien;

	    @FXML
	    private TableColumn<Ticket, String> technicien_col;

	    @FXML
	    private Button ticket_btn;

	    @FXML
	    private TableColumn<Ticket,String > ticket_col_id;

	    @FXML
	    private AnchorPane ticket_form;

	    @FXML
	    private TextField titre;

	    @FXML
	    private TableColumn<Ticket, String> titre_col;

	    @FXML
	    private Button update_ticket;

	    
	    private double x=0;
	    private double y=0;
	    
	    private Connection  connect;
	    private PreparedStatement  prepare;
	    private Statement statement;
	    private ResultSet result;
	    @FXML
	    private void updateTicket(ActionEvent event) {
	        // Récupérer le ticket sélectionné
	        Ticket selectedTicket = table_data.getSelectionModel().getSelectedItem();
	        if (selectedTicket == null) {
	            Alert alert = new Alert(AlertType.WARNING);
	            alert.setHeaderText(null);
	            alert.setContentText("Veuillez sélectionner un ticket !");
	            alert.show();
	            return;
	        }

	        // Récupérer le technicien sélectionné
	        Technicien selectedTech = technicien.getSelectionModel().getSelectedItem();
	        if (selectedTech == null) {
	            Alert alert = new Alert(AlertType.WARNING);
	            alert.setHeaderText(null);
	            alert.setContentText("Veuillez sélectionner un technicien !");
	            alert.show();
	            return;
	        }

	        // Mettre à jour la base de données
	        String sql = "UPDATE ticket SET idTechnicien = ? WHERE id = ?";
	        try {
	            connect = ConnectionDb.ConnectDb();
	            prepare = connect.prepareStatement(sql);
	            prepare.setInt(1, selectedTech.getId());
	            prepare.setInt(2, selectedTicket.getId());
	            int rows = prepare.executeUpdate();

	            if (rows > 0) {
	                // Mettre à jour localement la TableView
	                selectedTicket.setTechnicien(selectedTech);
	                table_data.refresh();

	                Alert alert = new Alert(AlertType.INFORMATION);
	                alert.setHeaderText(null);
	                alert.setContentText("Technicien mis à jour avec succès !");
	                alert.show();
	            } else {
	                Alert alert = new Alert(AlertType.ERROR);
	                alert.setHeaderText(null);
	                alert.setContentText("Erreur lors de la mise à jour !");
	                alert.show();
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	            Alert alert = new Alert(AlertType.ERROR);
	            alert.setHeaderText(null);
	            alert.setContentText("Erreur : " + e.getMessage());
	            alert.show();
	        }
	    }

	    private Technicien getTechnicienById(int id) {
	        Technicien tech = null;
	        String sql = """
	            SELECT u.id, u.nom, u.prenom, u.email
	            FROM utilisateur u
	            WHERE u.id = ? AND u.role = 'technicien'
	        """;

	        try {
	            connect = ConnectionDb.ConnectDb();
	            PreparedStatement ps = connect.prepareStatement(sql);
	            ps.setInt(1, id);
	            ResultSet rs = ps.executeQuery();

	            if (rs.next()) {
	                tech = new Technicien();
	                tech.setId(rs.getInt("id"));
	                tech.setNom(rs.getString("nom"));
	                tech.setPrenom(rs.getString("prenom"));
	                tech.setEmail(rs.getString("email")); // IMPORTANT
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return tech;
	    }

	    private Client getClientById(int id) {
	        Client client = null;
	        String sql = "SELECT id, email FROM utilisateur WHERE id = ? AND role = 'client'";
	        try {
	            connect = ConnectionDb.ConnectDb();
	            PreparedStatement ps = connect.prepareStatement(sql);
	            ps.setInt(1, id);
	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) {
	                client = new Client();
	                client.setId(rs.getInt("id"));
	                client.setEmail(rs.getString("email"));
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return client;
	    }

	    private ObservableList<Technicien> getAllTechniciens() {
	        ObservableList<Technicien> listTech = FXCollections.observableArrayList();

	        String sql = """
	            SELECT 
	                u.id,
	                u.nom,
	                u.prenom,
	                u.email,
	                t.specialite
	            FROM utilisateur u
	            INNER JOIN technicien t ON u.id = t.id
	            WHERE u.role = 'technicien'
	        """;

	        try {
	            connect = ConnectionDb.ConnectDb();
	            prepare = connect.prepareStatement(sql);
	            result = prepare.executeQuery();

	            while (result.next()) {
	                Technicien tech = new Technicien();
	                tech.setId(result.getInt("id"));
	                tech.setNom(result.getString("nom"));
	                tech.setPrenom(result.getString("prenom"));
	                tech.setEmail(result.getString("email"));
	                tech.setSpecialite(result.getString("specialite")); // ✅ clé
	                listTech.add(tech);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return listTech;
	    }

	    
	    @FXML
	    private void handleTableClick(MouseEvent event) {
	        Ticket selectedTicket = table_data.getSelectionModel().getSelectedItem();
	        if (selectedTicket == null) return;

	        client.setText(selectedTicket.getClient() != null ? String.valueOf(selectedTicket.getClient().getId()) : "");
	        client.setEditable(false);

	        titre.setText(selectedTicket.getTitre() != null ? selectedTicket.getTitre() : "");
	        titre.setEditable(false); 

	        description.setText(selectedTicket.getDescription() != null ? selectedTicket.getDescription() : "");
	        description.setEditable(false); 


	        if (selectedTicket.getPriorite() != null) {
	            priorite.getSelectionModel().select(selectedTicket.getPriorite()); 
	            priorite.setItems(FXCollections.observableArrayList(Priorite.values()));
	        } else {
	            priorite.getSelectionModel().clearSelection();
	        }

	        if (selectedTicket.getStatus() != null) {
	            status.getSelectionModel().select(selectedTicket.getStatus()); 
	            status.setItems(FXCollections.observableArrayList(Status.values()));

	        } else {
	            status.getSelectionModel().clearSelection();
	        }

	        if (selectedTicket.getTechnicien() != null) {
	            Technicien tech = selectedTicket.getTechnicien();
	            for (Technicien t : technicien.getItems()) {
	                if (t.getId() == tech.getId()) {
	                    technicien.getSelectionModel().select(t);
	                    
	                    break;
	                }
	            }
	        } else {
	            technicien.getSelectionModel().clearSelection();
	        }

	    }

	    public ObservableList<Ticket> listTicket() {

	        String sql = "SELECT * FROM ticket";
	        ObservableList<Ticket> listTicket = FXCollections.observableArrayList();

	        connect = ConnectionDb.ConnectDb();

	        try {
	            prepare = connect.prepareStatement(sql);
	            result = prepare.executeQuery();

	            while (result.next()) {

	               
	                int id = result.getInt("id");
	                String titre = result.getString("titre");
	                String description = result.getString("description");
	                Date datecreation = result.getDate("datecreation");

	                
	                Status status = null;
	                try {
	                    status = Status.valueOf(result.getString("status"));
	                } catch (IllegalArgumentException e) {
	                    status = null;
	                }

	                Priorite priorite = null;
	                try {
	                	priorite = Priorite.valueOf(result.getString("priorite"));
	                } catch (IllegalArgumentException e) {
	                	priorite = null;
	                }
	             
	                int idClient = result.getInt("idClient");
	                Client client = getClientById(idClient);

	                int idTech = result.getInt("idTechnicien");
	                Technicien technicien = null;

	                if (idTech != 0) {
	                    technicien = getTechnicienById(idTech);
	                }


	                Rapport rapport = null;

	                try {
	                    String sqlRapport = "SELECT * FROM rapport WHERE idTicket = ?";
	                    PreparedStatement psRapport = connect.prepareStatement(sqlRapport);
	                    psRapport.setInt(1, id);
	                    ResultSet rsRapport = psRapport.executeQuery();
	                    
	                    if (rsRapport.next()) {
	                        rapport = new Rapport();
	                        rapport.setId(rsRapport.getInt("id"));
	                        rapport.setTitre(rsRapport.getString("titre"));
	                        rapport.setContenu(rsRapport.getString("contenu")); // chemin relatif ou absolu
	                        rapport.setDatecreation(rsRapport.getDate("datecreation"));
	                    }
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	                Ticket ticket = new Ticket(
	                	    id,
	                	    titre,
	                	    description,
	                	    datecreation,
	                	    status,
	                	    priorite,
	                	    rapport,
	                	    client,
	                	    technicien
	                	);

	                listTicket.add(ticket);
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return listTicket;
	    }

	    
	    private ObservableList<Ticket> listTickets;
	    
	    public void listTicketShowList() {
	    	
	    	listTickets = listTicket();
	    	ticket_col_id.setCellValueFactory(new PropertyValueFactory<>("id"));

	    	client_col.setCellValueFactory(cellData -> {
	    	    Client c = cellData.getValue().getClient();
	    	    if (c == null || c.getEmail() == null) {
	    	        return new javafx.beans.property.SimpleStringProperty("");
	    	    }
	    	    return new javafx.beans.property.SimpleStringProperty(c.getEmail());
	    	});

	    	 technicien_col.setCellValueFactory(cellData -> {
	             Technicien tech = cellData.getValue().getTechnicien();
	             if (tech == null || tech.getEmail() == null || tech.getEmail().isEmpty()) {
	                 return new javafx.beans.property.SimpleStringProperty("");
	             }
	             return new javafx.beans.property.SimpleStringProperty(tech.getEmail());
	         });	    	titre_col.setCellValueFactory(new PropertyValueFactory<>("titre"));
	    	description_col.setCellValueFactory(new PropertyValueFactory<>("description"));
	    	priorite_col.setCellValueFactory(new PropertyValueFactory<>("priorite"));
	    	status_col.setCellValueFactory(new PropertyValueFactory<>("status"));
	    	date_creation_col.setCellValueFactory(new PropertyValueFactory<>("datecreation"));
	    	rapport_col.setCellValueFactory(cellData -> {
	    	    Rapport r = cellData.getValue().getRapport();
	    	    if (r != null && r.getTitre() != null) {
	    	        return new javafx.beans.property.SimpleStringProperty(r.getTitre());
	    	    }
	    	    return new javafx.beans.property.SimpleStringProperty("Aucun");
	    	});
	    	// Colonne pour le bouton "Voir Rapport"
	    	action_col.setCellFactory(col -> new javafx.scene.control.TableCell<>() {
	    	    private final Button btn = new Button("Voir rapport");

	    	    {
	    	        btn.setOnAction(event -> {
	    	            Ticket ticket = getTableView().getItems().get(getIndex());
	    	            if (ticket.getRapport() != null) {
	    	                showRapport(ticket.getRapport());
	    	            }
	    	        });
	    	    }

	    	    @Override
	    	    protected void updateItem(Void item, boolean empty) {
	    	        super.updateItem(item, empty);
	    	        if (empty || getTableView().getItems().get(getIndex()).getRapport() == null) {
	    	            setGraphic(null); // pas de bouton si pas de rapport
	    	        } else {
	    	            setGraphic(btn);
	    	        }
	    	    }
	    	});

	    	table_data.setItems(listTickets);
}
	    private void showRapport(Rapport rapport) {
	        if (rapport.getContenu() != null && !rapport.getContenu().isEmpty()) {
	            try {
	                File file = new File(rapport.getContenu()); // chemin du PDF
	                if (file.exists()) {
	                    if (Desktop.isDesktopSupported()) {
	                        Desktop.getDesktop().open(file); // ouvre le PDF avec le lecteur par défaut
	                    } else {
	                        Alert alert = new Alert(AlertType.ERROR);
	                        alert.setTitle("Erreur");
	                        alert.setHeaderText(null);
	                        alert.setContentText("Impossible d'ouvrir le PDF sur ce système.");
	                        alert.showAndWait();
	                    }
	                } else {
	                    Alert alert = new Alert(AlertType.WARNING);
	                    alert.setTitle("Fichier introuvable");
	                    alert.setHeaderText(null);
	                    alert.setContentText("Le fichier PDF n'existe pas : " + rapport.getContenu());
	                    alert.showAndWait();
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	                Alert alert = new Alert(AlertType.ERROR);
	                alert.setTitle("Erreur");
	                alert.setHeaderText(null);
	                alert.setContentText("Impossible d'ouvrir le PDF : " + e.getMessage());
	                alert.showAndWait();
	            }
	        } else {
	            Alert alert = new Alert(AlertType.INFORMATION);
	            alert.setTitle("Rapport vide");
	            alert.setHeaderText(null);
	            alert.setContentText("Aucun fichier PDF disponible pour ce rapport.");
	            alert.showAndWait();
	        }
	    }
	    
	    
	    /*public void listTicketSelect() {
	    	Ticket tickets= table_data.getSelectionModel().getSelectedItems();
	    	int num =table_data.getSelectionModel().getSelectedIndex();	    
	    	
	    	if((num - 1 ) < -1) {
	    		return  ;
	    		
	    	}
	    	ticket_col_id.setText(String.valueOf(tickets.getId()));
	    	client.setText(tickets.getClient());
	    	
	    	
	    			}*/
	    
	    private void updateDashboardCounts() {
	        int total = listTickets.size();
	        int ferme = 0;
	        int nonTraite = 0;

	        for (Ticket t : listTickets) {
	            if (t.getStatus() != null && t.getStatus() == Status.FERME) {
	                ferme++;
	            } else {
	                nonTraite++;
	            }
	        }

	        labelTicketFerme.setText(String.valueOf(ferme));
	        labelTicketNonTraite.setText(String.valueOf(nonTraite));
	        labelTotalTicket.setText(String.valueOf(total));
	    }

	    public void switchForm(ActionEvent event ) throws IOException {
	    	
	    	if(event.getSource() == dashboard_btn) {
	    		dashboard_form.setVisible(true);
	    		ticket_form.setVisible(false);
	    		
	    		dashboard_btn.setStyle("-fx-background-color:linear-gradient(to bottom right ,#2e48bc, #772d8d);");
	    		ticket_btn.setStyle("-fx-background-color:transparent");
	    		Add_members_Btn.setStyle("-fx-background-color:transparent");
	    		
	    	}else if(event.getSource() == ticket_btn ){
	    		dashboard_form.setVisible(false);
	    		ticket_form.setVisible(true);
	    		

	    		ticket_btn.setStyle("-fx-background-color:linear-gradient(to bottom right ,#2e48bc, #772d8d);");
	    		dashboard_btn.setStyle("-fx-background-color:transparent");
	    		Add_members_Btn.setStyle("-fx-background-color:transparent");

	    		//listTicketShowList();
	    	}else if (event.getSource() == Add_members_Btn) {
	    		dashboard_form.setVisible(false);
	    		ticket_form.setVisible(false);
	    		 Parent root = FXMLLoader.load(getClass().getResource("/fxml/register.fxml"));
			     Stage stage =new Stage();
			     Scene scene = new Scene(root);
			     stage.setScene(scene);
			        stage.show();
	    		Add_members_Btn.setStyle("-fx-background-color:linear-gradient(to bottom right ,#2e48bc, #772d8d);");
	    		ticket_btn.setStyle("-fx-background-color:transparent");
	    		dashboard_btn.setStyle("-fx-background-color:transparent");
	    		
	    	}
	    }
	    public void displayEmail() {
	    	String data=getData.email;
	    	nom_admin.setText(data.substring(0 , 1).toUpperCase() + data.substring(1) );
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
	    private void assignTechnicienToTicket(Ticket ticket, Technicien tech) {
	        String sql = "UPDATE ticket SET idTechnicien = ? WHERE id = ?";

	        try {
	            connect = ConnectionDb.ConnectDb();
	            prepare = connect.prepareStatement(sql);
	            prepare.setInt(1, tech.getId());
	            prepare.setInt(2, ticket.getId());
	            int rows = prepare.executeUpdate();

	            if (rows > 0) {
	                ticket.setTechnicien(tech); // Mise à jour locale
	                table_data.refresh();       // Refresh TableView

	                Alert alert = new Alert(AlertType.INFORMATION);
	                alert.setHeaderText(null);
	                alert.setContentText("Technicien assigné avec succès !");
	                alert.show();
	            } else {
	                Alert alert = new Alert(AlertType.ERROR);
	                alert.setHeaderText(null);
	                alert.setContentText("Erreur lors de l'assignation !");
	                alert.show();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            Alert alert = new Alert(AlertType.ERROR);
	            alert.setHeaderText(null);
	            alert.setContentText("Erreur : " + e.getMessage());
	            alert.show();
	        }
	    }

	    public void minimize() {
	    	
	    	Stage stage =(Stage)main_form.getScene().getWindow();
	    	stage.setIconified(true);
	    }
	    public void close() {
	    	System.exit(0);
	    }
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		displayEmail();
		listTicketShowList();
		updateDashboardCounts();
		table_data.setOnMouseClicked(this::handleTableClick);
		table_data.setOnMouseClicked(this::handleTableClick);

		technicien.setItems(getAllTechniciens());

		technicien.setCellFactory(cb -> new javafx.scene.control.ListCell<>() {
		    @Override
		    protected void updateItem(Technicien tech, boolean empty) {
		        super.updateItem(tech, empty);
		        if (empty || tech == null) {
		            setText("");
		        } else {
		            setText(tech.getEmail()); 
		        }
		    }
		});

		technicien.setButtonCell(new javafx.scene.control.ListCell<>() {
		    @Override
		    protected void updateItem(Technicien tech, boolean empty) {
		        super.updateItem(tech, empty);
		        if (empty || tech == null) {
		            setText("");
		        } else {
		            setText(tech.getEmail()); 
		        }
		    }
		});
		technicien.getSelectionModel()
	    .selectedItemProperty()
	    .addListener((obs, oldTech, newTech) -> {
	        if (newTech != null) {
	            specialite.setText(newTech.getSpecialite());
	        } else {
	            specialite.clear();
	        }
	    });
		technicien.getSelectionModel().selectedItemProperty().addListener((obs, oldTech, newTech) -> {
		    if (newTech != null) {
		        specialite.setText(newTech.getSpecialite());

		        // Si un ticket est sélectionné, mettre à jour la DB automatiquement
		        Ticket selectedTicket = table_data.getSelectionModel().getSelectedItem();
		        if (selectedTicket != null) {
		            assignTechnicienToTicket(selectedTicket, newTech);
		        }
		    } else {
		        specialite.clear();
		    }
		});

	specialite.setEditable(false);
	
	// Exemple de données simulées : tu peux les récupérer depuis ta DB
    Map<String, Integer> ticketsParJour = new LinkedHashMap<>();
    ticketsParJour.put("2026-01-01", 5);
    ticketsParJour.put("2026-01-02", 8);
    ticketsParJour.put("2026-01-03", 12);
    ticketsParJour.put("2026-01-04", 7);

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Tickets Total");

    for (Map.Entry<String, Integer> entry : ticketsParJour.entrySet()) {
        series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
    }

    dashboard_chart.getData().clear();
    dashboard_chart.getData().add(series);

    // Optional : personnaliser axes
    xAxis.setLabel("Date");
    yAxis.setLabel("Nombre Tickets");
	}

}
