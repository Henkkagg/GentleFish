package ui

import UiState
import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import ui.components.Filebrowser
import viewmodels.RootViewmodel

@Composable
fun Root(viewmodel: RootViewmodel = koinViewModel()) {

    when (val state = viewmodel.uiState) {
        is UiState.Onboarding -> Onboarding(
            showFailure = state.failureLaunchingStockfish,
            onButtonPress = { viewmodel.onFileBrowserButtonPress() })

        UiState.FileBrowser -> Filebrowser(onFileSelect = { viewmodel.onStockfishPathChange(it) })

        UiState.Analysis -> { Analysis() }

    }
}