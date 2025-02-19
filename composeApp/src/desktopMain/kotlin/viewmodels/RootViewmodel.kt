package viewmodels

import UiState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import services.StockfishService
import services.WebsocketService
import java.io.File

class RootViewmodel(
    private val stockfishService: StockfishService,
    private val websocketService: WebsocketService
) : ViewModel() {

    var uiState by mutableStateOf(UiState.Onboarding() as UiState)
        private set

    var losingMode by mutableStateOf(true)
        private set

    init {
        runCatching {
            val path = File("stockfish_path.cfg").readText()
            onStockfishPathChange(path)
        }
    }

    fun onFileBrowserButtonPress() {
        uiState = UiState.FileBrowser
    }

    fun onLosingModeChange(losingMode: Boolean) {
        this.losingMode = losingMode
    }

    fun onStockfishPathChange(path: String?) {
        if (path != null) {
            File("stockfish_path.cfg").writeText(path)
            val stockfishHasStarted = stockfishService.start(path)

            if (!stockfishHasStarted) {
                uiState = UiState.Onboarding(failureLaunchingStockfish = true)
                return
            } else {
                uiState = UiState.Analysis
            }

            viewModelScope.launch {
                websocketService.start().collect { movesInPGN ->
                    val fen = stockfishService.convertPGNToFEN(movesInPGN)
                    if (fen != null) stockfishService.updatePosition(fen, losingMode)
                }
            }
            viewModelScope.launch {
                stockfishService.proposedMoveFlow.collect { move ->
                    websocketService.sendMessage(move)
                }
            }
        }
    }
}