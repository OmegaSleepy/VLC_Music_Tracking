import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Test {
    public static void main(String[] args) throws SQLException {
        var con = DriverManager.getConnection("jdbc:sqlite:musicspy.db");

        ResultSet rs = con.createStatement().executeQuery("select \"hello world\"");

        System.out.println();
    }
}
