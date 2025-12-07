package vlc;

import sql.Log;
import sql.Query;
import sql.Script;
import sql.Settings;

import java.util.ArrayList;
import java.util.Queue;

public class Main {
    static ArrayList<Song> songs = new ArrayList<>();

    private static String quote (String s) {
        return "\"" + s + "\"";
    }

    private static void addToDB (Song song) {

        if(song.equals(Song.EMPTY_SONG)) return;

        StringBuilder statement = new StringBuilder();
        statement.append("insert into musicSpy value(%s,%s,%s,%s,%s,1,0) ".formatted(
                quote(song.title),
                quote(song.artist),
                quote(song.album),
                quote(song.comment),
                song.length
        ));
        statement.append("on DUPLICATE KEY UPDATE timesSeen = timesSeen+1;");

        Settings.logQueries = false;
        Query.getResult(statement.toString());
        Settings.logQueries = true;

        Log.exec("Logged song: %s".formatted(song));

    }

    private static void addTime(Song song, int time){
        if(song.equals(Song.EMPTY_SONG)) return;

        //ms -> s
        time /= 1000;

        String statement = "update musicSpy " +
                "set playtime = playtime + %s ".formatted(time) +
                "where title = %s;".formatted(quote(song.title));
        Query.getResult(statement);

    }

    public static void main (String[] args) throws Exception {
        long start = System.nanoTime();
        Query.getResult("use musicindex");

        Song previous = Song.EMPTY_SONG;
        Song current;


        long timeStep = (long) 2e3;
        int timeListenedToTheSong = 0;

        while (songs.size() < 1000) {

            current = VLCStatus.getCurrentSong();

            if (!previous.equals(current)) {
                System.out.println();

                addTime(previous,timeListenedToTheSong);
                timeListenedToTheSong = 0;

                songs.add(current);
                addToDB(current);

                previous = current;
            }

            Thread.sleep(timeStep);
            timeListenedToTheSong += (int) timeStep;
            System.out.printf("\rPlayed for %s seconds", timeListenedToTheSong/1000);

        }

        Log.logSelect.accept(Query.getResult("SELECT * FROM musicindex.musicspy;"));

        Script.end(start, System.nanoTime());

    }

}
