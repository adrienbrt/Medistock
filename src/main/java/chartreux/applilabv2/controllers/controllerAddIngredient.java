package chartreux.applilabv2.controllers;

import chartreux.applilabv2.DAO.DAOIngredient;
import chartreux.applilabv2.Entity.Laboratoire;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class controllerAddIngredient implements Initializable {
    @FXML
    private Button btnAdd;

    @FXML
    private Button btnCancel;

    @FXML
    private TextField fieldName;
    private Connection cnx;
    private Laboratoire laboratoire;

    public controllerAddIngredient(Connection cnx, Laboratoire laboratoire) {
        this.cnx = cnx;
        this.laboratoire = laboratoire;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                doClose();
            }
        });

        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    doAdd();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private void doClose() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void doAdd() throws SQLException {
        String nom = fieldName.getText();
        if(nom.isEmpty() || nom.isBlank()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText("Nom manquant");
            alert.setContentText("le champ doit etre remplis");

            alert.showAndWait();
        }else{
            new DAOIngredient(cnx).createIngredient(nom);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Valide");
            alert.setHeaderText("Ajout");
            alert.setContentText("l'ingredient "+nom+" a été ajouté");

            btnAdd.setDisable(true);
            alert.showAndWait();
            doClose();
        }
    }
}
