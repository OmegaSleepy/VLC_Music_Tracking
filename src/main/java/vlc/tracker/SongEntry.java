package vlc.tracker;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record SongEntry
        (String title, String artist, String album, String comment, int length, String status) {

    private static Map<String, String> map = Map.of("&#39;","'");

    public static final SongEntry EMPTY_SONG_RECORD = new SongEntry("","","","",0, "NOTHING");
    public static final SongEntry TITLE_SONG_RECORD = new SongEntry("title","artist","album","comment",0, "NOTHING");

    public static String fix(String that){
        for(String s: map.keySet()){
            that = that.replace(s,map.get(s));
        }
        return that;
    }

    @NotNull
    @Override
    public String toString () {
        return title + " by " + artist + " from " + album + " at " + comment + " (" + length + ")";
    }

}
