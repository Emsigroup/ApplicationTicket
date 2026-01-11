package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Priorite;
import model.Status;
import model.Rapport;
import model.Technicien;
import model.Ticket;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import utils.HibernateUtils;
import utils.UserSession;

public class TechnicienController implements Initializable {

    // ---------------- UI ----------------
    @FXML private Label username;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private ComboBox<String> comboxTypeStatus;

    @FXML private TilePane ticketsTilePane;
    @FXML private ScrollPane scrollpane;

    @FXML private AnchorPane detaille_ticket_form;
    @FXML private AnchorPane page_rapport_from;
    @FXML private AnchorPane main_form;

    @FXML private TableView<Ticket> ticket_table_detaille;
    @FXML private TableColumn<Ticket, String> column_titre_ticket;
    @FXML private TableColumn<Ticket, String> column_description_ticket;
    @FXML private TableColumn<Ticket, String> column_priorité_ticket;
    @FXML private TableColumn<Ticket, String> column_status_ticket;
    @FXML private TableColumn<Ticket, Void> rapport_col; // pour le bouton "Voir"

    @FXML private TextField champ_titre_textfield;
    @FXML private Button logout_btn;

    // ---------------- DATA ----------------
    private final SessionFactory factory = HibernateUtils.getSessionFactory();
    private List<Ticket> ticketsAffectes = new ArrayList<>();
    private Ticket ticketSelectionne;
    private Technicien technicienConnecte;

    private double x, y;

    private File file; // <-- variable globale pour le rapport PDF

    public void close() {
        System.exit(0);
    }

    public void minimize() {
        Stage stage = (Stage) main_form.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void importerRapport() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Importer un rapport");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        file = chooser.showOpenDialog(main_form.getScene().getWindow());

        if (file != null) {
            showAlert(Alert.AlertType.INFORMATION,
                    "Rapport importé : " + file.getName());
        }
    }

    @FXML
    public void retourAuxDetails() {
        page_rapport_from.setVisible(false);
        detaille_ticket_form.setVisible(true);
    }

    @FXML
    public void handleTicketBtn() {
        if (ticketSelectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun ticket sélectionné");
            return;
        }
        detaille_ticket_form.setVisible(true);
        ticketsTilePane.setVisible(false);
        page_rapport_from.setVisible(false);
    }

