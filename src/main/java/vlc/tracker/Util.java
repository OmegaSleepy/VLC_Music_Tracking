package vlc.tracker;

import vlc.logger.CrashedKt;
import vlc.logger.Log;

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

    public static String getFormatedResult(int value){
        if(value < 10){
            return "0" + value;
        }
        return String.valueOf(value);
    }

    public static String formatTimeHH (String value) {
        int seconds = (int) Double.parseDouble(value);

        StringBuilder format = new StringBuilder();

        format.append((seconds / 3600));
        format.append(":");
        format.append(getFormatedResult((seconds / 60) % 60));
        format.append(":");
        format.append(getFormatedResult(seconds % 60));

        return format.toString();
    }

    public static String formatTimeMM (String value) {
        int seconds = (int) Double.parseDouble(value);
        StringBuilder format = new StringBuilder();

        format.append((seconds / 60) % 60);
        format.append(":");
        format.append(getFormatedResult(seconds % 60));

        return format.toString();
    }

    public static void end(Exception e){
        Log.error("Check if VLC is started.");
        CrashedKt.setCRASHED(true);
    }

    public static boolean isValid (SongEntry songEntry) {
        return songEntry.equals(SongEntry.EMPTY_SONG_RECORD) || songEntry.equals(SongEntry.TITLE_SONG_RECORD);
    }

    public static void printSongs(){

//        SqlConnection connection = new SqlConnection(Path.of("credentials.txt"));

//        Log.logSelect.accept(Query.fromString("SELECT * FROM musicindex.musicspy " +
//                "where title != \"title\" and title is not null " +
//                "order by playtime desc;", connection));
    }

}


