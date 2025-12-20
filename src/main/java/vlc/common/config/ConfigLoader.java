package vlc.common.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import static vlc.util.StringUtil.capitalizeString;

public class ConfigLoader {

    private static final Path PATH = Path.of("settings.properties");

    public static Config loadOrCreate() throws Exception{
        if(Files.notExists(PATH)){
            createDefault();
        }

        return ConfigLoader.load();
    }

    private static void createDefault() throws Exception {

        Properties p = new Properties();

        p.setProperty("path.of.VLC", "C:/Program Files/VideoLan/VLC/vlc.exe");
        p.setProperty("db.type", "local");
        p.setProperty("path.of.DB","");

        p.setProperty("saveLogs","true");
        p.setProperty("logsCap", "8");

        p.setProperty("minimalAttention", "20");

        p.setProperty("vlcWebLocation","http://localhost:8080/requests/status.xml");

        try (var out = Files.newOutputStream(PATH)) {
            p.store(out, "settings");
        }
    }

    public static Config load() throws Exception{
        Properties p = new Properties();

        try(var in = Files.newInputStream(PATH)){
            p.load(in);
        }

        Config c = new Config();

        c.vlcPath = getPath(p, "path.of.VLC", "C:/Program Files/VideoLan/VLC/vlc.exe","");
        c.dbType = get(p, "db.type", "local");
        c.dbPath = getPath(p,"path.of.DB","","musicspy.db");
        c.reportPath = getPath(p,"path.of.REPORT","desktop","report.html");

        c.saveLogs = getBool(p, "saveLogs", true);
        c.logsCap = getInt(p, "logsCap", 8);

        c.minimalAttention = getInt(p, "minimalAttention", 20);

        c.vlcWebLocation = get(p,"vlcWebLocation","http://localhost:8080/requests/status.xml");

        ConfigValidator.validate(c);
        return c;

    }

    private static String get (Properties p, String key, String def) {
        return  p.getProperty(key,def);
    }

    private static int getInt(Properties p, String key, int def) {
        try {
            return Integer.parseInt(p.getProperty(key, String.valueOf(def)));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static boolean getBool(Properties p, String key, boolean def) {
        return Boolean.parseBoolean(p.getProperty(key, String.valueOf(def)));
    }

    private static final List<String> LIST_OF_KNOWN_FOLDERS
            = List.of("desktop","documents","downloads");

    private static Path getPath(Properties p, String key, String def, String fileName) {
        if(LIST_OF_KNOWN_FOLDERS.contains(p.getProperty(key))){
            String userHome = System.getProperty("user.home");
            return Paths.get(userHome, capitalizeString(p.getProperty(key)), fileName);
        }

        try {
            return Paths.get(p.getProperty(key, def));
        } catch (IllegalArgumentException e) {
            return Paths.get(def);
        }

    }




}
