package chartreux.applilabv2.controllers;

import chartreux.applilabv2.DAO.DAOMedicament;
import chartreux.applilabv2.Entity.Ingredient;
import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Entity.Medicament;
import chartreux.applilabv2.Entity.User;
import chartreux.applilabv2.HelloApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class controllerTableauMedicament implements Initializable {

    @FXML
    private Button btnAddMedoc;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnSupLabo;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableColumn<Pair<Medicament,Integer>, String> columnForme;

    @FXML
    private TableColumn<Pair<Medicament,Integer>, String> columnIngredient;

    @FXML
    private TableColumn<Pair<Medicament,Integer>, String> columnMedoc;

    @FXML
    private TableColumn<Pair<Medicament,Integer>, String> columnQtt;

    @FXML
    private ComboBox<?> comboIngred;

    @FXML
    private ComboBox<Medicament> comboMissing;

    @FXML
    private TextField fieldForme;

    @FXML
    private Label labelLabo;

    @FXML
    private Label labelMedocChoose;

    @FXML
    private Spinner<?> spinQtt;

    @FXML
    private TableView<Pair<Medicament,Integer>> tableMedoc;

    @FXML
    private TextArea textDescrip;
    private Connection cnx;
    private Laboratoire laboratoire;
    private User user;

    public controllerTableauMedicament(Connection cnx, Laboratoire laboratoire, User user) {
        this.cnx = cnx;
        this.laboratoire = laboratoire;
        this.user = user;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelLabo.setText("Médicaments du laboratoire "+laboratoire.getNom());

        /*Set value to tableMedoc*/
        {
            columnMedoc.setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getKey().getNom()));
            columnForme.setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getKey().getForme()));
            columnIngredient.setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getKey().getIngredient().getNom()));
            columnQtt.setCellValueFactory(new PropertyValueFactory<>("value"));
            try {
                ObservableList<Pair<Medicament, Integer>> observableList = FXCollections.observableArrayList(new DAOMedicament(cnx).findMedicamentLab(laboratoire));

                tableMedoc.setItems(observableList);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        /*Set value missing medoc combobox*/
        try {
            ObservableList<Medicament> missingMedoc = FXCollections.observableArrayList(new DAOMedicament(cnx).missingMedicLab(laboratoire));
            comboMissing.setItems(missingMedoc);

            comboMissing.setConverter(new StringConverter<Medicament>() {
                @Override
                public String toString(Medicament medicament) {
                    return medicament != null ? medicament.getNom() : "";
                }

                @Override
                public Medicament fromString(String s) {
                    return null;
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        /*Back Home btn*/
        btnHome.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    doHome();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });



    }

    private void doHome() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Tableau.fxml"));
        controllerTableau controllerTableau = new controllerTableau(cnx,user);
        fxmlLoader.setController(controllerTableau);

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) btnHome.getScene().getWindow();
        stage.setTitle("Accueil");
        stage.setScene(scene);
        stage.centerOnScreen();
    }
}
