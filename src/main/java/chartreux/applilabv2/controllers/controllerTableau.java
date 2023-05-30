package chartreux.applilabv2.controllers;

import chartreux.applilabv2.Entity.User;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

import chartreux.applilabv2.HelloApplication;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;

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

        buttonAddUser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                doAddUser();
            }
        });
    }

    private void doAddUser(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("manageUser.fxml"));
            controllerManageUser controllerManageUser = new controllerManageUser(cnx,user);
            fxmlLoader.setController(controllerManageUser);

            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
