package vlc.common.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    public Path vlcPath;
    public String dbType;
    public Path dbPath;
    public Path reportPath;

    public boolean saveLogs;
    public int logsCap;

    public int minimalAttention;

    public String vlcWebLocation;

    public Map<String, Object> getAsStringList(){
        Map<String, Object> config = new HashMap<>();
        config.put("VLC Path",vlcPath);
        config.put("dbType",dbType);
        config.put("dbPath",dbPath);
        config.put("Report Path",reportPath);
        config.put("Logs Cap",logsCap);
        config.put("minimalAttention",minimalAttention);
        config.put("vlc Web Location", vlcWebLocation);
        return config;
    }

}
