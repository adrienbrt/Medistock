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
    private TextField searchBar;
    @FXML
    private TableView<User> tableView;
    @FXML
    private Button suppLabRole;
    @FXML
    private TableView<HashMap<Laboratoire, Role>> tableLabRole;
    @FXML
    private Button addLabRole;
    @FXML
    private TableColumn<HashMap<Laboratoire, Role>, String> laboColumn;
    @FXML
    private TableColumn<HashMap<Laboratoire, Role>, String> roleColumn;

    private final Connection cnx;
    private final User user;

    private List<User> users;

    private HashMap<Laboratoire,Role> labRole;

    private  List<HashMap<Laboratoire,Role>> listLabRole;

    public controllerManageUser(Connection cnx, User user) {
        this.cnx = cnx;
        this.user = user;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        labRole = new HashMap<>();
        listLabRole = new ArrayList<>();



        /* ajoute utilisteur dans tableau */
        try {
            setupTableUser();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        /*set values combobox labo*/
        try {
            ObservableList<Laboratoire> listLaboratoire = FXCollections.observableList(new DAOLaboratoire(cnx).findAll());
            comboLabo.setItems(listLaboratoire);

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

        /*action combo Labo*/
        comboLabo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                setComboLabo();
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

        /*Selection un utilisateur*/
        tableView.setOnMouseClicked(mouseEvent -> {
            User selected = tableView.getSelectionModel().getSelectedItem();
            listLabRole.clear();

            if (selected != null) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
                    tableView.getSelectionModel().clearSelection();
                    fieldLogin.setText("");
                    fieldNom.setText("");
                    fieldPrenom.setText("");
                    fieldPassword.setText("");
                    tableLabRole.getItems().clear();
                    listLabRole.clear();
                    labRole.clear();
                }else{
                    fieldLogin.setText(selected.getLogin());
                    fieldPrenom.setText(selected.getPrenom());
                    fieldNom.setText(selected.getNom());

                    if(selected.getLaboratoires().isEmpty()){
                        tableLabRole.getItems().clear();
                    }else {
                        labRole = selected.getLaboratoires();

                        labRole.forEach((key, value)->{
                            HashMap<Laboratoire, Role> map = new HashMap<>();
                            map.put(key,value);
                            listLabRole.add(map);
                        });

                        // Convertissez la liste en une ObservableList
                        ObservableList<HashMap<Laboratoire, Role>> selectedObservableList = FXCollections.observableArrayList(listLabRole);

                        // Affectez la liste à la table
                        tableLabRole.setItems(selectedObservableList);

                        laboColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().keySet().iterator().next().getNom()));
                        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().values().iterator().next().getLibelle()));
                    }
                }
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
    }


    private void addRole(){
        if(comboRole.getValue() == null || (comboLabo.getValue() == null && !Objects.equals(comboRole.getValue().getId(), "role1"))){
            labelErrorRole.setText("vueuillez remplir les champs");
        }else{
            listLabRole.clear();
            labelErrorRole.setText("");
            System.out.println(labRole.containsKey(comboLabo.getValue()));

            if (labRole.containsKey(comboLabo.getValue())){
                Role old = labRole.get(comboLabo.getValue());
                labRole.replace(comboLabo.getValue(),old,comboRole.getValue());
            }else{
                labRole.put(comboLabo.getValue(),comboRole.getValue());
            }
            labRole.forEach((key, value)->{
                HashMap<Laboratoire, Role> map = new HashMap<>();
                map.put(key,value);
                listLabRole.add(map);
            });


            System.out.println(labRole.size());
            System.out.println(listLabRole.size());

            // Convertissez la liste en une ObservableList
            ObservableList<HashMap<Laboratoire, Role>> ObservableList = FXCollections.observableArrayList(listLabRole);

            // Affectez la liste à la table
            tableLabRole.setItems(ObservableList);

            laboColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().keySet().iterator().next().getNom()));
            roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().values().iterator().next().getLibelle()));

        }
    }

    private void suppRole(){



    }

    private void clickAdd() throws SQLException {
        User user1 = new User();
        if (
                fieldLogin.getText().isEmpty() ||
                fieldPassword.getText().isEmpty() ||
                fieldNom.getText().isEmpty() || fieldPrenom.getText().isEmpty() || comboRole.getValue() == null
        ){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Erreur");
            alert.setHeaderText("Problème d'ajout");
            alert.setContentText("Un champs est manquant");

            alert.showAndWait();
        }else if (comboRole.getValue().getId().equals("role1")){
            user1.setLogin(fieldLogin.getText());
            user1.setPassword(fieldPassword.getText());
            user1.setNom(fieldNom.getText());
            user1.setPrenom(fieldPrenom.getText());
            user1.setIsAdmin(true);
            new DAOUser(cnx).CreateSup(user1);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ajout");
            alert.setContentText("Ajout de l'utilisateur " +user1.getLogin());

            alert.showAndWait();
        }else if(!comboRole.getValue().getId().equals("role1")
                && comboRole.getValue() != null
                && comboLabo.getValue()!= null){

            HashMap<Laboratoire,Role> labRole = new HashMap<>();

            user1.setLogin(fieldLogin.getText());
            user1.setPassword(fieldPassword.getText());
            user1.setNom(fieldNom.getText());
            user1.setPrenom(fieldPrenom.getText());
            user1.setIsAdmin(false);

            labRole.put(comboLabo.getValue(),comboRole.getValue());

            new DAOUser(cnx).CreateNorm(user1,labRole);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ajout");
            alert.setContentText("Ajout de l'utilisateur " +user1.getLogin());

            alert.showAndWait();
        }

        ObservableList<User> users = FXCollections.observableList(new DAOUser(cnx).findAll(0,50));

        loginColumn.setCellValueFactory(new PropertyValueFactory<User,String>("login"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<User,String>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<User,String >("prenom"));

        tableView.setItems(users);
    }

    private void setupTableUser() throws SQLException {
        ObservableList<User> users = FXCollections.observableList(new DAOUser(cnx).findAllLab(0,50,""));

        loginColumn.setCellValueFactory(new PropertyValueFactory<User,String>("login"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<User,String>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<User,String >("prenom"));

        tableView.getItems().addAll(users);
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

    private void setComboLabo() {
        Laboratoire selectedLabo = comboLabo.getValue();
        if (selectedLabo != null) {

            String nom = selectedLabo.getNom();
            // Utilisez le libellé sélectionné comme nécessaire
            System.out.println("Labo sélectionné : " + nom);
        }
    }



}
