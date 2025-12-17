package vlc.tracker;

import log.Log;
import log.LogFileHandler;
import vlc.common.ScriptsKt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static vlc.tracker.Util.*;

public class Tracker {

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/","root","password");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static final int MINIMAL_ATTENTION = 20; //seconds

    public static ArrayList<SongEntry> songEntries = new ArrayList<>();

    public static void addToDB (SongEntry songEntry, int time) throws SQLException {

        var con = getConnection();

        if(isValid(songEntry)) return;

        StringBuilder statement = new StringBuilder();

        statement.append("insert into musicindex.musicSpy value(%s,%s,%s,%s,%s,1,0) ".formatted(
                quote(songEntry.title()),
                quote(songEntry.artist()),
                quote(songEntry.album()),
                quote(songEntry.comment()),
                songEntry.length()
        ));

        int change = 1;
        if(time/1000 < MINIMAL_ATTENTION) change = 0;
        System.out.println(time/1000);

        statement.append("on DUPLICATE KEY UPDATE timesSeen = timesSeen+%d;".formatted(change));

        String query = statement.toString();

        PreparedStatement preparedStatement = con.prepareStatement(query);

        Log.exec(query);
        preparedStatement.execute();
        Log.exec("Logged songEntry: %s".formatted(songEntry));

        con.close();

    }

    public static void addTime(SongEntry songEntry, double time) throws SQLException {
        var con = getConnection();

        if(isValid(songEntry)) return;

        //ms -> s
        time /= 1000;

        var formatedTime = Math.ceil(time);

        if(time < MINIMAL_ATTENTION) return;

        String query = "update musicIndex.musicSpy " +
                "set playtime = playtime + %s ".formatted(formatedTime) +
                "where title = %s;".formatted(quote(songEntry.title()));

        PreparedStatement statement = con.prepareStatement(query);

        Log.exec(query);

        statement.execute();

        con.close();

    }

    public static void main() throws Exception {

        long start = System.nanoTime();

        Log.MAX_LOGS = 16;
        LogFileHandler.cleanUp();

        SongEntry previous = SongEntry.EMPTY_SONG_RECORD;
        SongEntry current = SongEntry.EMPTY_SONG_RECORD;

        long timeStep = (long) 1e3;
        double timeListenedToTheSong = 0;

        while (songEntries.size() < 1000) {
            
            try{
                current = VLCStatus.getCurrentSong();
            } catch (Exception e) { //meaning VLC if offline
                addTime(previous, timeListenedToTheSong);
//                printSongs();

                if(Util.getUserInput(
                        "\nEnter any value to open vlc, none to crash"
                ).isBlank()) Util.end(e);

                ScriptsKt.openVLC();
            }

            if (!previous.equals(current)) {
                System.out.println();

                addTime(previous, timeListenedToTheSong);

                songEntries.add(current);
                addToDB(current, (int) timeListenedToTheSong);

                timeListenedToTheSong = 0;
                previous = current;
            }

            Thread.sleep(timeStep);

            timeListenedToTheSong = printAndUpdateTime(current, timeListenedToTheSong, timeStep);

        }

        printSongs();

        Log.info("End of program");
        Log.info("Program took %f seconds to execute".formatted((double)(System.nanoTime() - start) * 1.0E-9));
        LogFileHandler.saveLogFiles();

    }

    private static double printAndUpdateTime (SongEntry current, double timeListenedToTheSong, long timeStep) {

        if(!current.status().equals("paused"))
            timeListenedToTheSong += timeStep;

        System.out.printf("\rPlayed for %s sec", timeListenedToTheSong /1000);

        if(current.status().equals("paused"))
            System.out.print(". Paused");

        return timeListenedToTheSong;
    }

}