package chartreux.applilabv2.DAO;
import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Entity.Role;
import chartreux.applilabv2.Entity.User;
import javafx.util.Pair;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DAOUser {
    private Connection cnx;

    public DAOUser(Connection cnx){
        this.cnx=cnx;
    }

    /**
     * Recherche un utilisateur dans la base de données en fonction de son ID.
     * @param id L'ID de l'utilisateur à rechercher.
     * @return L'utilisateur correspondant à l'ID spécifié, ou null s'il n'est pas trouvé.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
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

    /**
     * Recherche un utilisateur dans la base de données en fonction de son login et mot de passe.
     * @param login Le login de l'utilisateur.
     * @param password Le mot de passe de l'utilisateur.
     * @return L'utilisateur correspondant aux identifiants spécifiés, ou null s'il n'est pas trouvé.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public User findConnect(String login, String password) throws SQLException {
        User user = null;
        String SQL = "SELECT * FROM utilisateurs WHERE login=?";
        try (PreparedStatement ps = cnx.prepareStatement(SQL)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        user = new User(
                                rs.getString("id"),
                                rs.getString("login"),
                                hashedPassword,
                                rs.getString("nom"),
                                rs.getString("prenom"),
                                rs.getBoolean("role_id")
                        );
                    }
                }
            }
        }
        return user;
    }


    /**
     * Récupère tous les utilisateurs présents dans la base de données.
     * @return Une liste contenant tous les utilisateurs.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public List<User> findAll() throws SQLException{
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
            if(!user.getIsAdmin()){
                List<Pair<Laboratoire, Role>> lesLaboRole = new ArrayList<>();
                String sql2 = "SELECT l.id AS labId, r.id AS roleId, r.libelle AS roleNom " +
                        "FROM userInLab ul " +
                        "JOIN laboratoires l ON ul.labId = l.id " +
                        "JOIN role r ON ul.roleId = r.id " +
                        "WHERE ul.userId = ?";
                PreparedStatement ps2 = cnx.prepareStatement(sql2);
                ps2.setString(1, user.getId());
                ResultSet rs2 = ps2.executeQuery();
                while (rs2.next()){
                    Role role = new Role( rs2.getString("roleId"), rs2.getString("roleNom"));
                    Laboratoire laboratoire = new DAOLaboratoire(cnx).findId(rs2.getString("labId"));
                    Pair<Laboratoire, Role> laboRole= new Pair<>(laboratoire,role);
                    lesLaboRole.add(laboRole);
                }
                user.setlesLaboUtil(lesLaboRole);
            }
            users.add(user);
        }
        return users;
    }

    /**
     * Met à jour les informations d'un utilisateur dans la base de données.
     * @param user L'utilisateur à mettre à jour.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public void updateUser(User user) throws SQLException {
        if (user.getIsAdmin()) {
            String sql = "UPDATE utilisateurs SET login=?, nom=?, prenom=?, role_id=1";
            if(!Objects.equals(user.getPassword(), "")){
                sql+= ", password=? WHERE id=?;";
                PreparedStatement ps = cnx.prepareStatement(sql);
                ps.setString(1, user.getLogin());

                ps.setString(2, user.getNom());
                ps.setString(3, user.getPrenom());

                // Hashage du mot de passe
                String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
                ps.setString(4, hashedPassword);
                ps.setString(5, user.getId());

                ps.executeUpdate();
                sql = "DELETE FROM userInLab WHERE userId=? ;";
                ps = cnx.prepareStatement(sql);
                ps.clearParameters();
                ps.setString(1, user.getId());
                ps.executeUpdate();
            }else{
                sql+=" WHERE id=?;";
                PreparedStatement ps = cnx.prepareStatement(sql);
                ps.setString(1, user.getLogin());

                ps.setString(2, user.getNom());
                ps.setString(3, user.getPrenom());

                ps.setString(4, user.getId());

                ps.executeUpdate();
                sql = "DELETE FROM userInLab WHERE userId=? ;";
                ps = cnx.prepareStatement(sql);
                ps.clearParameters();
                ps.setString(1, user.getId());
                ps.executeUpdate();
            }

        } else {
            List<String> currentLabIds = new ArrayList<>();
            String selectCurrentLabIdsSQL = "SELECT labId FROM userInLab WHERE userId = ?";
            PreparedStatement selectCurrentLabIdsPS = cnx.prepareStatement(selectCurrentLabIdsSQL);
            selectCurrentLabIdsPS.setString(1, user.getId());
            ResultSet currentLabIdsRS = selectCurrentLabIdsPS.executeQuery();
            while (currentLabIdsRS.next()) {
                currentLabIds.add(currentLabIdsRS.getString("labId"));
            }

            String sql = "UPDATE utilisateurs SET login=?, nom=?, prenom=?, role_id=0";
            if(!Objects.equals(user.getPassword(), "")){
                sql+= ", password=? WHERE id=?;";
                PreparedStatement ps = cnx.prepareStatement(sql);
                ps.setString(1, user.getLogin());

                ps.setString(2, user.getNom());
                ps.setString(3, user.getPrenom());

                // Hashage du mot de passe
                String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
                ps.setString(4, hashedPassword);
                ps.setString(5, user.getId());

                ps.executeUpdate();
            }else{
                sql+=" WHERE id=?;";
                PreparedStatement ps = cnx.prepareStatement(sql);
                ps.setString(1, user.getLogin());

                ps.setString(2, user.getNom());
                ps.setString(3, user.getPrenom());

                ps.setString(4, user.getId());

                ps.executeUpdate();

            }


            PreparedStatement ps = cnx.prepareStatement(selectCurrentLabIdsSQL);

            for (Pair<Laboratoire, Role> pair : user.getlesLaboUtil()) {
                currentLabIds.remove(pair.getKey().getId());

                sql = "UPDATE userInLab SET roleId=? WHERE userId=? AND labId=?;";
                ps = cnx.prepareStatement(sql);
                ps.clearParameters();
                ps.setString(1, pair.getValue().getId());
                ps.setString(2, user.getId());
                ps.setString(3, pair.getKey().getId());

                ps.executeUpdate();
            }

            for (String labIdToRemove : currentLabIds) {
                String deleteLabIdSQL = "DELETE FROM userInLab WHERE userId = ? AND labId = ?";
                PreparedStatement deleteLabIdPS = cnx.prepareStatement(deleteLabIdSQL);
                deleteLabIdPS.setString(1, user.getId());
                deleteLabIdPS.setString(2, labIdToRemove);
                deleteLabIdPS.executeUpdate();
            }

            // Ajout de l'instruction INSERT pour les nouvelles valeurs de userInLab
            for (Pair<Laboratoire, Role> pair : user.getlesLaboUtil()) {
                // Vérifier si la paire Laboratoire et Role existe déjà dans userInLab
                String checkExistsSQL = "SELECT COUNT(*) FROM userInLab WHERE userId = ? AND labId = ? AND roleId = ?";
                PreparedStatement checkExistsPS = cnx.prepareStatement(checkExistsSQL);
                checkExistsPS.setString(1, user.getId());
                checkExistsPS.setString(2, pair.getKey().getId());
                checkExistsPS.setString(3, pair.getValue().getId());
                ResultSet existsResult = checkExistsPS.executeQuery();
                existsResult.next();
                int count = existsResult.getInt(1);

                if (count == 0) {
                    // La paire n'existe pas, exécuter l'instruction INSERT
                    String insertSQL = "INSERT INTO userInLab (userId, labId, roleId) VALUES (?, ?, ?);";
                    PreparedStatement insertPS = cnx.prepareStatement(insertSQL);
                    insertPS.setString(1, user.getId());
                    insertPS.setString(2, pair.getKey().getId());
                    insertPS.setString(3, pair.getValue().getId());
                    insertPS.executeUpdate();
                }
            }

        }
    }


    /**
     * Récupère tous les utilisateurs appartenant à un laboratoire spécifique.
     * @param laboratoire Le laboratoire spécifique.
     * @return Une liste contenant tous les utilisateurs du laboratoire.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public List<User> findAllInOneLab(Laboratoire laboratoire) throws SQLException{
        List<User> lesUtilisateurs = new ArrayList<>();
        List<Pair<Laboratoire,Role>> leRole= new ArrayList<>();
        String sql = "SELECT u.id, u.login, u.password, u.nom, u.prenom, u.role_id, r.id AS roleId FROM userInLab ul INNER JOIN utilisateurs u ON ul.userId = u.id INNER JOIN laboratoires l ON ul.labId = l.id INNER JOIN `role` r ON ul.roleId = r.id WHERE ul.labId = ?;";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, laboratoire.getId());

        ResultSet rs = ps.executeQuery();

        while (rs.next()){
            Pair<Laboratoire, Role> userLabRole = new Pair<>(laboratoire, new DAORole(cnx).find(rs.getString("roleId")));
            leRole.clear();
            leRole.add(userLabRole);
            User user = new User(
                    rs.getString("id"),
                    rs.getString("login"),
                    rs.getString("password"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getBoolean("role_id")
            );
            user.setlesLaboUtil(leRole);
            lesUtilisateurs.add(user);
        }
        return lesUtilisateurs;

    }


    /**
     * Récupère la liste des associations laboratoire-rôle pour un utilisateur spécifique.
     * @param userId L'identifiant de l'utilisateur.
     * @return Une liste des associations laboratoire-rôle de l'utilisateur.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public List<Pair<Laboratoire, Role>> getLesLaboRole(String userId) throws SQLException {
        List<Pair<Laboratoire, Role>> lesLabRole = new ArrayList<>();
        String query = "SELECT labId, roleId FROM userInLab WHERE userId = ?";
        PreparedStatement statement = cnx.prepareStatement(query);
        statement.setString(1,userId);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()){
            Laboratoire leLabo = new DAOLaboratoire(cnx).findId(resultSet.getString("labId"));
            Role leRole = new DAORole(cnx).find(resultSet.getString("roleId"));

            lesLabRole.add(new Pair<>(leLabo,leRole));
        }
        return lesLabRole;
    }


    /**
     * Crée un nouvel utilisateur administrateur dans la base de données.
     * @param user L'utilisateur administrateur à créer.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public void CreateSup(User user) throws SQLException {
        String idUser = (count() + 1) + "user";
        String sql = "INSERT INTO utilisateurs(id, login, password, nom, prenom, role_id) VALUES(?, ?, ?, ?, ?, 1);";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, idUser);
        ps.setString(2, user.getLogin());

        // Hashage du mot de passe
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        ps.setString(3, hashedPassword);

        ps.setString(4, user.getNom());
        ps.setString(5, user.getPrenom());
        ps.executeUpdate();
    }

    /**
     * Crée un nouvel utilisateur normal dans la base de données.
     * @param user L'utilisateur normal à créer.
     * @throws SQLException En cas d'erreur lors de l'exécution de la requête SQL.
     */
    public void CreateNorm(User user) throws SQLException {
        String idUser = "user" + (count() + 1);
        String sql = "INSERT INTO utilisateurs(id, login, password, nom, prenom, role_id) VALUES(?, ?, ?, ?, ?, 0);";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, idUser);
        ps.setString(2, user.getLogin());

        // Hashage du mot de passe
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        ps.setString(3, hashedPassword);

        ps.setString(4, user.getNom());
        ps.setString(5, user.getPrenom());
        ps.executeUpdate();

        sql = "INSERT INTO userInLab (userId, labId, roleId) VALUES(?, ?, ?);";
        ps = cnx.prepareStatement(sql);
        for (Pair<Laboratoire,Role> entry : user.getlesLaboUtil()) {
            Laboratoire laboratoire = entry.getKey();
            Role role = entry.getValue();
            ps.setString(1, idUser);
            ps.setString(2, laboratoire.getId());
            ps.setString(3, role.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Retourne le role d'un utilisateur dans un laboratoire
     * @param user l'utilisateur
     * @param laboratoire le labo
     * @throws SQLException
     */
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

    /**
     * Retourne le nombre d'utilisateur
     * @return int
     * @throws SQLException
     */
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
