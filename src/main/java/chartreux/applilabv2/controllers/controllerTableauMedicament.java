package chartreux.applilabv2.controllers;

import chartreux.applilabv2.DAO.DAOIngredient;
import chartreux.applilabv2.DAO.DAOMedicament;
import chartreux.applilabv2.DAO.DAOUser;
import chartreux.applilabv2.Entity.Role;
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
import java.util.Objects;
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
    private ComboBox<Ingredient> comboIngred;

    @FXML
    private ComboBox<Medicament> comboMissing;

    @FXML
    private TextField fieldForme;

    @FXML
    private Label labelLabo;

    @FXML
    private Label labelMedocChoose;

    @FXML
    private Spinner<Integer> spinQtt;

    @FXML
    private TableView<Pair<Medicament,Integer>> tableMedoc;

    @FXML
    private TextArea textDescrip;
    private final Connection cnx;
    private final Laboratoire laboratoire;
    private final User user;
    private final DAOIngredient daoIngredient;
    private final DAOMedicament daoMedicament;
    private Role role;

    public controllerTableauMedicament(Connection cnx, Laboratoire laboratoire, User user) {
        this.cnx = cnx;
        this.laboratoire = laboratoire;
        this.user = user;
        this.daoIngredient = new DAOIngredient(cnx);
        this.daoMedicament = new DAOMedicament(cnx);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        labelLabo.setText("Médicaments du laboratoire "+laboratoire.getNom());
        btnSupLabo.setDisable(true);
        btnUpdate.setDisable(true);


        /*recupération du role de l'utilisateur*/
        try {
            this.role = new DAOUser(cnx).userGetRoleLab(user,laboratoire);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        /*ajustement en fonction du role*/
        if(Objects.equals(role.getId(), "role3")){
            fieldForme.setDisable(true);
            textDescrip.setDisable(true);
            textDescrip.setDisable(true);
            comboIngred.setDisable(true);
            btnAddMedoc.setVisible(false);
        }

        /*Set value to tableMedoc*/
        {
            columnMedoc.setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getKey().getNom()));
            columnForme.setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getKey().getForme()));
            columnIngredient.setCellValueFactory(celldata -> new SimpleStringProperty(celldata.getValue().getKey().getIngredient().getNom()));
            columnQtt.setCellValueFactory(new PropertyValueFactory<>("value"));
            try {
                ObservableList<Pair<Medicament, Integer>> observableList = FXCollections.observableArrayList(daoMedicament.findMedicamentLab(laboratoire));

                tableMedoc.setItems(observableList);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        /*Set value missing medoc combobox*/
        try {
            ObservableList<Medicament> missingMedoc = FXCollections.observableArrayList(daoMedicament.missingMedicLab(laboratoire));
            missingMedoc.add(null);
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

        /*Set ingredient combobox*/
        try {
            ObservableList<Ingredient> listMedicament = FXCollections.observableArrayList(daoIngredient.findAll());
            comboIngred.setItems(listMedicament);
            comboIngred.setConverter(new StringConverter<Ingredient>() {
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

        if (Objects.equals(role.getId(), "role3")){
            btnSupLabo.setVisible(false);
        }

        /*Valeur du spin */
        spinQtt.setDisable(true);
        comboIngred.valueProperty().addListener((observable, oldValue, newValue)->{
            if (newValue == null) {
                spinQtt.setDisable(true);
            } else {
                spinQtt.setDisable(false);
                if(tableMedoc.getSelectionModel().isEmpty()){
                    btnUpdate.setText("Ajouter médicament au labo");
                    btnUpdate.setDisable(false);
                    try {
                        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,daoIngredient.getQttOneIngredientLab(newValue,laboratoire),0);
                        spinQtt.setValueFactory(valueFactory);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    btnUpdate.setText("Mettre a jour");
                    btnUpdate.setDisable(false);
                    try {
                        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0,daoIngredient.getQttOneIngredientLab(newValue,laboratoire)+tableMedoc.getSelectionModel().getSelectedItem().getValue(),tableMedoc.getSelectionModel().getSelectedItem().getValue());
                        spinQtt.setValueFactory(valueFactory);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

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

        tableMedoc.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                doSelectItem(mouseEvent);
            }
        });

        /*Ajout de nouveau medicament*/
        btnAddMedoc.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    doAddMedoc();
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnUpdate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    doUpdate();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnSupLabo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                doDeleteFromLab();
            }
        });

        /*Listenner combo missing*/
        comboMissing.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                doComboMissing();
            }
        });
    }

    private void doDeleteFromLab() {
        Pair<Medicament,Integer> medicamentIntegerPair = tableMedoc.getSelectionModel().getSelectedItem();
        if (medicamentIntegerPair == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Attention");
            alert.setHeaderText("Medicament manquant");
            alert.setContentText("Veuillez choisir un médicament");

            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Voulez-vous supprimer le médicament "+ medicamentIntegerPair.getKey().getNom()+" du laboratoire ?");
            alert.setContentText("Cliquez sur Oui ou sur Non.");

            // Définissez les boutons de la boîte de dialogue
            ButtonType buttonTypeYes = new ButtonType("Oui");
            ButtonType buttonTypeNo = new ButtonType("Non");
            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

            // Affichez la boîte de dialogue et attendez la réponse
            alert.showAndWait().ifPresent(buttonType -> {

                if (buttonType == buttonTypeYes) {
                    try {
                        daoMedicament.deleteMedocLab(medicamentIntegerPair.getKey(),laboratoire);
                        List<Pair<Medicament, Integer>> updatedList = daoMedicament.findMedicamentLab(laboratoire);
                        ObservableList<Pair<Medicament, Integer>> updatedObservableList = FXCollections.observableArrayList(updatedList);
                        tableMedoc.setItems(updatedObservableList);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                } else if (buttonType == buttonTypeNo) {
                    alert.close();
                }
            });
        }
    }

    private void doComboMissing() {
        if(comboMissing.getValue()!= null){
            btnSupLabo.setDisable(true);
            labelMedocChoose.setText(comboMissing.getValue().getNom());
            fieldForme.setText(comboMissing.getValue().getForme());
            textDescrip.setText(comboMissing.getValue().getDescription());
            comboIngred.setValue(comboMissing.getValue().getIngredient());
            btnUpdate.setDisable(false);
        }else{
            btnUpdate.setDisable(true);
            btnSupLabo.setDisable(true);
            labelMedocChoose.setText("");
            fieldForme.setText("");
            textDescrip.setText("");
            comboIngred.setValue(null);
        }
    }

    private void doUpdate() throws SQLException {
        Pair<Medicament,Integer> pair = tableMedoc.getSelectionModel().getSelectedItem();

        //cas ou on applique les modif a un medoc qui existe deja
        if(pair != null){
            //si utilisateur est un simple user
            if(Objects.equals(role.getId(), "role3")){
                try {
                    new DAOIngredient(cnx).updateIngredientValue( spinQtt.getValue() -pair.getValue() ,pair.getKey().getIngredient(),laboratoire);
                    daoMedicament.updateMedocLab(pair.getKey(),laboratoire,spinQtt.getValue());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            //si utilisateur a du pouvoir
            }else{
                try {
                    new DAOIngredient(cnx).updateIngredientValue(spinQtt.getValue() - pair.getValue(),pair.getKey().getIngredient(),laboratoire);
                    daoMedicament.updateMedocLab(pair.getKey(),laboratoire,spinQtt.getValue());
                    Medicament newMedicament = new Medicament(pair.getKey().getId(),pair.getKey().getNom(),fieldForme.getText(),textDescrip.getText(),comboIngred.getValue());
                    daoMedicament.updateMedoc(newMedicament);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        //cas ou le medoc n'est pas encore dans le tableau
        }else{
            if(Objects.equals(role.getId(), "role3")){
                try {
                    new DAOIngredient(cnx).updateIngredientValue(spinQtt.getValue(),comboMissing.getValue().getIngredient(),laboratoire);
                    daoMedicament.addMedocLab(comboMissing.getValue(),laboratoire,spinQtt.getValue());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }else{
                try {
                    new DAOIngredient(cnx).updateIngredientValue(spinQtt.getValue(),comboMissing.getValue().getIngredient(),laboratoire);
                    daoMedicament.addMedocLab(comboMissing.getValue(),laboratoire,spinQtt.getValue());
                    Medicament newMedicament = new Medicament(comboMissing.getValue().getId(),comboMissing.getValue().getNom(),fieldForme.getText(),textDescrip.getText(),comboIngred.getValue());
                    daoMedicament.updateMedoc(newMedicament);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        List<Pair<Medicament, Integer>> updatedList = daoMedicament.findMedicamentLab(laboratoire);
        ObservableList<Pair<Medicament, Integer>> updatedObservableList = FXCollections.observableArrayList(updatedList);
        tableMedoc.setItems(updatedObservableList);

    }

    private void doAddMedoc() throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addMedicament.fxml"));
        controllerAddMedicament controllerAddMedicament = new controllerAddMedicament(daoMedicament,daoIngredient.findAll());
        fxmlLoader.setController(controllerAddMedicament);

        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setTitle("ajouter médicament");
        stage.setScene(new Scene(root));
        stage.show();

        stage.setOnHidden(event->{
            ObservableList<Medicament> missingMedoc = null;
            try {
                missingMedoc = FXCollections.observableArrayList(daoMedicament.missingMedicLab(laboratoire));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            missingMedoc.add(null);
            comboMissing.setItems(missingMedoc);
        });

    }

    private void doSelectItem(MouseEvent mouseEvent) {
        Pair<Medicament,Integer> pair = tableMedoc.getSelectionModel().getSelectedItem();
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2){
            tableMedoc.getSelectionModel().clearSelection();
            labelMedocChoose.setText("");
            fieldForme.setText("");
            textDescrip.setText("");
            comboIngred.setValue(null);
            comboMissing.setValue(null);
            comboMissing.setDisable(false);
            btnUpdate.setDisable(true);
            btnSupLabo.setDisable(true);
        }else{
            btnSupLabo.setDisable(false);
            btnUpdate.setDisable(false);
            comboMissing.setDisable(true);
            labelMedocChoose.setText(pair.getKey().getNom());
            fieldForme.setText(pair.getKey().getForme());
            textDescrip.setText(pair.getKey().getDescription());
            comboIngred.setValue(pair.getKey().getIngredient());

        }
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
