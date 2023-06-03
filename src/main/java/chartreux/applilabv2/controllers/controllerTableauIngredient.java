package chartreux.applilabv2.controllers;

import chartreux.applilabv2.DAO.DAOIngredient;
import chartreux.applilabv2.Entity.Ingredient;
import chartreux.applilabv2.Entity.Laboratoire;
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
    private TableColumn<Pair<Ingredient,Integer>, String> columnIngredient;

    @FXML
    private TableColumn<Pair<Ingredient,Integer>, String> columnQtt;

    @FXML
    private Spinner<Integer> fieldQtt;

    @FXML
    private Label labelLab;

    @FXML
    private Label labelSelectIngredient;

    @FXML
    private TableView<Pair<Ingredient,Integer>> tableIngreQtt;

    private Connection cnx;
    private User user;
    private Laboratoire laboratoire;

    private List<Pair<Ingredient,Integer>> listPair;

    public controllerTableauIngredient(Connection cnx, User user, Laboratoire laboratoire) {
        this.cnx = cnx;
        this.user = user;
        this.laboratoire = laboratoire;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelLab.setText("Ingredient du laboratoire "+laboratoire.getNom());
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,1000,0);
        fieldQtt.setValueFactory(valueFactory);

        /*set missing ingredient*/
        try {
            ObservableList<Ingredient> missingIng = FXCollections.observableList(new DAOIngredient(cnx).missingIngredientLab(laboratoire));
            comboMissing.setItems(missingIng);

            comboMissing.setConverter(new StringConverter<Ingredient>() {
                @Override
                public String toString(Ingredient ingredient) {
                    return ingredient != null ? ingredient.getNom() : "";

                }

                @Override
                public Ingredient fromString(String s) {
                    return null;
                }
            });

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        /*set btn back home*/
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

        /*set btn add Ingrient*/
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

        /*add ingredient/qtt to table*/
        {
            List<Pair<Ingredient, Integer>> listPair = null;
            try {
                listPair = new DAOIngredient(cnx).findIngredientLab(laboratoire);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


            ObservableList<Pair<Ingredient, Integer>> ObservableListe = FXCollections.observableArrayList(listPair);

            columnIngredient.setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getKey().getNom()));
            columnQtt.setCellValueFactory(new PropertyValueFactory<>("value"));

            tableIngreQtt.setItems(ObservableListe);
        }

        tableIngreQtt.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                doSelectedItem(mouseEvent);
            }
        });

        buttonDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                doDeleteSelect();
            }
        });

        buttonUpdate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                doUpdate();
            }
        });

        comboMissing.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if(comboMissing.getValue()!=null ){
                    labelSelectIngredient.setText("Ingredients :"+comboMissing.getValue().getNom());
                    buttonUpdate.setText("Ajouté ingrédient");
                }
            }
        });

    }

    private void doUpdate() {
        if(comboMissing.getValue() == null && !tableIngreQtt.getSelectionModel().getSelectedIndices().isEmpty()){
           Pair<Ingredient,Integer> pair = tableIngreQtt.getSelectionModel().getSelectedItem();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Voulez-vous appliquer la modification à "+ pair.getKey().getNom()+" ?");
            alert.setContentText("Cliquez sur Oui ou sur Non.");

            // Définissez les boutons de la boîte de dialogue
            ButtonType buttonTypeYes = new ButtonType("Oui");
            ButtonType buttonTypeNo = new ButtonType("Non");
            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            // Affichez la boîte de dialogue et attendez la réponse
            alert.showAndWait().ifPresent(buttonType -> {

                if (buttonType == buttonTypeYes) {
                    try {
                        new DAOIngredient(cnx).updateIngredientLab(pair.getKey(),laboratoire, fieldQtt.getValue());
                        List<Pair<Ingredient, Integer>> updatedList = new DAOIngredient(cnx).findIngredientLab(laboratoire);
                        ObservableList<Pair<Ingredient, Integer>> updatedObservableList = FXCollections.observableArrayList(updatedList);
                        tableIngreQtt.setItems(updatedObservableList);

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else if (buttonType == buttonTypeNo) {
                    alert.close();
                }
            });
        }else if(!comboMissing.isDisable() && comboMissing.getValue() != null){
            Pair<Ingredient, Integer> pair = new Pair<>(comboMissing.getValue(),fieldQtt.getValue());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Voulez-vous ajouter "+ pair.getKey().getNom()+" ?");
            alert.setContentText("Cliquez sur Oui ou sur Non.");

            // Définissez les boutons de la boîte de dialogue
            ButtonType buttonTypeYes = new ButtonType("Oui");
            ButtonType buttonTypeNo = new ButtonType("Non");
            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            // Affichez la boîte de dialogue et attendez la réponse
            alert.showAndWait().ifPresent(buttonType -> {

                if (buttonType == buttonTypeYes) {
                    try {
                        new DAOIngredient(cnx).addIngredientFromLab(pair.getKey(),laboratoire, pair.getValue());
                        List<Pair<Ingredient, Integer>> updatedList = new DAOIngredient(cnx).findIngredientLab(laboratoire);
                        ObservableList<Pair<Ingredient, Integer>> updatedObservableList = FXCollections.observableArrayList(updatedList);
                        tableIngreQtt.setItems(updatedObservableList);

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else if (buttonType == buttonTypeNo) {
                    alert.close();
                }
            });
        }
    }

    private void doDeleteSelect() {
        Pair<Ingredient,Integer> pair = tableIngreQtt.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Voulez-vous supprimer l'ingrédient "+ pair.getKey().getNom()+" ?");
        alert.setContentText("Cliquez sur Oui ou sur Non.");

        // Définissez les boutons de la boîte de dialogue
        ButtonType buttonTypeYes = new ButtonType("Oui");
        ButtonType buttonTypeNo = new ButtonType("Non");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        // Affichez la boîte de dialogue et attendez la réponse
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == buttonTypeYes) {
                try {
                    new DAOIngredient(cnx).deleteIngredientFromLab(pair.getKey(),laboratoire);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                ObservableList<Pair<Ingredient, Integer>> list = tableIngreQtt.getItems();
                list.remove(pair);
                tableIngreQtt.setItems(list);

            } else if (buttonType == buttonTypeNo) {
                alert.close();
            }
        });
    }

    private void doSelectedItem(MouseEvent mouseEvent) {
        Pair<Ingredient,Integer> pair = tableIngreQtt.getSelectionModel().getSelectedItem();
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
            tableIngreQtt.getSelectionModel().clearSelection();
            comboMissing.setDisable(false);
            comboMissing.setValue(null);
            comboMissing.setPromptText("ingredient manquant");
            labelSelectIngredient.setText("Ingredients :");
            fieldQtt.getValueFactory().setValue(0);
        } else {
            comboMissing.setDisable(true);
            comboMissing.setValue(null);
            comboMissing.setPromptText("ingredient manquant");
            labelSelectIngredient.setText("Ingredients :"+pair.getKey().getNom());
            fieldQtt.getValueFactory().setValue(pair.getValue());
            buttonUpdate.setText("Mettre à jour");
        }
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
