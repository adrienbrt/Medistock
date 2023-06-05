package chartreux.applilabv2.controllers;

import chartreux.applilabv2.DAO.DAOUser;
import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Entity.Role;
import chartreux.applilabv2.Entity.User;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import chartreux.applilabv2.HelloApplication;
import chartreux.applilabv2.Util.Tool;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.StringConverter;

public class controllerTableau implements Initializable {
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button buttonCommande;
    @FXML
    private Button buttonIngredient;
    @FXML
    private Button buttonMedoc;
    @FXML
    private ComboBox<Laboratoire> labCombox;
    @FXML
    private Button buttonAddUser;
    private final Connection cnx;
    private final User user;
    private List<Pair<Laboratoire, Role>> lesLaboRole;

    public controllerTableau(Connection cnx, User user) {
        this.cnx = cnx;
        this.user = user;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lesLaboRole =new ArrayList<>();

        welcomeLabel.setText("Bienvenue "+user.getPrenom());

        try {
            lesLaboRole = new DAOUser(cnx).getLesLaboRole(user.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ObservableList<Laboratoire> lesLabos = FXCollections.observableArrayList();
        for (Pair<Laboratoire,Role> leLaboRole: lesLaboRole){
            lesLabos.add(leLaboRole.getKey());
        }
        labCombox.setItems(lesLabos);

        labCombox.setValue(lesLabos.get(0));

        labCombox.setConverter(new StringConverter<Laboratoire>() {
            @Override
            public String toString(Laboratoire laboratoire) {
                return laboratoire != null ? laboratoire.getNom(): "";
            }
            @Override
            public Laboratoire fromString(String s) {
                return null;
            }
        });

        labCombox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                displayAddUser();
            }
        });

        buttonAddUser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                doAddUser();
            }
        });
        
        buttonIngredient.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    openIngredientTable();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        buttonMedoc.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    openMedicamentTable();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        
        buttonCommande.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    openCommandeTable();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }
    /**
     * Ouverture de la page des commandes)
     */
    private void openCommandeTable() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("TableauLivraison.fxml"));
        controllerTableauLivraison controllerTableauLivraison = new controllerTableauLivraison(cnx,labCombox.getValue(),user);
        fxmlLoader.setController(controllerTableauLivraison);

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) buttonCommande.getScene().getWindow();
        stage.setTitle("Commande");
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    /**
     * Ouverture de la page des medicaments
     */
    private void openMedicamentTable() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("TableauMedicament.fxml"));
        controllerTableauMedicament controllerTableauMedicament = new controllerTableauMedicament(cnx,labCombox.getValue(),user);
        fxmlLoader.setController(controllerTableauMedicament);

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) buttonMedoc.getScene().getWindow();
        stage.setTitle("Médicament");
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    /**
     * Ouverture de la page des ingrédients
     */
    private void openIngredientTable() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("TableauIngredient.fxml"));
        controllerTableauIngredient controllerTableauIngredient = new controllerTableauIngredient(cnx,user,labCombox.getValue());
        fxmlLoader.setController(controllerTableauIngredient);

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) buttonIngredient.getScene().getWindow();
        stage.setTitle("Ingredient");
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    private void displayAddUser() {
        buttonAddUser.setVisible(Objects.requireNonNull(Tool.checkRole(lesLaboRole, labCombox.getValue())).getId() != "role3");
    }
    
    private void doAddUser(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ManageUser.fxml"));
            controllerManageUser controllerManageUser = new controllerManageUser(cnx,user,labCombox.getValue());
            fxmlLoader.setController(controllerManageUser);

            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
