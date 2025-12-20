package vlc;

import org.jetbrains.annotations.NotNull;
import vlc.common.config.Config;
import vlc.common.config.ConfigLoader;
import vlc.tracker.ExportInfo;
import vlc.tracker.Tracker;

import static vlc.common.config.ConfigLoader.loadOrCreate;
import static vlc.util.SQLUtil.createTable;

public class Main {

    public static Config config;

    public static void main (@NotNull String[] args) throws Exception {

        createTable();
        loadOrCreate();
        config = ConfigLoader.load();

        switch (args[0].toLowerCase()) {
            case "track" -> Tracker.main();
            case "export" -> ExportInfo.saveSongsToHTML();
            default -> {
                System.err.println("Unknown command");
                System.exit(1);
            }
        }
    }

}
