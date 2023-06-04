package chartreux.applilabv2.DAO;
import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Entity.Role;
import chartreux.applilabv2.Entity.User;
import chartreux.applilabv2.Entity.UserInLab;

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


    public List<User> findAllLab(int offset, int limit,String lab) throws SQLException{
        List<Laboratoire> laboratoireList = new DAOLaboratoire(cnx).findAll();
        List<Role> roles = new DAORole(cnx).findAll();

        List<User> utilisateurs = findAll(offset,limit);

        List<User> lesUtilisateurs = new ArrayList<>();

        HashMap<String, Laboratoire> laboratoireMap = new HashMap<>();
        HashMap<String, Role> roleMap = new HashMap<>();

        for (Laboratoire laboratoire : laboratoireList) {
            laboratoireMap.put(laboratoire.getId(), laboratoire);
        }

        for (Role role : roles) {
            roleMap.put(role.getId(), role);
        }

        for (User utilisateur:utilisateurs) {
            String userId = utilisateur.getId();
            HashMap<Laboratoire, Role> laboratoires = new HashMap<>();

            List<UserInLab> userInLabRecords = new DAOUserInLab(cnx).getUserInLabRecordsByUserId(userId);

            for (UserInLab userInLab: userInLabRecords){
                String labID = userInLab.getLabId();
                Laboratoire laboratoire = laboratoireMap.get(labID);

                String roleId = userInLab.getRoleId();
                Role role = roleMap.get(roleId);

                laboratoires.put(laboratoire,role);

            }

            utilisateur.setLaboratoires(laboratoires);

            lesUtilisateurs.add(utilisateur);
        }

        return lesUtilisateurs;
    }

    /*public List<UserInLab> getUserInLabRecordsByUserId(String userId) {
        List<UserInLab> userInLabRecords = new ArrayList<>();

        // Utilisez une requête SQL pour récupérer les enregistrements correspondants dans la table userInLab
        String query = "SELECT * FROM userInLab WHERE userId = ?";

        try (Connection connection = DriverManager.getConnection("your_database_url", "username", "password");
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Récupérez les valeurs des colonnes de la table userInLab
                    String labId = resultSet.getString("labId");
                    String roleId = resultSet.getString("roleId");

                    // Créez un nouvel objet UserInLab avec les valeurs récupérées
                    UserInLab userInLab = new UserInLab(userId, labId, roleId);

                    // Ajoutez l'objet UserInLab à la liste des enregistrements
                    userInLabRecords.add(userInLab);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userInLabRecords;
    }*/


    public void CreateSup(User user) throws SQLException{
        String idUser = (count()+1) +"user";
        String sql = "INSERT INTO utilisateurs(id, login, password, nom, prenom, role_id) VALUES(?, ?, ?, ?, ?, 1);";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, idUser);
        ps.setString(2, user.getLogin());
        ps.setString(3,user.getPassword());
        ps.setString(4,user.getNom());
        ps.setString(5, user.getPrenom());
        ps.executeUpdate();
        System.out.println("ajouter");
    }

    public void CreateNorm(User user, HashMap<Laboratoire,Role> laboRole) throws SQLException{
        String idUser = "user" + (count()+1);
        String sql = "INSERT INTO utilisateurs(id, login, password, nom, prenom, role_id) VALUES(?, ?, ?, ?, ?, 0);";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, idUser);
        ps.setString(2, user.getLogin());
        ps.setString(3,user.getPassword());
        ps.setString(4,user.getNom());
        ps.setString(5, user.getPrenom());
        ps.executeUpdate();

        sql = "INSERT INTO userInLab (userId, labId, roleId) VALUES(?, ?, ?);";
        ps = cnx.prepareStatement(sql);
        for (Map.Entry<Laboratoire,Role> entry: laboRole.entrySet()) {
            Laboratoire laboratoire =entry.getKey();
            Role role = entry.getValue();
            ps.setString(1, idUser);
            ps.setString(2, laboratoire.getId());
            ps.setString(3, role.getId());
            ps.executeUpdate();
        }

        System.out.println("ajouter");
    }

    public Role userGetRoleLab(User user, Laboratoire laboratoire) throws SQLException {
        Role role = null;
        String sql = "SELECT r.id, r.libelle FROM role r JOIN userInLab uil ON r.id = uil.roleId WHERE uil.userId = ? AND uil.labId = ? ;";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, user.getId());
        ps.setString(2, laboratoire.getId());
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            role = new Role(
                    rs.getString("id"),
                    rs.getString("libelle")
            );
        }
        return role;
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
