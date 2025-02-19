package ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.darkrockstudios.libraries.mpfilepicker.FilePicker

@Composable
fun Filebrowser(onFileSelect: (String?) -> Unit) {
    var showFilePicker by remember { mutableStateOf(true) }

    FilePicker(show = showFilePicker) { platformFile ->
        showFilePicker = false
        onFileSelect(platformFile?.path)
        // do something with the file
    }
}