package chartreux.applilabv2.controllers;

import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Entity.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;

public class controllerTableauLivraison {
    @FXML
    private Button btnHome;

    @FXML
    private Button btnModifEtat;

    @FXML
    private Button btnNewCmd;

    @FXML
    private TableColumn<?, ?> columnDate;

    @FXML
    private TableColumn<?, ?> columnEtat;

    @FXML
    private TableColumn<?, ?> columnIng;

    @FXML
    private TableColumn<?, ?> columnNbIng;

    @FXML
    private TableColumn<?, ?> columnQtt;

    @FXML
    private ComboBox<?> comboEtat;

    @FXML
    private Label labelDate;

    @FXML
    private Label labelLabo;

    @FXML
    private TableView<?> tableCmd;

    @FXML
    private TableView<?> tableDetail;

    private Connection cnx;
    private Laboratoire laboratoire;
    private User user;

    public controllerTableauLivraison(Connection cnx, Laboratoire laboratoire, User user) {
        this.cnx = cnx;
        this.laboratoire = laboratoire;
        this.user = user;
    }
}
