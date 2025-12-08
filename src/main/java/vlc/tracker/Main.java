package vlc.tracker;

import sql.*;

import java.util.ArrayList;

import static vlc.tracker.Util.quote;

public class Main {

    public static final double ATTENTION_RATE = 0.3;

    public static ArrayList<Song> songs = new ArrayList<>();

    public static boolean isAttendant (int time, Song song){
        if(time < song.length*ATTENTION_RATE) {
            Log.warn("Attention too low -%s%%, must be at least %s%%!".formatted((double) time / song.length, ATTENTION_RATE));
            return false;
        }
        return true;
    }

    public static void addToDB (Song song, int time) {

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

        Query.getResult(statement.toString());

        Log.exec("Logged song: %s".formatted(song));

    }

    public static void addTime(Song song, double time){


        if(song.equals(Song.EMPTY_SONG)) return;

        //ms -> s
        time /= 1000;

        var formatedTime = Math.ceil(time);

        String statement = "update musicSpy " +
                "set playtime = playtime + %s ".formatted(formatedTime) +
                "where title = %s;".formatted(quote(song.title));
        Query.getResult(statement);

    }

    public static void printSongs(){
        Log.logSelect.accept(Query.getResult("SELECT * FROM musicindex.musicspy " +
                "where title != \"title\" and title is not null " +
                "order by playtime desc;"));
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
            } catch (Exception e) {
                addTime(previous, timeListenedToTheSong);
                printSongs();

                Log.error("Check if VLC is started");
                CrashUtil.crash(e);
            }

            if (!previous.equals(current)) {
                System.out.println();

                addTime(previous,timeListenedToTheSong);
                timeListenedToTheSong = 0;

                songs.add(current);
                addToDB(current);

                previous = current;
            }

            Thread.sleep(timeStep);

            if(!current.status.equals("paused"))
                timeListenedToTheSong += timeStep;

            System.out.printf("\rPlayed for %s sec", timeListenedToTheSong/1000);

            if(current.status.equals("paused"))
                System.out.print(". Paused");

        }

        printSongs();

        Script.end(start, System.nanoTime());

    }

}