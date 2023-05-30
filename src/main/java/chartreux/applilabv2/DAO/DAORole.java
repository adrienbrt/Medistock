package chartreux.applilabv2.DAO;

import chartreux.applilabv2.Entity.Role;
import chartreux.applilabv2.Entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DAORole {

    private Connection cnx;

    public DAORole(Connection cnx){
        this.cnx=cnx;
    }
    public Role find(String id) throws SQLException {
        Role role = null;
        String SQL = "SELECT * FROM role WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(SQL)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    role = new Role(
                            rs.getString("id"),
                            rs.getString("libelle")
                    );
                }
            }
        }
        return role;
    }

}
