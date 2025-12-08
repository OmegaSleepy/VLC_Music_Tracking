package vlc.tracker;

import sql.*;

import java.util.ArrayList;

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

        Query.getResult(statement.toString());

        Log.exec("Logged song: %s".formatted(song));

    }

    private static void addTime(Song song, double time){
        if(song.equals(Song.EMPTY_SONG)) return;

        //ms -> s
        time /= 1000;

        var formatedTime = Math.ceil(time);

        String statement = "update musicSpy " +
                "set playtime = playtime + %s ".formatted(formatedTime) +
                "where title = %s;".formatted(quote(song.title));
        Query.getResult(statement);

    }

    private static void printSongs(){
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

            System.out.printf("\rPlayed for %s seconds", timeListenedToTheSong/1000);

            if(current.status.equals("paused")){
                System.out.print(". Paused");
            }

        }

        printSongs();

        Script.end(start, System.nanoTime());

    }

}
