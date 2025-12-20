package vlc.util;

import vlc.logger.CrashedKt;
import vlc.logger.Log;

import static vlc.util.SQLUtil.createTable;
import static vlc.util.SongUtil.printSongs;

public class Util {

    public static void end(){
        Log.error("Check if VLC is started.");
        CrashedKt.setCRASHED(true);
    }

    public static void main (String[] args) {
        createTable();
        printSongs();
    }

}


