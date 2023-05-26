package chartreux.applilabv2.controllers;

import chartreux.applilabv2.Entity.User;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class controllerTableau implements Initializable {
    @FXML
    private Label welcomeLabel;

    @FXML
    private Button buttonAddUser;
    private Connection cnx;
    private User user;

    public controllerTableau(Connection cnx, User user) {
        this.cnx = cnx;
        this.user = user;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcomeLabel.setText("Bienvenu "+user.getPrenom());
    }
}
