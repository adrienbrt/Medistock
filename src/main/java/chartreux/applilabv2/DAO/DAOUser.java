package chartreux.applilabv2.DAO;
import chartreux.applilabv2.Entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAOUser {
    private Connection cnx;

    public DAOUser(Connection cnx){
        this.cnx=cnx;
    }

    public User find(String id) throws SQLException {
        User user = null;
        String SQL = "SELECT * FROM utilisateurs WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(SQL)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getString("id"),
                            rs.getString("login"),
                            rs.getString("password"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("role_id")
                    );
                }
            }
        }
        return user;
    }
}
