package services

import com.github.bhlangonijr.chesslib.Board
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlin.math.absoluteValue

class StockfishService {
    private lateinit var process: Process
    private lateinit var writer: BufferedWriter
    private lateinit var reader: BufferedReader
    private lateinit var lastLine: String
    val proposedMoveFlow = MutableSharedFlow<String>()
    private var isProcessing = false
    private var processingJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun start(stockfishBinaryPath: String): Boolean {

        runCatching {
            process = ProcessBuilder(stockfishBinaryPath).start()
        }.onFailure { return false }

        writer = BufferedWriter(OutputStreamWriter(process.outputStream))
        reader = BufferedReader(InputStreamReader(process.inputStream))

        //At this point some program is running. Making sure it is Stockfish
        val firstLine = reader.readLine()
        val stockfishHasStarted = firstLine.startsWith("Stockfish 17")
        if (!stockfishHasStarted) {
            process.destroy()
            return false
        }

        //Initializing Stockfish
        sendCommand("uci")

        for (line in generateSequence { reader.readLine() }) {
            lastLine = line
            if (line == "uciok") break
        }

        return true
    }

    suspend fun updatePosition(fen: String, losingMode: Boolean){

        //Often times the board position is updated before proposed move has been calculated for previous position
        //In that case we should stop the unnecessary calculations to be able to start new ones immediately
        if (isProcessing) {
            sendCommand("stop")
        }
        processingJob?.let {
            sendCommand("stop")
            isProcessing = false
            it.join()
        }

        processingJob = coroutineScope.launch {
            isProcessing = true

            val depth = 15

            if (losingMode) {
                sendCommand("position fen $fen")
                sendCommand("setoption name MultiPV value 5")
                sendCommand("go depth $depth")
            } else {
                sendCommand("position fen $fen")
                sendCommand("go depth $depth")
            }

            //Used for losingMode
            var correctDepth = false
            val moveList = mutableListOf<Pair<String, Int>>()

            for (line in generateSequence { reader.readLine() }) {
                if (line.startsWith("bestmove")) {
                    if (isProcessing && !losingMode) proposedMoveFlow.emit(line.split(" ")[1])
                    break
                }

                if (losingMode) {
                    if (line.contains("depth $depth")) correctDepth = true

                    if (correctDepth) {
                        val parts = line.split(" ")
                        val score = parts[9].toInt()
                        val move = parts[21]

                        moveList.add(move to score)
                    }
                }
            }

            if (losingMode) {
                moveList.sortByDescending { it.second }

                //Selects move which loses at least by 0,5 points if possible...
                var losingMove = moveList.firstOrNull { it.second <= -50 } ?: moveList.last()

                //...but if the move is completely losing, reject it choose the most equalizing move
                if (losingMove.second <= -300) {
                    moveList.sortBy { it.second.absoluteValue }
                    losingMove = moveList.first()
                }

                if (isProcessing) proposedMoveFlow.emit(losingMove.first)
            }

            isProcessing = false
        }
    }

    private fun sendCommand(command: String) {
        writer.write("$command\n")
        writer.flush()
    }

    fun convertPGNToFEN(pgn: String): String? {

        //Client JS sends moves in PGN format wrapped with "". Dropping last character to work around it
        val moveList = pgn.dropLast(1).split(" ").filter { !it.contains(".") }
        val board = Board()
        println(moveList)

        runCatching {
            moveList.map { move ->
                board.doMove(move)
            }
        }.onFailure { return null }

        return board.fen
    }
}