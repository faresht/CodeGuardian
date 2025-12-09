
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class VulnerableCode {

    public void connectAndExecute(String username) {
        // [VULNERABILITY] Hardcoded Credentials
        String dbPassword = "password123";

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test", "root", dbPassword);
            Statement stmt = conn.createStatement();

            // [VULNERABILITY] SQL Injection
            String query = "SELECT * FROM users WHERE username = '" + username + "'";
            stmt.executeQuery(query);

            // [VULNERABILITY] Command Injection
            Runtime.getRuntime().exec("echo " + username);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
