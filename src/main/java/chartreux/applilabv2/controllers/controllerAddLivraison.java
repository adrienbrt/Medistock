package chartreux.applilabv2.controllers;

import chartreux.applilabv2.DAO.DAOCommande;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    private DAOCommande daoCommande;

    public controllerAddLivraison(DAOCommande daoCommande) {
        this.daoCommande = daoCommande;
    }
}
