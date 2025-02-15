import java.io.File

object DataTools {

    fun getStockfishPathIfExists(): String? {

        val currentPath = System.getProperty("user.dir")
        println("Current path: $currentPath")

        val stockfishPath = runCatching {
            File("$currentPath\\stockfish_path.cfg").readText()
        }.getOrNull()

        return stockfishPath
    }
}