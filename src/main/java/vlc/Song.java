package vlc;

import java.util.Collection;
import java.util.Objects;

public class Song {

    public String title;
    public String artist;
    public String album;
    public String comment;
    public String length;

    public Song (String title, String artist, String album, String comment, String length) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.comment = comment;
        this.length = length;
    }

//    public static <T extends Collection<String>> Song fromList(T list){
//        String [] values = list.toArray(new String[]{});
//
//        for(String s: values){
//            System.out.println(s);
//        }
//
//        return new Song(values[2],values[1],values[0],values[3]);
//    }

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
