package vlc.common

import vlc.Main

fun openVLC() {
    if (ProcessHandle.allProcesses()
            .anyMatch { it.info().command().orElse("").contains("vlc") }) {
        return
    }

    ProcessBuilder(Main.config.vlcPath.toString()).start()
}

