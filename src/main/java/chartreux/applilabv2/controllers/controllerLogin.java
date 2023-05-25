package chartreux.applilabv2.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class controllerLogin {

    @FXML
    private Button connexionButton;

    @FXML
    private Label loginMessageLabel;
    @FXML
    private PasswordField passwordLabel;

    @FXML
    private TextField usernameLabel;


    @FXML
    void onClickLogin(ActionEvent event) {
        loginMessageLabel.setText("Mauvais identifiants");
    }
}
