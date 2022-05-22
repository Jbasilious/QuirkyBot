import java.sql.*;
import java.util.ArrayList;

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

    protected static String selectYEA_AUDIO() throws SQLException {

        try (PreparedStatement statement = connection.prepareStatement(
                "select ID from YEA_AUDIO")) {

            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            ArrayList<String> yeahs = new ArrayList<>(columnCount);
            while (resultSet.next()) {
                int i = 1;
                while (i <= columnCount) {
                    yeahs.add(resultSet.getString(i++));
                }
            }
            int r = (int) (Math.random() * (yeahs.size() - 1));
            return yeahs.get(r);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

}