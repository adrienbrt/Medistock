package chartreux.applilabv2.controllers;

import chartreux.applilabv2.DAO.DAOIngredient;
import chartreux.applilabv2.Entity.Ingredient;
import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Entity.User;
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
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class controllerTableauIngredient implements Initializable {


    @FXML
    private ComboBox<Ingredient> comboMissing;

    @FXML
    private Button btnAccueil;

    @FXML
    private Button btnAddIngredient;

    @FXML
    private Button buttonDelete;

    @FXML
    private Button buttonLess;

    @FXML
    private Button buttonMore;

    @FXML
    private Button buttonUpdate;

    @FXML
    private TableColumn<?, ?> columnIngredient;

    @FXML
    private TableColumn<?, ?> columnQtt;

    @FXML
    private TextField fieldQtt;

    @FXML
    private Label labelLab;

    @FXML
    private Label labelSelectIngredient;

    @FXML
    private TableView<?> tableIngreQtt;

    private Connection cnx;
    private User user;
    private Laboratoire laboratoire;

    public controllerTableauIngredient(Connection cnx, User user, Laboratoire laboratoire) {
        this.cnx = cnx;
        this.user = user;
        this.laboratoire = laboratoire;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelLab.setText("Ingredient du laboratoire "+laboratoire.getNom());

        try {
            ObservableList<Ingredient> missingIng = FXCollections.observableList(new DAOIngredient(cnx).missingIngredientLab(laboratoire));
            comboMissing.setItems(missingIng);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        btnAccueil.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    backHome();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnAddIngredient.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    doAddIngredient();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }

    private void doAddIngredient() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addIngredient.fxml"));
        controllerAddIngredient controllerAddIngredients = new controllerAddIngredient(cnx,laboratoire);
        fxmlLoader.setController(controllerAddIngredients);

        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("ajouter ingredients");
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void backHome() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Tableau.fxml"));
        controllerTableau controllerTableau = new controllerTableau(cnx,user);
        fxmlLoader.setController(controllerTableau);

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) btnAccueil.getScene().getWindow();
        stage.setTitle("Accueil");
        stage.setScene(scene);
        stage.centerOnScreen();
    }


}
