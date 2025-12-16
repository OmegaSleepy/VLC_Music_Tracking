package vlc.tracker;

import sql.SqlConnection;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import static vlc.tracker.SqlSong.format;


public class ExportInfo {

    static int totalSongs = 0;
    static int totalArtists = 0;

    static int totalAlbums = 0;
    static int totalTimesPlayed = 0; //TotalLength

    static double averageTimesPlayed = 0; //AverageTimesSeen
    static int totalTime = 0; //TotalPlaytime
    static double averageTime = 0; //AveragePlaytime

    static int totalLength = 0; //TotalTimesSeen
    static double averageLength = 0; //AverageLength
    
    public static String getFormatedResult(int value){
        if(value < 10){
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

    public static final Set<String> keys =  Set.of("${title}", "${artist}", "${album}", "${link}", "${linkString}","${length}", "${times}", "${playtime}", "${formattedTime}");

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

    public static void saveSongsToHTML() throws IOException, SQLException {
        Path path = Path.of("report.html");

        ArrayList<String> contents = new ArrayList<>(Files.readAllLines(Path.of("template.txt")));

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/","root","password");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }




        try {
            PreparedStatement statement = connection.prepareStatement(
                    """
                        SELECT * FROM musicindex.musicspy
                        where title != "title" and artist not like "%Sān-Z%"
                        order by playtime desc;
                    """);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
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

            statement = connection.prepareStatement("""
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

            while (resultSet2.next()) {
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

        String values = buildFormat(new HashMap<>() {{
            put("${title}", String.valueOf(totalSongs));
            put("${artist}", String.valueOf(totalArtists));
            put("${album}", String.valueOf(totalAlbums));

            put("${length}", formatTimeHH(String.valueOf(totalLength)));
            put("${times}", String.valueOf(totalTimesPlayed));
            put("${playtime}", String.valueOf(totalTime));

            put("${formattedTime}", formatTimeHH(String.valueOf(totalTime)));
        }});

        contents.add(values);

        values = buildFormat(new HashMap<>() {{
            put("${length}", formatTimeHH(String.valueOf(averageLength)));
            put("${times}", String.format(String.valueOf(averageTimesPlayed)));
            put("${playtime}", (String.valueOf(averageTime)));
            put("${formattedTime}", formatTimeHH(String.valueOf(averageTime)));
        }});

        contents.add(values);

        contents.add("""
                    </table>
                
                </div>
                
                </body>
                </html>""");

        AtomicInteger totalTimeAtomic = new AtomicInteger(totalTime);
        AtomicInteger totalSongsAtomic = new AtomicInteger(totalSongs);

        StringBuilder html = new StringBuilder();

        var map = getBestSongs();

        contents.forEach(line -> {
            line = line.replace("${TotalTime}",  formatTimeHH(String.valueOf(totalTimeAtomic.get())));
            line =  line.replace("${TotalSongs}",  String.valueOf(totalSongsAtomic.get()));
            line = line.replace("${Date}", Calendar.getInstance().getTime().toString());
            line = line.replace("${VLCVersion}", "3.0.21 Vetinari");

            for(String str : map.keySet()) {
                line = line.replace(str, map.get(str));
            }

            html.append(line);
        });



        try {
            Files.write(path, html.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    static HashMap<String, String> getBestSongs() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/","root","password");
        PreparedStatement statement = connection.prepareStatement("""
                SELECT sum(playtime) as `Total`, artist FROM musicindex.musicspy
                group by artist
                order by `Total` desc
                """);
        ResultSet resultSet = statement.executeQuery();

        String value = "${TopI}";
        String name = "${TopIValue}";

        HashMap<String, String> map = new HashMap<>();

        int i = 0;
        while (resultSet.next()) {
            map.put(name.replace("I", String.valueOf(i)),resultSet.getString("Total"));
            map.put(value.replace("I", String.valueOf(i)),resultSet.getString("artist"));

            i++;
        }

        return map;

    }

}
