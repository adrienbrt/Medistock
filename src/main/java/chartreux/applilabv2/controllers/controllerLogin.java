package chartreux.applilabv2.controllers;

import chartreux.applilabv2.DAO.DAOUser;
import chartreux.applilabv2.Entity.User;
import chartreux.applilabv2.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class controllerLogin {
    private Connection cnx;
    @FXML
    private Button connexionButton;

    @FXML
    private Label loginMessageLabel;
    @FXML
    private PasswordField passwordLabel;

    @FXML
    private TextField usernameLabel;

    public controllerLogin(Connection cnx){
        this.cnx = cnx;
    }
    @FXML
    void onClickLogin(ActionEvent event) throws SQLException, IOException {

        if(!usernameLabel.getText().isBlank() && !passwordLabel.getText().isBlank()){
            loginMessageLabel.setText("tentative de connexion");
            User user = new DAOUser(cnx).findConnect(usernameLabel.getText(),passwordLabel.getText());
//            User user = new DAOUser(cnx).findConnect("Superadmin","123+aze");

            if (user == null){
                loginMessageLabel.setText("mauvais identifiants");
            }else if (user.getIsAdmin()){
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ManageUser.fxml"));
                controllerManageUser controllerManageUser = new controllerManageUser(cnx,user,null);
                fxmlLoader.setController(controllerManageUser);

                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) connexionButton.getScene().getWindow();
                stage.setTitle("Accueil");
                stage.setScene(scene);
                stage.centerOnScreen();
            }
            else {
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Tableau.fxml"));
                controllerTableau controllerTableau = new controllerTableau(cnx,user);
                fxmlLoader.setController(controllerTableau);

                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) connexionButton.getScene().getWindow();
                stage.setScene(scene);
                stage.centerOnScreen();
            }
        }else {
            loginMessageLabel.setText("veuillez remplir les champs");
        }
    }


}
