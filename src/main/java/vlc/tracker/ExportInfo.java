package vlc.tracker;

import sql.SqlConnection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static vlc.tracker.SqlSong.format;

public class ExportInfo {
    public static String getFormatedResult(int value){
        if(value <= 10){
            return "0" + value;
        }
        return String.valueOf(value);
    }

    public static String formatTimeHH (String value) {
        int seconds = (int) Double.parseDouble(value);

        StringBuilder format = new StringBuilder();

        format.append((seconds / 3600));
        format.append(":");
        format.append(getFormatedResult((seconds / 60) % 60));
        format.append(":");
        format.append(getFormatedResult(seconds % 60));

        return format.toString();
    }

    public static String formatTimeMM (String value) {
        int seconds = (int) Double.parseDouble(value);
        StringBuilder format = new StringBuilder();

        format.append((seconds / 60) % 60);
        format.append(":");
        format.append(getFormatedResult(seconds % 60));

        return format.toString();
    }

    public static final Set<String> keys =  Set.of("${title}", "${artist}", "${album}", "${link}",  "${length}", "${times}", "${playtime}", "${formattedTime}");


    public static String buildFormat(Map<String, String> map) {
        String value =  format;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            value = value.replace(entry.getKey(), entry.getValue());
        }

        for (String key: keys){
            if(value.contains(key)){
                value=value.replace(key,"");
            }
        }

        return value;

    }

    public static void saveSongsToHTML() throws IOException {
        Path path = Path.of("report.html");

        ArrayList<String> contents = new ArrayList<>(Files.readAllLines(Path.of("template.txt")));

        SqlConnection connection = new SqlConnection(Path.of("credentials.txt"));

        int totalSongs = 0;
        int totalArtists = 0;
        int totalAlbums = 0;

        int totalTimesPlayed = 0; //TotalLength
        double averageTimesPlayed = 0; //AverageTimesSeen

        int totalTime = 0; //TotalPlaytime
        double averageTime = 0; //AveragePlaytime

        int totalLength = 0; //TotalTimesSeen
        double averageLength = 0; //AverageLength


        try {
            PreparedStatement statement = connection.connection.prepareStatement("" +
                    "SELECT * FROM musicindex.musicspy " +
                    "order by playtime desc;");
            ResultSet resultSet = statement.executeQuery();

            for(;resultSet.next();){
                String title = resultSet.getString("title");
                String artist = resultSet.getString("artist");
                String album = resultSet.getString("album");
                String link = resultSet.getString("url");
                String length = resultSet.getString("length");
                String times = resultSet.getString("timesSeen");
                String playtime = resultSet.getString("playtime");

                String format = formatTimeHH(playtime);

                SqlSong song = new SqlSong(title,artist,album,link,length,times,playtime, format.toString());

                contents.add(song.toHTMLTable());
            }

            statement = connection.connection.prepareStatement("""
                    SELECT
                        COUNT(title) AS TotalTitles,
                        COUNT(DISTINCT artist) AS UniqueArtists,
                        COUNT(DISTINCT album) AS UniqueAlbums,
                        SUM(length) AS TotalLength,
                        SUM(timesSeen) AS TotalTimesSeen,
                        SUM(playtime) AS TotalPlaytime,
                        AVG(length) as AverageLength,
                        AVG(timesSeen) as AverageTimesSeen,
                        AVG(playtime) as AveragePlaytime
                    FROM musicindex.musicspy m;
                    """);

            ResultSet resultSet2 = statement.executeQuery();

            for(;resultSet2.next();){
                totalSongs = resultSet2.getInt("TotalTitles");
                totalArtists = resultSet2.getInt("UniqueArtists");
                totalAlbums = resultSet2.getInt("UniqueAlbums");

                totalTimesPlayed = resultSet2.getInt("TotalTimesSeen");
                averageTimesPlayed = resultSet2.getDouble("AverageTimesSeen");

                totalTime = resultSet2.getInt("TotalPlaytime");
                averageTime = resultSet2.getDouble("AveragePlaytime");

                totalLength = resultSet2.getInt("TotalLength");
                averageLength = resultSet2.getDouble("AverageLength");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        int finalTotalSongs = totalSongs;
        int finalTotalArtists = totalArtists;

        int finalTotalAlbums = totalAlbums;
        int finalTotalLength = totalLength;
        int finalTotalTimesPlayed = totalTimesPlayed;
        int finalTotalTime = totalTime;

        String values = buildFormat(new HashMap<>() {{
            put("${title}", String.valueOf(finalTotalSongs));
            put("${artist}", String.valueOf(finalTotalArtists));
            put("${album}", String.valueOf(finalTotalAlbums));

            put("${length}", formatTimeHH(String.valueOf(finalTotalLength)));
            put("${times}", String.valueOf(finalTotalTimesPlayed));
            put("${playtime}", String.valueOf(finalTotalTime));

            put("${formattedTime}", formatTimeHH(String.valueOf(finalTotalTime)));
        }});

        contents.add(values);

        double finalAverageTimesPlayed = averageTimesPlayed;
        double finalAverageLength = averageLength;
        double finalAverageTime = averageTime;

        values = buildFormat(new HashMap<>() {{
            put("${length}", formatTimeHH(String.valueOf(finalAverageLength)));
            put("${times}", String.format(String.valueOf(finalAverageTimesPlayed)));
            put("${playtime}", (String.valueOf(finalAverageTime)));
            put("${formattedTime}", formatTimeHH(String.valueOf(finalAverageTime)));
        }});

        contents.add(values);

        contents.add("""
                    </table>
                
                </div>
                
                </body>
                </html>""");

        try {
            Files.write(path, contents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
