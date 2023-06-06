package chartreux.applilabv2.controllers;

import chartreux.applilabv2.DAO.DAOLaboratoire;
import chartreux.applilabv2.DAO.DAORole;
import chartreux.applilabv2.DAO.DAOUser;
import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Entity.Role;
import chartreux.applilabv2.Entity.User;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

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
import javafx.util.Pair;
import javafx.util.StringConverter;

public class controllerManageUser implements Initializable {
    @FXML
    private Label labelErrorRole;
    @FXML
    private Button ButtonAdd;
    @FXML
    private TextField fieldLogin;
    @FXML
    private TextField fieldNom;
    @FXML
    private PasswordField fieldPassword;
    @FXML
    private TextField fieldPrenom;
    @FXML
    private ComboBox<Role> comboRole;
    @FXML
    private ComboBox<Laboratoire> comboLabo;
    @FXML
    private TableColumn<User, String> loginColumn;
    @FXML
    private TableColumn<User, String> nomColumn;
    @FXML
    private TableColumn<User, String> prenomColumn;
    @FXML
    private TableView<User> tableView;
    @FXML
    private Button suppLabRole;
    @FXML
    private TableView<Pair<Laboratoire, Role>> tableLabRole;
    @FXML
    private Button addLabRole;
    @FXML
    private Button btnUpdate;

    @FXML
    private TableColumn<Pair<Laboratoire, Role>, String> laboColumn;
    @FXML
    private TableColumn<Pair<Laboratoire, Role>, String> roleColumn;

    private final Connection cnx;
    private final User user;
    private Laboratoire laboratoire;
    private List<User> listUsers;
    private List<Pair<Laboratoire,Role>> listLabRole;
    private boolean isAdmin;
    private DAOUser daoUser;

