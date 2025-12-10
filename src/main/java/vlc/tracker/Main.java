package vlc.tracker;

import sql.*;

import java.util.ArrayList;

import static vlc.tracker.Util.*;

public class Main {

    public static final int MINIMAL_ATTENTION = 20; //seconds

    public static ArrayList<Song> songs = new ArrayList<>();

    public static void addToDB (Song song, int time) {

        if(isValid(song)) return;

        StringBuilder statement = new StringBuilder();

        statement.append("insert into musicSpy value(%s,%s,%s,%s,%s,1,0) ".formatted(
                quote(song.title),
                quote(song.artist),
                quote(song.album),
                quote(song.comment),
                song.length
        ));

        int change = 1;
        if(time/1000 < MINIMAL_ATTENTION) change = 0;
        System.out.println(time/1000);

        statement.append("on DUPLICATE KEY UPDATE timesSeen = timesSeen+%d;".formatted(change));

        Query.getResult(statement.toString());

        Log.exec("Logged song: %s".formatted(song));

    }

    public static void addTime(Song song, double time){

        if(isValid(song)) return;

        //ms -> s
        time /= 1000;

        var formatedTime = Math.ceil(time);

        if(time < MINIMAL_ATTENTION) return;

        String statement = "update musicSpy " +
                "set playtime = playtime + %s ".formatted(formatedTime) +
                "where title = %s;".formatted(quote(song.title));
        Query.getResult(statement);

    }

    public static void main (String[] args) throws Exception {

        long start = System.nanoTime();
        Query.getResult("use musicindex");

        Log.MAX_LOGS = 16;
        Log.cleanUp();

        Song previous = Song.EMPTY_SONG;
        Song current = Song.EMPTY_SONG;

        long timeStep = (long) 1e3;
        double timeListenedToTheSong = 0;

        while (songs.size() < 1000) {
            
            try{
                current = VLCStatus.getCurrentSong();
            } catch (Exception e) { //meaning VLC if offline
                addTime(previous, timeListenedToTheSong);
                printSongs();

                if(Util.getUserInput(
                        "Enter any value to open vlc, none to crash"
                ).isBlank()) Util.end(e);

                ScriptsKt.openVLC();
            }

            if (!previous.equals(current)) {
                System.out.println();

                addTime(previous, timeListenedToTheSong);

                songs.add(current);
                addToDB(current, (int) timeListenedToTheSong);

                timeListenedToTheSong = 0;
                previous = current;
            }

            Thread.sleep(timeStep);

            timeListenedToTheSong = printAndUpdateTime(current, timeListenedToTheSong, timeStep);

        }

        printSongs();

        Script.end(start, System.nanoTime());

    }

    private static double printAndUpdateTime (Song current, double timeListenedToTheSong, long timeStep) {

        if(!current.status.equals("paused"))
            timeListenedToTheSong += timeStep;

        System.out.printf("\rPlayed for %s sec", timeListenedToTheSong /1000);

        if(current.status.equals("paused"))
            System.out.print(". Paused");

        return timeListenedToTheSong;
    }

}