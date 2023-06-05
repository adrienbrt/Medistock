package chartreux.applilabv2.controllers;

import chartreux.applilabv2.DAO.DAOCommande;
import chartreux.applilabv2.DAO.DAOIngredient;
import chartreux.applilabv2.Entity.Commande;
import chartreux.applilabv2.Entity.Etat;
import chartreux.applilabv2.Entity.Ingredient;
import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Util.Tool;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class controllerAddLivraison implements Initializable {
    @FXML
    private Button btnAdd;

    @FXML
    private Button btnAddIng;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSupIng;

    @FXML
    private TableColumn<Pair<Ingredient,Integer>, String> columnIng;

    @FXML
    private TableColumn<Pair<Ingredient,Integer>, String> columnQtt;

    @FXML
    private ComboBox<Ingredient> comboIng;

    @FXML
    private Spinner<Integer> spinQtt;

    @FXML
    private TableView<Pair<Ingredient,Integer>> tableDetail;

    private DAOIngredient daoIngredient;
    private DAOCommande daoCommande;
    private Laboratoire laboratoire;

    private List<Pair<Ingredient, Integer>> pairList;

    public controllerAddLivraison(DAOCommande daoCommande, Connection cnx, Laboratoire laboratoire) {
        this.daoCommande = daoCommande;
        this.daoIngredient = new DAOIngredient(cnx);
        this.laboratoire = laboratoire;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        pairList = new ArrayList<>();
        btnSupIng.setDisable(true);

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,1000,0);
        spinQtt.setValueFactory(valueFactory);

        columnIng.setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getKey().getNom()));
        columnQtt.setCellValueFactory(new PropertyValueFactory<>("value"));

        try {
            ObservableList<Ingredient> observableList = FXCollections.observableArrayList(daoIngredient.findAll());
            comboIng.setItems(observableList);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        comboIng.setConverter(new StringConverter<Ingredient>() {
            @Override
            public String toString(Ingredient ingredient) {
                return ingredient != null ?ingredient.getNom() : "";
            }

            @Override
            public Ingredient fromString(String s) {
                return null;
            }
        });
        tableDetail.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                doSelectedItem(mouseEvent);
            }
        });

        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                doCancel();
            }
        });

        btnAddIng.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                doAddIng();
            }
        });


        btnSupIng.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                doSuppIng();
            }
        });


        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    doAdd();
                } catch (SQLException | ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void doSelectedItem(MouseEvent mouseEvent) {
        Pair<Ingredient, Integer> pair = tableDetail.getSelectionModel().getSelectedItem();
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            comboIng.setValue(null);
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,1000,0);
            spinQtt.setValueFactory(valueFactory);
            btnAddIng.setText("Ajouter ingrédient");

        } else {
            btnAddIng.setText("Mettre a jours");
            comboIng.setValue(pair.getKey());
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,1000,pair.getValue());
            spinQtt.setValueFactory(valueFactory);
            btnSupIng.setDisable(false);
        }
    }

    private void doAddIng() {
        if(comboIng.getValue() == null || spinQtt.getValue() == 0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText("Champ manquant");
            alert.setContentText("les champs doit etre remplis");

            alert.showAndWait();
        }else{
            if(Tool.checkKeyExists(pairList, comboIng.getValue())){
                for (int i = 0; i < pairList.size(); i++) {
                    Pair<Ingredient, Integer> pair = pairList.get(i);
                    if (pair.getKey().equals(comboIng.getValue())) {
                        pairList.set(i, new Pair<>(pair.getKey(), spinQtt.getValue())); // Met à jour la paire avec la nouvelle valeur
                    }
                }
            }else {
                pairList.add(new Pair<>(comboIng.getValue(),spinQtt.getValue()));
            }
            ObservableList<Pair<Ingredient,Integer>> observableList = FXCollections.observableArrayList(pairList);
            tableDetail.setItems(observableList);
        }
    }

    private void doCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void doSuppIng(){
        Pair<Ingredient, Integer> pair = tableDetail.getSelectionModel().getSelectedItem();
        if(pair == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText("Champ manquant");
            alert.setContentText("aucun élément selectionné");

            alert.showAndWait();
        }else{
            for (int i = 0; i < pairList.size(); i++) {
                Pair<Ingredient, Integer> pairTemp = pairList.get(i);
                if (pairTemp.getKey().equals(pair.getKey())) {
                    pairList.remove(i); // Supprime l'élément correspondant de la liste
                    break;
                }
            }
            ObservableList<Pair<Ingredient,Integer>> observableList = FXCollections.observableArrayList(pairList);
            tableDetail.setItems(observableList);
        }
    }

    private void doAdd() throws SQLException, ParseException {
        if(pairList.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText("Champ manquant");
            alert.setContentText("aucun élément dans la commande");

            alert.showAndWait();
        }else{
            Etat etat = new Etat("etat1","En attente");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.now();
            String formattedDate = date.format(formatter);
            Commande commande = new Commande("idNew", laboratoire, date, etat, pairList);
            daoCommande.create(commande);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Ajout");
            alert.setContentText("Commande ajouté");

            alert.showAndWait();

            doCancel();
        }
    }

}
