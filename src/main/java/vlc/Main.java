package vlc;

import org.jetbrains.annotations.NotNull;
import vlc.tracker.ExportInfo;
import vlc.tracker.Tracker;

import static vlc.util.Util.createTable;

public class Main {
    public static void main (@NotNull String[] args) throws Exception {
        createTable();
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
