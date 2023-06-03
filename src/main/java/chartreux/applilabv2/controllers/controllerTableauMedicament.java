package chartreux.applilabv2.controllers;

import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Entity.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Connection;
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
    private TableColumn<?, ?> columnForme;

    @FXML
    private TableColumn<?, ?> columnIngredient;

    @FXML
    private TableColumn<?, ?> columnMedoc;

    @FXML
    private TableColumn<?, ?> columnQtt;

    @FXML
    private ComboBox<?> comboIngred;

    @FXML
    private ComboBox<?> comboMissing;

    @FXML
    private TextField fieldForme;

    @FXML
    private Label labelLabo;

    @FXML
    private Label labelMedocChoose;

    @FXML
    private Spinner<?> spinQtt;

    @FXML
    private TableView<?> tableMedoc;

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

    }
}
