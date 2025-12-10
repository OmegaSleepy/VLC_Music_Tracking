package vlc.tracker;

import sql.CrashUtil;
import sql.Log;
import sql.Query;

import java.util.Scanner;

public class Util {

    public static String quote (String s) {
        return "\"" + s + "\"";
    }

    public static String getUserInput(String message){

        Scanner scanner = new Scanner(System.in);

        System.out.print(message + ": ");

        return scanner.nextLine();

    }

    public static void end(Exception e){
        Log.error("Check if VLC is started: ");
        CrashUtil.crash(e);
    }

    public static boolean isValid (Song song) {
        return song.equals(Song.EMPTY_SONG) || song.equals(Song.TITLE_SONG);
    }

    public static void printSongs(){
        Log.logSelect.accept(Query.getResult("SELECT * FROM musicindex.musicspy " +
                "where title != \"title\" and title is not null " +
                "order by playtime desc;"));
    }

}
