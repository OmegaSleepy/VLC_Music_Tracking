package vlc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Song {

    private static Map<String, String> map = Map.of("&#39;","'");

    static final Song EMPTY_SONG = new Song("","","","",0);

    public String title;
    public String artist;
    public String album;
    public String comment;
    public int length;

    public Song (String title, String artist, String album, String comment, int length) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.comment = comment;
        this.length = length;
        fix();
    }

    private void fix(){
        for(String s: map.keySet()){
            title = title.replace(s,map.get(s));
            artist = artist.replace(s,map.get(s));
            album = album.replace(s,map.get(s));
        }
    }

    @Override
    public String toString () {
        return title + " by " + artist + " from " + album + " at " + comment + " (" + length + ")";
    }

    @Override
    public boolean equals (Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Song song = (Song) object;
        return Objects.equals(title, song.title)
                        && Objects.equals(artist, song.artist)
                        && Objects.equals(album, song.album)
                        && Objects.equals(comment, song.comment);
    }

    @Override
    public int hashCode () {
        return Objects.hash(title, artist, album, comment);
    }
}
