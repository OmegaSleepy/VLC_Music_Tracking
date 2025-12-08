package vlc.tracker

import sql.Query

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

fun main(args: Array<String>) {
    select()
}