    public controllerManageUser(Connection cnx, User user, Laboratoire laboratoire) {
        this.cnx = cnx;
        this.user = user;
        this.laboratoire =laboratoire;
        this.daoUser = new DAOUser(cnx);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnUpdate.setVisible(false);

        listLabRole = new ArrayList<>();
        listUsers = new ArrayList<>();

        loginColumn.setCellValueFactory(new PropertyValueFactory<User,String>("login"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<User,String>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<User,String >("prenom"));

        laboColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getKey().getNom()));
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue().getLibelle()));

        isAdmin = false;

        /* ajoute utilisteur dans tableau */
        try {
            setupTableUser();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        /*set values combobox labo*/
        try {
            if(laboratoire==null){
                ObservableList<Laboratoire> listLaboratoire = FXCollections.observableList(new DAOLaboratoire(cnx).findAll());
                comboLabo.setItems(listLaboratoire);
            }else{
                ObservableList<Laboratoire> listLaboratoire = FXCollections.observableList(new ArrayList<>());
                listLaboratoire.add(laboratoire);
                comboLabo.setItems(listLaboratoire);
            }
            comboLabo.setConverter(new StringConverter<Laboratoire>() {
                @Override
                public String toString(Laboratoire laboratoire) {
                    return laboratoire != null ? laboratoire.getNom() : "";
                }
                @Override
                public Laboratoire fromString(String s) {
                    return null;
                }

            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        /*set values combobox role*/
        try {
            ObservableList<Role> listRole = FXCollections.observableList(new DAORole(cnx).findAll());

            if(!user.getIsAdmin()){
               listRole.remove(0);
            }
            comboRole.setItems(listRole);
            comboRole.setConverter(new StringConverter<Role>() {
                @Override
                public String toString(Role role) {
                    return role != null ? role.getLibelle() : "";
                }

                @Override
                public Role fromString(String s) {
                    return null;
                }

            });


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        /*action combo role*/
        comboRole.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                setComboRole();
            }
        });

        /*Selection un utilisateur*/
        tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                doSelectUser(mouseEvent);
            }
        });

        /*Selection un roleLabo*/
        tableLabRole.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                doSelectLabRole(mouseEvent);
            }
        });

        /*ajoute un role à un laboratoire*/
        addLabRole.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                addRole();
            }
        });

        /*supprime un role*/
        suppLabRole.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                suppRole();
            }
        });

        /*Ajoute utilisateur*/
        ButtonAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    clickAdd();

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        
        /*Mise a jours utilisateur*/
        btnUpdate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    doUpdateUser();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private void doUpdateUser() throws SQLException {
        String id = tableView.getSelectionModel().getSelectedItem().getId();
        if(id != null){
            if(isAdmin){
                User userUpdate = new User(id,
                        fieldLogin.getText(),
                        fieldPassword.getText(),
                        fieldNom.getText(),fieldPrenom.getText(),
                        isAdmin);
                daoUser.updateUser(userUpdate);
            }else{
                User userUpdate = new User(id,
                        fieldLogin.getText(),
                        fieldPassword.getText(),
                        fieldNom.getText(),fieldPrenom.getText(),
                        isAdmin,
                        listLabRole);
                daoUser.updateUser(userUpdate);
            }
            tableView.getSelectionModel().clearSelection();
            setupTableUser();
            fieldLogin.setText("");
            fieldNom.setText("");
            fieldPrenom.setText("");
            fieldPassword.setText("");
            tableLabRole.getItems().clear();
            listLabRole.clear();
            ButtonAdd.setVisible(true);
            btnUpdate.setVisible(false);
        }
    }

    private void doSelectLabRole(MouseEvent mouseEvent) {
        Pair<Laboratoire,Role> selected = tableLabRole.getSelectionModel().getSelectedItem();
        if(selected != null){
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                comboLabo.setValue(null);
                comboRole.setValue(null);
            }else{
                comboLabo.setValue(selected.getKey());
                comboRole.setValue(selected.getValue());
            }
        }
    }

    private void doSelectUser(MouseEvent mouseEvent){
        User selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                tableView.getSelectionModel().clearSelection();
                fieldLogin.setText("");
                fieldNom.setText("");
                fieldPrenom.setText("");
                fieldPassword.setText("");
                tableLabRole.getItems().clear();
                listLabRole.clear();
                ButtonAdd.setVisible(true);
                btnUpdate.setVisible(false);
            }else{
                btnUpdate.setVisible(true);
                ButtonAdd.setVisible(false);
                fieldLogin.setText(selected.getLogin());
                fieldPrenom.setText(selected.getPrenom());
                fieldNom.setText(selected.getNom());

                if(selected.getlesLaboUtil() != null) {
                    listLabRole = selected.getlesLaboUtil();
                    ObservableList<Pair<Laboratoire, Role>> selectedObservableList = FXCollections.observableArrayList(selected.getlesLaboUtil());
                    tableLabRole.setItems(selectedObservableList);
                }else {
                    ObservableList<Pair<Laboratoire, Role>> selectedObservableList = FXCollections.observableArrayList(new ArrayList<>());
                    tableLabRole.setItems(selectedObservableList);
                }
            }
        }
    }
    private void addRole(){
        if(comboRole.getValue() == null || (comboLabo.getValue() == null && !Objects.equals(comboRole.getValue().getId(), "role1"))){
            labelErrorRole.setText("vueuillez remplir les champs");
        }else{
            Role newRole = comboRole.getValue();
            if(Tool.checkKeyExistsLabo(listLabRole,comboLabo.getValue())){
                for (int i = 0; i < listLabRole.size(); i++) {
                    Pair<Laboratoire, Role> pair = listLabRole.get(i);
                    if(pair.getKey().equals(comboLabo.getValue())){
                        listLabRole.set(i,new Pair<>(comboLabo.getValue(),newRole));
                        break;
                    }
                }
            }else if(Objects.equals(newRole.getId(), "role1")){
                listLabRole.clear();
                isAdmin = true;
                listLabRole.add(new Pair<>(new Laboratoire(), newRole));
            }else {
                if(isAdmin){
                    listLabRole.clear();
                    isAdmin = false;
                }
                listLabRole.add(new Pair<>(comboLabo.getValue(),newRole));
            }

            // Convertissez la liste en une ObservableList
            ObservableList<Pair<Laboratoire, Role>> observableList = FXCollections.observableArrayList(listLabRole);

            // Affectez la liste à la table
            tableLabRole.setItems(observableList);
        }
    }

    private void suppRole(){
        if(tableLabRole.getSelectionModel().isEmpty()){
            labelErrorRole.setText("Aucun role selectionné");
        }else{
            labelErrorRole.setText("");
            Laboratoire leLabo = tableLabRole.getSelectionModel().getSelectedItem().getKey();
            Role leRole = tableLabRole.getSelectionModel().getSelectedItem().getValue();
            Pair<Laboratoire, Role> pair  = new Pair<>(leLabo,leRole);
            listLabRole.remove(pair);
        }
        // Convertissez la liste en une ObservableList
        ObservableList<Pair<Laboratoire, Role>> observableList = FXCollections.observableArrayList(listLabRole);

        // Affectez la liste à la table
        tableLabRole.setItems(observableList);
    }

    private void clickAdd() throws SQLException {
        User user1 = new User();
        if (
                fieldLogin.getText().isEmpty() ||
                fieldPassword.getText().isEmpty() ||
                fieldNom.getText().isEmpty() || fieldPrenom.getText().isEmpty() || listLabRole.isEmpty()
        ){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur d'ajout");
            alert.setContentText("Un champs est manquant");

            alert.showAndWait();
        }else if (isAdmin){
            user1.setId("useNew");
            user1.setLogin(fieldLogin.getText());
            user1.setPassword(fieldPassword.getText());
            user1.setNom(fieldNom.getText());
            user1.setPrenom(fieldPrenom.getText());
            user1.setIsAdmin(isAdmin);
            new DAOUser(cnx).CreateSup(user1);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ajout");
            alert.setContentText("Ajout de l'utilisateur " +user1.getLogin());

            alert.showAndWait();


        }else{

            user1.setLogin(fieldLogin.getText());
            user1.setPassword(fieldPassword.getText());
            user1.setNom(fieldNom.getText());
            user1.setPrenom(fieldPrenom.getText());
            user1.setIsAdmin(false);
            user1.setlesLaboUtil(listLabRole);

            new DAOUser(cnx).CreateNorm(user1);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ajout");
            alert.setContentText("Ajout de l'utilisateur " +user1.getLogin());

            alert.showAndWait();
        }
        fieldLogin.setText("");
        fieldPrenom.setText("");
        fieldNom.setText("");
        fieldPassword.setText("");

        ObservableList<User> users = FXCollections.observableList(new DAOUser(cnx).findAll());

        tableView.setItems(users);
    }

    private void setupTableUser() throws SQLException {
        if(user.getIsAdmin() || laboratoire==null){
            listUsers = daoUser.findAll();
            ObservableList<User> userObservableList = FXCollections.observableList(listUsers);
            tableView.setItems(userObservableList);
        }else{
            listUsers = daoUser.findAllInOneLab(laboratoire);
            ObservableList<User> userObservableList = FXCollections.observableList(listUsers);
            tableView.setItems(userObservableList);
        }
    }

    private void setComboRole() {
        Role selectedRole = comboRole.getValue();
        assert selectedRole != null;
        if (selectedRole.getId().equals("role1")){
            comboLabo.setDisable(true);
            comboLabo.setValue(null);
        }else {
            comboLabo.setDisable(false);
        }
    }

}
