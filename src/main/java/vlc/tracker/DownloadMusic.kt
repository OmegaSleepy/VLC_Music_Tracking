package vlc.tracker

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path

const val PYTHON_PATH = "C:/Users/THEBEAST/AppData/Local/Programs/Python/Python310/python.exe"
const val MUSIC_DIRECTORY = "C:/Users/THEBEAST/music"

fun runPython(args: Array<String>, scriptPath: Path) {

    val scriptPath = scriptPath.toAbsolutePath().toString()

    val command = ArrayList<String>()
    command.add(PYTHON_PATH)
    command.add(scriptPath)
    command.addAll(args.toList())

    val pb = ProcessBuilder(command)
        .directory(Path.of(MUSIC_DIRECTORY).toFile())
        .redirectErrorStream(true)

    val process = pb.start()

    BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
        reader.forEachLine { println(it) }
    }

    val exitCode = process.waitFor()
    println("Python script finished with exit code $exitCode")
}
