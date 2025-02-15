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
        skin = greenMagicSkin()
    ) {
        KoinApplication(application = {
            modules(viewmodelModule)
        }) {

            Root()
        }
    }
}