package chartreux.applilabv2.Util;
import java.sql.DriverManager;
import java.sql.SQLException;
public class Singleton {
    public java.sql.Connection cnx;
    private static Singleton instance;
    private static String dsn;
    private static String username;
    private static String password;

    private Singleton() throws SQLException {

        dsn ="jdbc:mysql://10.30.103.78:3306/javadb";
        username = "pharmaapp";
        password = "123+aze";
        cnx = DriverManager.getConnection(dsn, username, password);

    }

    public static Singleton getInstance() throws SQLException {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
