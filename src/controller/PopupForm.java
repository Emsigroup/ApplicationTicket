package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PopupForm {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;

    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    private Stage dialogStage;
    private boolean okClicked = false;

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public String getNom() {
        return nomField.getText();
    }

    public String getPrenom() {
        return prenomField.getText();
    }

    public String getEmail() {
        return emailField.getText();
    }

    @FXML
    private void valider() {
        okClicked = true;
        dialogStage.close();
    }

    @FXML
    private void annuler() {
        dialogStage.close();
    }
}
