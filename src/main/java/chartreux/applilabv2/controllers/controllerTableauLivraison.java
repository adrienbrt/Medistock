package chartreux.applilabv2.controllers;

import chartreux.applilabv2.DAO.DAOCommande;
import chartreux.applilabv2.DAO.DAOEtat;
import chartreux.applilabv2.Entity.*;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class controllerTableauLivraison implements Initializable {
    @FXML
    private Button btnHome;

    @FXML
    private Button btnModifEtat;

    @FXML
    private Button btnNewCmd;

    @FXML
    private TableColumn<Pair<Commande, Integer>, String> columnDate;

    @FXML
    private TableColumn<Pair<Commande, Integer>, String> columnEtat;

    @FXML
    private TableColumn<Pair<Ingredient, Integer>, String> columnIng;

    @FXML
    private TableColumn<Pair<Commande, Integer>, String> columnNbIng;

    @FXML
    private TableColumn<Pair<Ingredient, Integer>, String> columnQtt;

    @FXML
    private ComboBox<Etat> comboEtat;

    @FXML
    private Label labelDate;

    @FXML
    private Label labelLabo;

    @FXML
    private TableView<Pair<Commande, Integer>> tableCmd;

    @FXML
    private TableView<Pair<Ingredient, Integer>> tableDetail;

    private Connection cnx;
    private Laboratoire laboratoire;
    private User user;
    private DAOCommande daoCommande;


    public controllerTableauLivraison(Connection cnx, Laboratoire laboratoire, User user) {
        this.cnx = cnx;
        this.laboratoire = laboratoire;
        this.user = user;
        this.daoCommande = new DAOCommande(cnx);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelLabo.setText("Commande du laboratoire "+laboratoire.getNom());

        btnModifEtat.setDisable(true);

        columnDate.setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getKey().getDate().toString()));
        columnEtat.setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getKey().getEtat().getLibelle()));
        columnNbIng.setCellValueFactory(new PropertyValueFactory<>("value"));
        columnIng.setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getKey().getNom()));
        columnQtt.setCellValueFactory(new PropertyValueFactory<>("value"));

        try {
            ObservableList<Pair<Commande, Integer>> observableListCmd = FXCollections.observableArrayList(daoCommande.findAllCommandeLabNbIng(laboratoire));
            tableCmd.setItems(observableListCmd);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            ObservableList<Etat> observableListEtat = FXCollections.observableArrayList(new DAOEtat(cnx).findAll());
            comboEtat.setItems(observableListEtat);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        comboEtat.setConverter(new StringConverter<Etat>() {
            @Override
            public String toString(Etat etat) {
                return etat != null ? etat.getLibelle() : "";
            }

            @Override
            public Etat fromString(String s) {
                return null;
            }
        });

        tableCmd.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    doSelectCommande(mouseEvent);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

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

        btnNewCmd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    doAddCmd();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnModifEtat.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    doModifEtat();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void doModifEtat() throws SQLException {
        if(comboEtat.getValue()!=null){
            Etat etat = comboEtat.getValue();
            Commande commande = tableCmd.getSelectionModel().getSelectedItem().getKey();
            commande.setEtat(etat);
            daoCommande.update(commande);
            ObservableList<Pair<Commande, Integer>> observableListCmd = FXCollections.observableArrayList(daoCommande.findAllCommandeLabNbIng(laboratoire));
            tableCmd.setItems(observableListCmd);
        }
    }

    private void doAddCmd() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addLivraison.fxml"));
        controllerAddLivraison controllerAddLivraison = new controllerAddLivraison(daoCommande,cnx,laboratoire);
        fxmlLoader.setController(controllerAddLivraison);

        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("ajouter commande");
        stage.setScene(new Scene(root));
        stage.show();

        stage.setOnHidden(event -> {
            try {
                ObservableList<Pair<Commande, Integer>> observableListCmd = FXCollections.observableArrayList(daoCommande.findAllCommandeLabNbIng(laboratoire));
                tableCmd.setItems(observableListCmd);

            } catch (SQLException e) {
                throw new RuntimeException(e);
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

    private void doSelectCommande(MouseEvent mouseEvent) throws SQLException {
        Pair<Commande, Integer> pair = tableCmd.getSelectionModel().getSelectedItem();
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            tableDetail.setItems(null);
            labelDate.setText("");
            comboEtat.setValue(null);
        }else{
            btnModifEtat.setDisable(false);
            ObservableList<Pair<Ingredient, Integer>> pairIng = FXCollections.observableArrayList(daoCommande.findAllIngCommand(pair.getKey()));
            tableDetail.setItems(pairIng);
            labelDate.setText(pair.getKey().getDate().toString());
            comboEtat.setValue(pair.getKey().getEtat());
        }
    }

}
