package viewmodels

import UiState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RootViewmodel() : ViewModel() {

    var uiState by mutableStateOf(UiState.Onboarding as UiState)
        private set

    fun onFileBrowserButtonPress() = {
        uiState = UiState.FileBrowser
    }
    fun onStockfishPathChange() = { path: String? ->
        println(path)
    }
}