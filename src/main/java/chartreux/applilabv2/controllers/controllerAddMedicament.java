package chartreux.applilabv2.controllers;

import chartreux.applilabv2.DAO.DAOIngredient;
import chartreux.applilabv2.DAO.DAOMedicament;
import chartreux.applilabv2.Entity.Ingredient;
import chartreux.applilabv2.Entity.Medicament;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class controllerAddMedicament implements Initializable {
    @FXML
    private Button btnAdd;

    @FXML
    private Button btnCancel;

    @FXML
    private ComboBox<Ingredient> comboIng;

    @FXML
    private TextField fiedlNom;

    @FXML
    private TextField fieldForme;

    @FXML
    private TextArea textDesc;

    private DAOMedicament daoMedicament;
    private List<Ingredient> ingredientList;

    public controllerAddMedicament(DAOMedicament daoMedicament, List<Ingredient> ingredientList) {
        this.daoMedicament = daoMedicament;
        this.ingredientList = ingredientList;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        ObservableList<Ingredient> observableList = FXCollections.observableArrayList(ingredientList);
        comboIng.setItems(observableList);

        comboIng.setConverter(new StringConverter<Ingredient>() {
            @Override
            public String toString(Ingredient ingredient) {
                return ingredient != null ? ingredient.getNom() : "";
            }

            @Override
            public Ingredient fromString(String s) {
                return null;
            }
        });
        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                doCancel();
            }
        });

        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                doAdd();
            }
        });
    }

    private void doAdd() {
        if(comboIng.getValue() == null || fiedlNom.getText().isEmpty() || fieldForme.getText().isEmpty() || textDesc.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText("Nom manquant");
            alert.setContentText("le champs doit etre remplis");

            alert.showAndWait();
        }else{
            Medicament medicament = new Medicament("idNew", fiedlNom.getText(), fieldForme.getText(), textDesc.getText(), comboIng.getValue());

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Voulez-vous ajouter le médicament  "+ medicament.getNom()+" ?");
            alert.setContentText("Cliquez sur Oui ou sur Non.");

            // Définissez les boutons de la boîte de dialogue
            ButtonType buttonTypeYes = new ButtonType("Oui");
            ButtonType buttonTypeNo = new ButtonType("Non");
            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            // Affichez la boîte de dialogue et attendez la réponse
            alert.showAndWait().ifPresent(buttonType -> {

                if (buttonType == buttonTypeYes) {
                    try {
                        daoMedicament.createMedoc(medicament);
                        doCancel();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else if (buttonType == buttonTypeNo) {
                    alert.close();
                }
            });
        }
    }

    private void doCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }


}
