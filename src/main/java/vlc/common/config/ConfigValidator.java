package vlc.common.config;

public class ConfigValidator {

    public static void validate(Config c) {

        if(c.logsCap < 0)
            throw new IllegalArgumentException("logsCap cannot be negative");

        if(!c.dbType.equalsIgnoreCase("local")
            && !c.dbType.equalsIgnoreCase("remote"))
            throw new IllegalArgumentException("dbType must be either 'local' or 'remote'");

        if(c.dbType.equalsIgnoreCase("remote") && c.dbPath.toString().isBlank())
            throw new IllegalArgumentException("dbPath cannot be empty when dbType is remote");



    }
}
