package vlc.util;

import java.util.Scanner;

public class StringUtil {

    public static String capitalizeString(String string){
        string = string.toLowerCase();
        string = string.substring(0, 1).toUpperCase() + string.substring(1);
        return string;
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
}
