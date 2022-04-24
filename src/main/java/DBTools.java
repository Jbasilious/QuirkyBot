import java.sql.*;

public class DBTools {
    private static Connection connection;


    protected static void openConnection() throws SQLException {
        try {
            connection = DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PASS);
            System.out.println("VALID: " + connection.isValid(5));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            closeConnection();
        }
    }

    protected static void closeConnection() throws SQLException {
        System.out.println("Closing Connection...");
        connection.close();
        System.out.println("VALID: " + connection.isValid(5));

    }

    protected static void insertGUILD_USER(String GUILD, String UID, int YEACOUNT) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO GUILD_USER(GUILD, UID, YEACOUNT) VALUES (?,?,?)")) {
            statement.setString(1, GUILD);
            statement.setString(2, UID);
            statement.setInt(3, YEACOUNT);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    protected static void updateGUILD_USER(String GUILD, String UID, int YEACOUNT) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE GUILD_USER set  YEACOUNT=? WHERE GUILD=? and UID=?")) {
            statement.setString(2, GUILD);
            statement.setString(3, UID);
            statement.setInt(1, YEACOUNT);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    protected static ResultSet selectGUILD_USER(String condition) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "select UID, YEACOUNT from GUILD_USER " + condition)) {
            ResultSet resultSet = statement.executeQuery();
            return resultSet;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

}