package chartreux.applilabv2;

import chartreux.applilabv2.DAO.DAOUser;
import chartreux.applilabv2.Entity.User;
import chartreux.applilabv2.Util.Singleton;
import chartreux.applilabv2.controllers.controllerLogin;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {
        Connection cnx = Singleton.getInstance().cnx;

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Login.fxml"));
        controllerLogin loginController = new controllerLogin(cnx);
        fxmlLoader.setController(loginController);

        Parent root = fxmlLoader.load();


        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Connexion");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) throws SQLException {
        launch();
    }
}