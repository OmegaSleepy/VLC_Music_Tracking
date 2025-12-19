package vlc.common;

import vlc.logger.CrashedKt;
import vlc.logger.Log;
import vlc.tracker.SongEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Util {

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:musicspy.db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String quote (String s) {
        return "\"" + s + "\"";
    }

    public static String getUserInput(String message){

        Scanner scanner = new Scanner(System.in);

        System.out.print(message + ": ");

        return scanner.nextLine();

    }

    public static String getFormatedResult(int value){
        if(value < 10){
            return "0" + value;
        }
        return String.valueOf(value);
    }

    public static String formatTimeHH (String value) {
        int seconds = (int) Double.parseDouble(value);

        return (seconds / 3600) +
                ":" +
                getFormatedResult((seconds / 60) % 60) +
                ":" +
                getFormatedResult(seconds % 60);
    }

    public static String formatTimeMM (String value) {
        int seconds = (int) Double.parseDouble(value);

        return (seconds / 60) % 60 +
                ":" +
                getFormatedResult(seconds % 60);
    }

    public static void end(Exception e){
        Log.error("Check if VLC is started.");
        CrashedKt.setCRASHED(true);
    }

    public static boolean isValid (SongEntry songEntry) {
        return songEntry.equals(SongEntry.EMPTY_SONG_RECORD) || songEntry.equals(SongEntry.TITLE_SONG_RECORD);
    }

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

    public static void printSongs(){

        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("""
                SELECT title, artist, album, length, timesSeen as `Plays`, playtime as `Playtime` FROM music
                where title != 'title'
                order by `Playtime` desc
            """);

            Log.logSelect.accept(
                    selectOperation(preparedStatement)
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createTable(){
        try {
            var con = DriverManager.getConnection("jdbc:sqlite:musicspy.db");
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

    public static void main (String[] args) {
        createTable();
        printSongs();
    }

}


