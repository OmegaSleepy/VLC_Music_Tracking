package vlc.util;

import vlc.Main;

import java.sql.*;
import java.util.ArrayList;

public class SQLUtil {

    public static ArrayList<String[]> selectOperation (PreparedStatement statement) {
        var result = new ArrayList<String[]>();

        try (ResultSet resultSet = statement.executeQuery()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            String[] columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = (metaData.getColumnName(i+1));
            }

            result.addFirst(columnNames);

            // loop through all rows
            while (resultSet.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    Object value = resultSet.getObject(i);
                    row[i-1] = (value != null) ? value.toString() : null;
                }
                result.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static Connection getConnection() {

        String url;

        if(Main.config.dbType.equals("local")){
            url = "jdbc:sqlite:musicspy.db";
        } else{
            url = "jbdc:sqlite:" + Main.config.dbPath.toString();
        }

        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createTable(){
        try {
            var con = getConnection();
            con.prepareStatement("""
            	create table IF NOT EXISTS music(
            	    "title" TEXT NOT NULL,
            	    "artist" TEXT NOT NULL,
            	    "album" TEXT NOT NULL,
            	    "url" TEXT NOT NULL,
            	    "length" INTEGER NOT NULL DEFAULT 1,
            	    "timesSeen"	INTEGER NOT NULL DEFAULT 1,
            	    "playtime"	INTEGER DEFAULT 1,
            	    PRIMARY KEY("title")
            	)""");


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
