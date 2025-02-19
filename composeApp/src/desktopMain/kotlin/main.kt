import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import di.serviceModule
import di.viewmodelModule
import org.koin.compose.KoinApplication
import org.pushingpixels.aurora.theming.graphiteElectricSkin
import org.pushingpixels.aurora.theming.greenMagicSkin
import org.pushingpixels.aurora.window.AuroraWindow
import org.pushingpixels.aurora.window.auroraApplication
import ui.Root

fun main() = auroraApplication {

    AuroraWindow(
        onCloseRequest = ::exitApplication,
        title = "GentleFish",
        skin = greenMagicSkin(),
        state = WindowState(size = DpSize(400.dp, 300.dp),),
        resizable = false
    ) {
        KoinApplication(application = {
            modules(listOf(serviceModule, viewmodelModule))
        }) {

            Root()
        }
    }
}