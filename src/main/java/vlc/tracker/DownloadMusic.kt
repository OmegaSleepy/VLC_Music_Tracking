package vlc.tracker

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path

fun runPython(args: Array<String>, scriptPath: Path) {
    val pythonPath =
        "C:/Users/THEBEAST/AppData/Local/Programs/Python/Python310/python.exe"

    val scriptPath = scriptPath.toAbsolutePath().toString()

    val command = ArrayList<String>()
    command.add(pythonPath)
    command.add(scriptPath)
    command.addAll(args.toList())   // ✅ FIXED

    val pb = ProcessBuilder(command)
        .directory(Path.of("C:/Users/THEBEAST/Music").toFile())
        .redirectErrorStream(true)

    val process = pb.start()

    BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
        reader.forEachLine { println(it) }
    }

    val exitCode = process.waitFor()
    println("Python script finished with exit code $exitCode")
}
