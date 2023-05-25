package chartreux.applilabv2;

import chartreux.applilabv2.DAO.DAOUser;
import chartreux.applilabv2.Entity.User;
import chartreux.applilabv2.Util.Singleton;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.Console;
import java.io.IOException;
import java.sql.SQLException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Connexion");
        stage.setScene(scene);
        stage.show();
    }

    public static void testFind() throws SQLException {
        java.sql.Connection cnx = Singleton.getInstance().cnx;
        User user = new DAOUser(cnx).find("user1");
        System.out.println(user.toString());

    }
    public static void main(String[] args) throws SQLException {
        testFind();
        launch();
    }
}