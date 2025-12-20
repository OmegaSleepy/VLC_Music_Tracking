package vlc.tracker;

import static vlc.util.StringUtil.formatTimeMM;

record SqlSong (String title, String artist, String album, String link, String length, String times, String playtime, String formattedTime) {
    public String toHTMLTable(){

        String value = format;

        value = value.replace("${title}", title);
        value = value.replace("${artist}", artist);
        value = value.replace("${album}", album);
        value = value.replace("${link}", link);
        value = value.replace("${linkString}", "On Youtube.com");
        value = value.replace("${length}", formatTimeMM(length));
        value = value.replace("${times}", times);
        value = value.replace("${playtime}", playtime);
        value = value.replace("${formattedTime}", formattedTime);

        return value;
    }

    static String format = """
                <tr>
                    <td>${title}</td>
                    <td>${artist}</td>
                    <td>${album}</td>
                    <td><a href = "${link}"> ${linkString} </a></td>
                    <td>${length}</td>
                    <td>${times}</td>
                    <td>${playtime}</td>
                    <td>${formattedTime}</td>
                </tr>
                """;
}