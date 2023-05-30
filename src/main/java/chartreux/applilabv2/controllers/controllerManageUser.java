package chartreux.applilabv2.controllers;

import chartreux.applilabv2.Entity.User;

import java.sql.Connection;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class controllerManageUser {
    @FXML
    private TableColumn<?, ?> laboColumn;

    @FXML
    private TableColumn<?, ?> loginColumn;

    @FXML
    private TableColumn<?, ?> nomColumn;

    @FXML
    private TableColumn<?, ?> prenomColumn;

    @FXML
    private TextField searchBar;

    @FXML
    private TableView<?> tableView;
    private Connection cnx;
    private User user;

    public controllerManageUser(Connection cnx, User user) {
        this.cnx = cnx;
        this.user = user;
    }


}
