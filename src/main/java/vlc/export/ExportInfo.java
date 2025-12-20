package vlc.export;

import vlc.Main;
import vlc.logger.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static vlc.export.SqlSong.format;
import static vlc.util.SQLUtil.getConnection;
import static vlc.util.StringUtil.formatTimeHH;


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


    public static final Set<String> keys =  Set.of("${title}", "${artist}", "${album}", "${link}", "${linkString}","${length}", "${times}", "${playtime}", "${formattedTime}");


    public static String buildFormat(Map<String, String> map) {
        String value = format;
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

    public static void saveSongsToHTML() throws SQLException {
        Path outPath = Path.of("report.html");
        Path inPath = Path.of("template.txt");
        ArrayList<String> contents = new ArrayList<>();

        InputStream in = ExportInfo.class.getClassLoader().getResourceAsStream(inPath.toString());
        assert in != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        contents.add(reader.lines().collect(Collectors.joining("\n")));

        Connection connection = null;
        connection = getConnection();

        try {
            PreparedStatement statement = connection.prepareStatement(
                    """
                        SELECT * FROM music
                        where title != 'title'
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

                SqlSong song = new SqlSong(title,artist,album,link,length,times,playtime, format);

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
                    FROM music m;
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
            put("${times}", String.format("%3.3s",averageTimesPlayed));
            put("${playtime}", String.format("%3.3s",(averageTime)));
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

        Path out = Main.config.reportPath;
        Log.exec(out.toAbsolutePath().toString());

        try {
            Files.write(out, html.toString().getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static HashMap<String, String> getBestSongs() throws SQLException {
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement("""
                SELECT sum(playtime) as `Total`, artist FROM music
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
