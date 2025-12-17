package vlc.tracker;

import common.CrashUtil;
import log.Log;
import sql.SqlConnection;
import sql.query.Query;

import java.nio.file.Path;
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

    public static boolean isValid (SongEntry songEntry) {
        return songEntry.equals(SongEntry.EMPTY_SONG_RECORD) || songEntry.equals(SongEntry.TITLE_SONG_RECORD);
    }

    public static void printSongs(){

        SqlConnection connection = new SqlConnection(Path.of("credentials.txt"));

        Log.logSelect.accept(Query.fromString("SELECT * FROM musicindex.musicspy " +
                "where title != \"title\" and title is not null " +
                "order by playtime desc;", connection));
    }

}


