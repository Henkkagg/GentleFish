package ui

import UiState
import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import ui.components.Filebrowser
import viewmodels.RootViewmodel

@Composable
fun Root(viewmodel: RootViewmodel = koinViewModel()) {

    when (viewmodel.uiState) {
        UiState.Onboarding -> Onboarding(onButtonPress = viewmodel.onFileBrowserButtonPress())

        UiState.FileBrowser -> Filebrowser(onFileSelect = viewmodel.onStockfishPathChange())

        UiState.Analysis -> {}

    }
}