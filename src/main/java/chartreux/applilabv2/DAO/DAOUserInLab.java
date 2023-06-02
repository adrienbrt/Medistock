package chartreux.applilabv2.DAO;

import chartreux.applilabv2.Entity.Laboratoire;
import chartreux.applilabv2.Entity.Role;
import chartreux.applilabv2.Entity.UserInLab;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DAOUserInLab {
    private final Connection cnx;

    public DAOUserInLab(Connection cnx) {
        this.cnx = cnx;
    }

    public List<UserInLab> getUserInLabRecordsByUserId(String userId) throws SQLException {
        List<UserInLab> userInLabRecords = new ArrayList<>();
        String query = "SELECT * FROM userInLab WHERE userId = ?";
        PreparedStatement statement = cnx.prepareStatement(query);
        statement.setString(1,userId);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()){
            String labId = resultSet.getString("labId");
            String roleId = resultSet.getString("roleId");

            // Créez un nouvel objet UserInLab avec les valeurs récupérées
            UserInLab userInLab = new UserInLab(userId, labId, roleId);
            userInLabRecords.add(userInLab);
        }
        return userInLabRecords;
    }

    public HashMap<Laboratoire, Role> getLesLabRole(String userId) throws SQLException {
        HashMap<Laboratoire, Role> lesLabRole = new HashMap<>();
        String query = "SELECT labId, roleId FROM userInLab WHERE userId = ?";
        PreparedStatement statement = cnx.prepareStatement(query);
        statement.setString(1,userId);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()){
            Laboratoire leLabo = new DAOLaboratoire(cnx).findId(resultSet.getString("labId"));
            Role leRole = new DAORole(cnx).find(resultSet.getString("roleId"));

            lesLabRole.put(leLabo,leRole);
        }
        return lesLabRole;
    }
}
