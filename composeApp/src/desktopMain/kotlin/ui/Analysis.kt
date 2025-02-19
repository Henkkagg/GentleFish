package ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import org.pushingpixels.aurora.component.model.Command
import org.pushingpixels.aurora.component.projection.CommandButtonProjection
import ui.components.SmallHorizontalSpacer
import viewmodels.RootViewmodel

@Composable
fun Analysis(viewmodel: RootViewmodel = koinViewModel()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val gameModeText = remember(viewmodel.losingMode) { if(viewmodel.losingMode) "losing" else "winning" }
            Text("Current gamemode: $gameModeText", fontSize = 24.sp)

            SmallHorizontalSpacer()

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val modifier = remember { Modifier.fillMaxSize().weight(1f) }
                CommandButtonProjection(
                    contentModel = Command(
                        text = "Losing mode",
                        isActionToggle = viewmodel.losingMode,
                        isActionEnabled = !viewmodel.losingMode,
                        action = { viewmodel.onLosingModeChange(true) }
                    )
                ).project(modifier)
                CommandButtonProjection(
                    contentModel = Command(
                        text = "Winning mode",
                        isActionToggle = !viewmodel.losingMode,
                        isActionEnabled = viewmodel.losingMode,
                        action = { viewmodel.onLosingModeChange(false) }
                    )
                ).project(modifier)
            }
        }
}