package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import model.Ticket;
import model.Status;

import java.net.URL;
import java.util.ResourceBundle;

public class TicketCardController implements Initializable {

    @FXML private Label priorite;
    @FXML private Label status;
    @FXML private AnchorPane statusLabel;
    @FXML private Label titreticket_card;

    private Ticket ticket;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // rien
    }

    public void setData(Ticket ticket) {
        this.ticket = ticket;

        if (ticket == null) {
            clearData();
            return;
        }

        // Titre
        titreticket_card.setText(
                ticket.getTitre() != null ? ticket.getTitre() : ""
        );

        // ---------- STATUS ----------
        Status statusEnum = ticket.getStatus();
        String statusName = statusEnum != null ? statusEnum.name() : "OUVERT";
        status.setText(statusName);

        status.getStyleClass().removeAll(
                "status-ouvert",
                "status-en-cours",
                "status-resolu",
                "status-ferme"
        );

        switch (statusName) {
            case "OUVERT" -> status.getStyleClass().add("status-ouvert");
            case "EN_COURS" -> status.getStyleClass().add("status-en-cours");
            case "RESOLU" -> status.getStyleClass().add("status-resolu");
            case "FERME" -> status.getStyleClass().add("status-ferme");
            default -> status.getStyleClass().add("status-ouvert");
        }

        // ---------- PRIORITÉ ----------
        String prioriteStr = ticket.getPriorite() != null
                ? ticket.getPriorite().name()
                : "NORMAL";

        priorite.setText(prioriteStr);

        priorite.getStyleClass().removeAll(
                "priorite-basse",
                "priorite-moyenne",
                "priorite-haute"
        );

        switch (prioriteStr) {
            case "LOW" -> priorite.getStyleClass().add("priorite-basse");
            case "NORMAL" -> priorite.getStyleClass().add("priorite-moyenne");
            case "HIGH" -> priorite.getStyleClass().add("priorite-haute");
        }
    }

    private void clearData() {
        titreticket_card.setText("");
        status.setText("");
        priorite.setText("");
        ticket = null;
    }

    public Ticket getTicket() {
        return ticket;
    }
}
