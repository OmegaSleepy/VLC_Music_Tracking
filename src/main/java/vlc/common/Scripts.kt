package vlc.common

import sql.Query
import java.nio.file.Path

fun createDBandTable(){

    Query.getResult("use musicindex;" +
            "DROP TABLE if exists musicspy;" +
            "CREATE TABLE `musicspy` (\n" +
            "  `title` varchar(64) NOT NULL,\n" +
            "  `artist` varchar(64) DEFAULT NULL,\n" +
            "  `album` varchar(64) DEFAULT NULL,\n" +
            "  `url` varchar(128) DEFAULT NULL,\n" +
            "  `length` int DEFAULT NULL,\n" +
            "  `timesSeen` int DEFAULT NULL,\n" +
            "  PRIMARY KEY (`title`)\n" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;\n")

}

fun select(){

    Query.getResult("SELECT * FROM musicindex.musicspy order by timesSeen desc;")
}

fun openVLC() {
    val dir = Path.of("C:/Program Files/VideoLan/VLC/vlc.exe")
    val commands = "C:/Users/THEBEAST/Music/STARSET/Album - DIVISIONS/MANIFEST.mp3"

    val args = ArrayList<String>()
    args.add(dir.toString())
    args.add(commands)

    val pp = ProcessBuilder(args)

    pp.start()
}

fun packageSingles(singles: List<String>):String {

    val builder = StringBuilder("-single{")
    for (single in singles) {
        builder.append(single).append(',')
    }
    builder.removeRange(builder.length - 1, builder.length - 1)
    builder.append("}")

    return builder.toString()
}

fun packageAlbums(albums: List<String>):String {
    val builder = StringBuilder("-albums{")
    for (album in albums) {
        builder.append(album).append(',')
    }
    builder.removeRange(builder.length - 1, builder.length - 1)
    builder.append("}")
    return builder.toString()
}

fun main(args: Array<String>) {
    val scriptPah = Path.of("DownloadMusic.py")
    runPython(arrayOf("-single{hWqLuSnyqbI}"), scriptPah)
}