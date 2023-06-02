package chartreux.applilabv2.controllers;

import chartreux.applilabv2.DAO.DAOUserInLab;
import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Entity.Role;
import chartreux.applilabv2.Entity.User;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.ResourceBundle;

import chartreux.applilabv2.HelloApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class controllerTableau implements Initializable {
    @FXML
    private Label welcomeLabel;

    @FXML
    private ComboBox<Laboratoire> labCombox;
    @FXML
    private Button buttonAddUser;
    private final Connection cnx;
    private final User user;

    private HashMap<Laboratoire, Role> lesLaboRole;

    public controllerTableau(Connection cnx, User user) {
        this.cnx = cnx;
        this.user = user;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcomeLabel.setText("Bienvenu "+user.getPrenom());


        try {
            lesLaboRole = new DAOUserInLab(cnx).getLesLabRole(user.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ObservableList<Laboratoire> lesLabos = FXCollections.observableArrayList(lesLaboRole.keySet());
        labCombox.setItems(lesLabos);

        labCombox.setConverter(new StringConverter<Laboratoire>() {
            @Override
            public String toString(Laboratoire laboratoire) {
                return laboratoire != null ? laboratoire.getNom(): "";
            }
            @Override
            public Laboratoire fromString(String s) {
                return null;
            }
        });

        labCombox.setValue(lesLabos.get(0));

        labCombox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                displayAddUser();
            }
        });

        if(Objects.equals(lesLaboRole.get(labCombox.getValue()).getId(), "role3")){
            buttonAddUser.setVisible(false);
        }

        buttonAddUser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                doAddUser();
            }
        });
    }

    private void displayAddUser() {
        buttonAddUser.setVisible(!Objects.equals(lesLaboRole.get(labCombox.getValue()).getId(), "role3"));
    }


    private void doAddUser(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ManageUser.fxml"));
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
