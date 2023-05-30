package chartreux.applilabv2.controllers;

import chartreux.applilabv2.DAO.DAOLaboratoire;
import chartreux.applilabv2.DAO.DAORole;
import chartreux.applilabv2.DAO.DAOUser;
import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Entity.Role;
import chartreux.applilabv2.Entity.User;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

public class controllerManageUser implements Initializable {

    @FXML
    private Button ButtonAdd;
    @FXML
    private TextField fieldLogin;

    @FXML
    private TextField fieldNom;

    @FXML
    private PasswordField fieldPassword;

    @FXML
    private TextField fieldPrenom;

    @FXML
    private ComboBox<Role> comboRole;

    @FXML
    private ComboBox<Laboratoire> comboLabo;

    @FXML
    private TableColumn<User, String> loginColumn;

    @FXML
    private TableColumn<User, String> nomColumn;

    @FXML
    private TableColumn<User, String> prenomColumn;

    @FXML
    private TextField searchBar;

    @FXML
    private TableView<User> tableView;
    private Connection cnx;
    private User user;

    public controllerManageUser(Connection cnx, User user) {
        this.cnx = cnx;
        this.user = user;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            new DAOUser(cnx).Create();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        /* ajoute utilisteur dans tableau */
        try {
            ObservableList<User> users = FXCollections.observableList(new DAOUser(cnx).findAll(0,50));

            loginColumn.setCellValueFactory(new PropertyValueFactory<User,String>("login"));
            nomColumn.setCellValueFactory(new PropertyValueFactory<User,String>("nom"));
            prenomColumn.setCellValueFactory(new PropertyValueFactory<User,String >("prenom"));

            tableView.setItems(users);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        /*set values combobox labo*/
        try {
            ObservableList<Laboratoire> listLaboratoire = FXCollections.observableList(new DAOLaboratoire(cnx).findAll());
            comboLabo.setItems(listLaboratoire);

            comboLabo.setConverter(new StringConverter<Laboratoire>() {
                @Override
                public String toString(Laboratoire laboratoire) {
                    return laboratoire != null ? laboratoire.getNom() : "";
                }

                @Override
                public Laboratoire fromString(String s) {
                    return null;
                }

            });


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        /*set values combobox role*/
        try {
            ObservableList<Role> listRole = FXCollections.observableList(new DAORole(cnx).findAll());
            comboRole.setItems(listRole);

            comboRole.setConverter(new StringConverter<Role>() {
                @Override
                public String toString(Role role) {
                    return role != null ? role.getLibelle() : "";
                }

                @Override
                public Role fromString(String s) {
                    return null;
                }

            });


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        /*action combo role*/
        comboRole.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                setComboRole();
            }
        });

        /*action combo Labo*/
        comboLabo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                setComboLabo();
            }
        });

        ButtonAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                clickAdd();
            }
        });
    }

    private void clickAdd() {
        if (
                fieldLogin.getText().isEmpty() ||
                fieldPassword.getText().isEmpty() ||
                fieldNom.getText().isEmpty() || fieldPrenom.getText().isEmpty() || comboRole.getValue() == null
        ){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur");
            alert.setHeaderText("Problème d'ajout");
            alert.setContentText("Un champs est manquant");

            alert.showAndWait();
        }
    }

    private void setComboRole() {
        Role selectedRole = comboRole.getValue();
        assert selectedRole != null;
        if (selectedRole.getId().equals("role1")){
            comboLabo.setDisable(true);
            comboLabo.setValue(null);
        }else {
            comboLabo.setDisable(false);
        }
    }

    private void setComboLabo() {
        Laboratoire selectedLabo = comboLabo.getValue();
        if (selectedLabo != null) {

            String nom = selectedLabo.getNom();
            // Utilisez le libellé sélectionné comme nécessaire
            System.out.println("Labo sélectionné : " + nom);
        }
    }



}
