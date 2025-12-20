package vlc.logger;

import java.time.format.DateTimeFormatter;

/**
 * Holds lookup values for the rest of the library which can be used its scope too.
 * **/
public class Settings {

    private Settings(){}

    /**
     * Used to stop coloring everything past the timestamp
     * **/
    public static String RESET = "\u001B[0m";
    /**
     * Used for coloring the timestamp of info operations
     * @see vlc.logger.Log#info(String message)
     * **/
    public static String GREEN = "\u001B[32m";
    /**
     * Used for coloring the timestamp of execution operations
     * @see vlc.logger.Log#exec(String message)
     * **/
    public static String BLUE = "\u001B[34m";
    /**
     * Used for coloring the timestamp of warn operations
     * @see vlc.logger.Log#warn(String message)
     * **/
    public static String YELLOW = "\u001B[33m";
    /**
     * Used for coloring the timestamp of error operations
     * @see vlc.logger.Log#error(String message)
     * **/
    public static String RED = "\u001B[31m";


    public static DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss:ms");
    public static DateTimeFormatter FILE = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");


}
