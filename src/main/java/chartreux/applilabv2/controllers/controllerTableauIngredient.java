package chartreux.applilabv2.controllers;

import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Entity.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class controllerTableauIngredient implements Initializable {


    @FXML
    private ComboBox<?> ComboMissing;

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

    }
}
