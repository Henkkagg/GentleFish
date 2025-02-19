package ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.pushingpixels.aurora.component.model.Command
import org.pushingpixels.aurora.component.projection.CommandButtonProjection
import ui.components.LinkText
import ui.components.SmallHorizontalSpacer

@Composable
fun Onboarding(showFailure: Boolean, onButtonPress: () -> Unit) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showFailure) {
            Text(
                text = "There was problem loading Stockfish",
                color = Color.Red
            )
            SmallHorizontalSpacer()
        }
        CommandButtonProjection(
            contentModel = Command(
                text = "Locate Stockfish Binary",
                action = onButtonPress
            )
        ).project()
        SmallHorizontalSpacer()
        Row {
            Text("Don't have Stockfish yet? ")
            LinkText(
                text = "Download it here",
                url = "https://stockfishchess.org/download/"
            )
        }
    }
}