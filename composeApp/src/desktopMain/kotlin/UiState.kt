sealed class UiState {
    data class Onboarding(val failureLaunchingStockfish: Boolean = false) : UiState()
    object FileBrowser : UiState()
    object Analysis : UiState()
}