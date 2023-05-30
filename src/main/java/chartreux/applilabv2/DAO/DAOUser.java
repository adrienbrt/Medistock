package chartreux.applilabv2.DAO;
import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Entity.Role;
import chartreux.applilabv2.Entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                            rs.getBoolean("role_id")
                    );
                }
            }
        }
        return user;
    }

    public User findConnect(String login, String password) throws SQLException {
        User user = null;
        String SQL = "SELECT * FROM utilisateurs WHERE login=? AND password=?";
        try (PreparedStatement ps = cnx.prepareStatement(SQL)) {
            ps.setString(1, login);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getString("id"),
                            rs.getString("login"),
                            rs.getString("password"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getBoolean("role_id")
                    );
                }
            }
        }
        return user;
    }

    public List<User> findAll(int offset, int limit) throws SQLException{
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){
            User user = new User( rs.getString("id"),
                    rs.getString("login"),
                    rs.getString("password"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getBoolean("role_id"));
            users.add(user);
        }
        return users;
    }


    public List<User> findByLab(int offset, int limit,String lab) throws SQLException{
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.id, u.login, u.password, u.nom, u.prenom, u.role_id, l.nom as nomLab FROM utilisateurs u, userInLab uil, laboratoires l  WHERE u.id = uil.userId AND uil.labId = l.id AND l.id =? LIMIT ? OFFSET ?;";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, lab+"%");
        ps.setInt(2, limit);
        ps.setInt(3, offset);
        ResultSet rs = ps.executeQuery();
        while (rs.next()){

            User user = new User( rs.getString("id"),
                    rs.getString("login"),
                    rs.getString("password"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getBoolean("role_id"),
                    rs.getString("nomLab"));
            users.add(user);
        }
        return users;
    }

    public void CreateSup(User user) throws SQLException{
        String idUser = (count()+1) +"user";
        String sql = "INSERT INTO utilisateurs(id, login, password, nom, prenom, role_id) VALUES(?, ?, ?, ?, ?, 1);";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, idUser);
        ps.setString(2, user.getLogin());
        ps.setString(3,user.getPassword());
        ps.setString(4,user.getNom());
        ps.setString(5, user.getPrenom());
        ResultSet rs = ps.executeQuery();
        System.out.println("ajouter");
    }

    public void CreateNorm(User user, HashMap<Laboratoire,Role> laboRole) throws SQLException{
        String idUser = (count()+1) +"user";
        String sql = "INSERT INTO utilisateurs(id, login, password, nom, prenom, role_id) VALUES(?, ?, ?, ?, ?, 0);";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, idUser);
        ps.setString(2, user.getLogin());
        ps.setString(3,user.getPassword());
        ps.setString(4,user.getNom());
        ps.setString(5, user.getPrenom());
        ResultSet rs = ps.executeQuery();

        sql = "INSERT INTO userInLab (userId, labId, roleId) VALUES(?, ?, ?);";
        ps = cnx.prepareStatement(sql);
        for (Map.Entry<Laboratoire,Role> entry: laboRole.entrySet()) {
            Laboratoire laboratoire =entry.getKey();
            Role role = entry.getValue();
            ps.setString(1, idUser);
            ps.setString(2, laboratoire.getId());
            ps.setString(3, role.getId());
            rs = ps.executeQuery();
        }

        System.out.println("ajouter");
    }



    public void Create() throws SQLException{
        String idUser = (count()+1) +"user";
        System.out.println(idUser);

    }
    public int count() throws SQLException {
        int count = 0;
        String SQL = "SELECT COUNT(*) FROM utilisateurs";
        PreparedStatement ps = cnx.prepareStatement(SQL);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            count = rs.getInt(1);
        }
        return count;
    }

}
