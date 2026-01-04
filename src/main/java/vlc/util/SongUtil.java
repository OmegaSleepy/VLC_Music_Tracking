package vlc.util;

import vlc.logger.Log;
import vlc.tracker.SongEntry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static vlc.util.SQLUtil.getConnection;
import static vlc.util.SQLUtil.selectOperation;

public class SongUtil {

    public static boolean isValid (SongEntry songEntry) {
        return songEntry.equals(SongEntry.EMPTY_SONG_RECORD) || songEntry.equals(SongEntry.TITLE_SONG_RECORD);
    }



    public static void printSongs(){

        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("""
                SELECT title, artist, album, length, timesSeen as `Plays`, playtime as `Playtime` FROM music
                where title != 'title'
                order by `Playtime` desc
            """);

            Log.logSelect.accept(
                    selectOperation(preparedStatement)
            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
