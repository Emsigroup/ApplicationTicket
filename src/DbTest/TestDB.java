package DbTest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDB {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/applicationticket"; // nom de ta base
        String user = "root"; // utilisateur XAMPP par défaut
        String password = ""; // mot de passe vide si non modifié

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion good !");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
