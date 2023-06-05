package chartreux.applilabv2.controllers;

import chartreux.applilabv2.DAO.DAOCommande;
import chartreux.applilabv2.DAO.DAOIngredient;
import chartreux.applilabv2.Entity.Laboratoire;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;

public class controllerAddLivraison {
    @FXML
    private Button btnAdd;

    @FXML
    private Button btnAddIng;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSupIng;

    @FXML
    private TableColumn<?, ?> columnIng;

    @FXML
    private TableColumn<?, ?> columnQtt;

    @FXML
    private ComboBox<?> comboIng;

    @FXML
    private Spinner<?> spinQtt;

    @FXML
    private TableView<?> tableDetail;

    private DAOIngredient daoIngredient;
    private DAOCommande daoCommande;
    private Laboratoire laboratoire;

    public controllerAddLivraison(DAOCommande daoCommande, Connection cnx, Laboratoire laboratoire) {
        this.daoCommande = daoCommande;
        this.daoIngredient = new DAOIngredient(cnx);
        this.laboratoire = laboratoire;
    }
}
