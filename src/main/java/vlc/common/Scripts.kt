package vlc.common

import java.nio.file.Path

fun openVLC() {
    val dir = Path.of("C:/Program Files/VideoLan/VLC/vlc.exe")

    val args = ArrayList<String>()
    args.add(dir.toString())

    val pp = ProcessBuilder(args)

    pp.start()
}

