package vlc.util;

import vlc.common.config.Config;
import vlc.logger.CrashedKt;
import vlc.logger.Log;

import static vlc.util.SQLUtil.createTable;
import static vlc.util.SongUtil.printSongs;

public class Util {

    public static void end(){
        Log.error("Check if VLC is started.");
        CrashedKt.setCRASHED(true);
    }

    public static void printConfig(Config config){
        config.getAsStringList().forEach((key,value) -> {
            Log.info(key + " | " + value);
        });
    }

}


