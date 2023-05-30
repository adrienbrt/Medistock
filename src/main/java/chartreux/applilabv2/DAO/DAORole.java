package chartreux.applilabv2.DAO;

import chartreux.applilabv2.Entity.Role;
import chartreux.applilabv2.Entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public List<Role> findAll() throws SQLException{
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM role";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            Role role = new Role( rs.getString("id"),
                    rs.getString("libelle"));
            roles.add(role);
        }
        return roles;
    }

}
