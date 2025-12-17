package vlc;

import log.Log;
import log.LogFileHandler;
import org.jetbrains.annotations.NotNull;
import vlc.tracker.ExportInfo;
import vlc.tracker.Tracker;
import vlc.ui.DownloadUI;

public class Main {
    public static void main (@NotNull String[] args) throws Exception {
        LogFileHandler.saveLogFiles();
        switch (args[0].toLowerCase()) {
            case "download" -> DownloadUI.main();
            case "track" -> Tracker.main();
            case "export" -> ExportInfo.saveSongsToHTML();
            default -> {
                System.err.println("Unknown command");
                System.exit(1);
            }
        }
    }

}
