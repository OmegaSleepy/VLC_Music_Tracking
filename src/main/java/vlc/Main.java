package vlc;

import org.jetbrains.annotations.NotNull;
import vlc.common.config.Config;
import vlc.common.config.ConfigLoader;
import vlc.export.ExportInfo;
import vlc.logger.Log;
import vlc.tracker.Tracker;
import vlc.util.Util;

import static vlc.common.config.ConfigLoader.loadOrCreate;
import static vlc.logger.LogFileHandler.*;
import static vlc.util.SQLUtil.createTable;

public class Main {

    public static Config config;

    public static void main (@NotNull String[] args) throws Exception {
        createFolders();
        config = ConfigLoader.load();
        Util.printConfig(config);
        loadOrCreate();

        long start = System.nanoTime();

        Log.MAX_LOGS = config.logsCap;
        cleanUp();

        createTable();

        if(args.length < 1){
            Log.error("There were no provided system args!");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "track" -> Tracker.main();
            case "export" -> ExportInfo.saveSongsToHTML();
            default -> {
                System.err.println("Unknown command");
                System.exit(1);
            }
        }

        var now = System.nanoTime();
        Log.info("System took " + (now-start*1e-9) + " seconds");

        if(config.saveLogs){
            saveLogFiles();
        }
    }

}
