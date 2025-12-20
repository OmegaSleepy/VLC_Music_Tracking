package vlc.common

import vlc.Main

fun openVLC() {
    val dir = Main.config.vlcPath;

    val args = ArrayList<String>()
    args.add(dir.toString())

    val pp = ProcessBuilder(args)

    pp.start()
}