    @FXML
    public void handleRapportBtn() {
        if (ticketSelectionne == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un ticket");
            return;
        }
        detaille_ticket_form.setVisible(false);
        ticketsTilePane.setVisible(false);
        page_rapport_from.setVisible(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        technicienConnecte = UserSession.getTechnicien();
        if (technicienConnecte == null) {
            showAlert(Alert.AlertType.ERROR, "Aucun technicien connecté !");
            return;
        }

        username.setText(technicienConnecte.getPrenom() + " " + technicienConnecte.getNom());

        // PRIORITE FILTER
        priorityComboBox.getItems().add("Tous");
        for (Priorite p : Priorite.values()) {
            priorityComboBox.getItems().add(p.name());
        }
        priorityComboBox.setValue("Tous");
        priorityComboBox.valueProperty().addListener((o, a, b) -> afficherTickets());

        // STATUS COMBO
        for (Status s : Status.values()) {
            comboxTypeStatus.getItems().add(s.name());
        }

        // TABLE
        column_titre_ticket.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitre()));
        column_description_ticket.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescription()));
        column_priorité_ticket.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getPriorite() != null ? d.getValue().getPriorite().name() : ""
                )
        );
        column_status_ticket.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getStatus() != null ? d.getValue().getStatus().name() : ""
                )
        );

        // Ajouter bouton "Voir" dans le tableau pour ouvrir le PDF
        rapport_col.setCellFactory(col -> new TableCell<>() {
            private final Button viewButton = new Button("Voir");

            {
                viewButton.setOnAction(event -> {
                    Ticket ticket = getTableView().getItems().get(getIndex());
                    if (ticket.getRapport() != null && ticket.getRapport().getContenu() != null) {
                        try {
                            File f = new File("src/main/resources/" + ticket.getRapport().getContenu());
                            if (f.exists()) {
                                java.awt.Desktop.getDesktop().open(f);
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Fichier rapport introuvable !");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    

                    } else {
                        showAlert(Alert.AlertType.WARNING, "Aucun rapport disponible !");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });

        detaille_ticket_form.setVisible(false);
        page_rapport_from.setVisible(false);

        chargerTicketsDuTechnicien();
        chargerTicketsDepuisFichier();

        afficherTickets();
        startNotificationThread(); 

    }
    private void chargerTicketsDepuisFichier() {
        File f = new File("tickets.txt");
        if (!f.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            ticketsAffectes.clear();
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] parts = ligne.split(";");
                Ticket t = new Ticket();
                t.setId(Integer.parseInt(parts[0]));
                t.setTitre(parts[1]);
                t.setDescription(parts[2]);
                t.setPriorite(parts[3].isEmpty() ? null : Priorite.valueOf(parts[3]));
                t.setStatus(parts[4].isEmpty() ? null : Status.valueOf(parts[4]));
                ticketsAffectes.add(t);
            }
            afficherTickets();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement depuis fichier : " + e.getMessage());
        }
    }

    private void chargerTicketsDuTechnicien() {
        try (Session session = factory.openSession()) {
            technicienConnecte = session.get(Technicien.class, technicienConnecte.getId());
            ticketsAffectes = new ArrayList<>(technicienConnecte.getTicketaffecter());
        }
    }

    private void afficherTickets() {
        ticketsTilePane.getChildren().clear();

        if (ticketsAffectes == null || ticketsAffectes.isEmpty()) {
            ticketsTilePane.getChildren().add(new Label("Aucun ticket assigné"));
            return;
        }

        String filtre = priorityComboBox.getValue();
        Priorite pFiltre = "Tous".equals(filtre) ? null : Priorite.valueOf(filtre);

        ticketsAffectes.stream()
            .filter(t -> pFiltre == null || t.getPriorite() == pFiltre)  // filtrage
            .sorted((t1, t2) -> t2.getDatecreation().compareTo(t1.getDatecreation())) // tri par date desc
            .forEach(t -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TicketCard.fxml"));
                    Parent card = loader.load();
                    TicketCardController controller = loader.getController();
                    controller.setData(t);

                    card.setOnMouseClicked(e -> ouvrirDetailTicket(t));
                    ticketsTilePane.getChildren().add(card);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        // Exemple Lambda pour compter tickets non traités
        long nonTraites = ticketsAffectes.stream()
            .filter(t -> t.getStatus() == Status.FERME)
            .count();
        System.out.println("Tickets non traités : " + nonTraites);
    }

    private void startNotificationThread() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10000);

                    long nonTraites = ticketsAffectes.stream()
                            .filter(t -> t.getStatus() == Status.FERME)
                            .count();

                    if (nonTraites > 0) {
                        Platform.runLater(() -> {
                            showAlert(Alert.AlertType.INFORMATION,
                                    "Attention : " + nonTraites + " ticket(s) non traités !");
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break; 
                }
            }
        });

        thread.setDaemon(true); 
        thread.start();
    }


    private void ouvrirDetailTicket(Ticket ticket) {
        ticketSelectionne = ticket;
        detaille_ticket_form.setVisible(true);
        ticketsTilePane.setVisible(false);
        page_rapport_from.setVisible(false);

        ticket_table_detaille.setItems(FXCollections.observableArrayList(ticket));
        comboxTypeStatus.setValue(ticket.getStatus().name());
    }

    @FXML
    private void modifierStatusTicket() {
        if (ticketSelectionne == null) return;

        Status s = Status.valueOf(comboxTypeStatus.getValue());
        ticketSelectionne.setStatus(s);

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.update(ticketSelectionne);
            session.getTransaction().commit();
        }

        showAlert(Alert.AlertType.INFORMATION, "Status modifié");
        retourGrille();
    }

    private void retourGrille() {
        detaille_ticket_form.setVisible(false);
        page_rapport_from.setVisible(false);
        ticketsTilePane.setVisible(true);
        afficherTickets();
    }
    @FXML
    private void sauvegarderTicketsDansFichier() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tickets.txt"))) {
            for (Ticket t : ticketsAffectes) {
                String ligne = t.getId() + ";" + t.getTitre() + ";" 
                               + t.getDescription() + ";" + 
                               (t.getPriorite() != null ? t.getPriorite().name() : "") + ";" +
                               (t.getStatus() != null ? t.getStatus().name() : "");
                writer.write(ligne);
                writer.newLine();
            }
            showAlert(Alert.AlertType.INFORMATION, "Tickets sauvegardés dans tickets.txt !");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la sauvegarde dans fichier : " + e.getMessage());
        }
    }


    @FXML
    private void enregistrerRapport() {
        if (ticketSelectionne == null || file == null) return;

        String titre = champ_titre_textfield.getText().trim();
        if (titre.isEmpty()) return;

        try {
            // Dossier de destination dans ton projet
            File destDir = new File("src/main/resources/images");
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            // Nom du fichier = idTicket + nom original pour éviter collision
            String destFileName = ticketSelectionne.getId() + "_" + file.getName();
            File destFile = new File(destDir, destFileName);

            // Copier le fichier
            java.nio.file.Files.copy(file.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // Enregistrer dans la base le chemin relatif
            try (Session session = factory.openSession()) {
                session.beginTransaction();

                Rapport rapport = ticketSelectionne.getRapport();
                if (rapport == null) {
                    rapport = new Rapport();
                    rapport.setTicket(ticketSelectionne);
                    rapport.setTechnicien(technicienConnecte);
                    rapport.setDatecreation(new Date());
                }

                rapport.setTitre(titre);
                rapport.setContenu("images/" + destFileName); // chemin relatif

                ticketSelectionne.setRapport(rapport);

                session.saveOrUpdate(rapport);
                session.saveOrUpdate(ticketSelectionne);
                session.getTransaction().commit();
            }

            showAlert(Alert.AlertType.INFORMATION, "Rapport enregistré avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'enregistrement du rapport : " + e.getMessage());
        }
        sauvegarderTicketsDansFichier(); // <-- Phase 5

    }

    @FXML
    private void logout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.show();
            logout_btn.getScene().getWindow().hide();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType t, String msg) {
        Alert a = new Alert(t);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
