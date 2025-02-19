import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)

            //Koin
            val koinVersion = "4.0.2"
            //implementation("io.insert-koin:koin-core:$koinVersion")
            implementation("io.insert-koin:koin-compose:$koinVersion")
            //implementation("io.insert-koin:koin-core-viewmodel:$koinVersion")
            implementation("io.insert-koin:koin-compose-viewmodel:$koinVersion")

            //Aurora
            val auroraVersion = "1.3.0"
            implementation("org.pushing-pixels:aurora-theming:$auroraVersion")
            implementation("org.pushing-pixels:aurora-component:$auroraVersion")
            implementation("org.pushing-pixels:aurora-window:$auroraVersion")

            implementation("com.darkrockstudios:mpfilepicker:3.1.0")

            //Ktor for websockets
            val ktorVersion = "3.1.0"
            implementation("io.ktor:ktor-server-core:$ktorVersion")
            implementation("io.ktor:ktor-server-netty:$ktorVersion")
            implementation("io.ktor:ktor-server-websockets:$ktorVersion")

            implementation("com.github.bhlangonijr:chesslib:1.3.4")
        }
    }
}


compose.desktop {
    application {
        mainClass = "com.hegesoftware.gentlefish.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.hegesoftware.gentlefish"
            packageVersion = "1.0.0"
        }
    }
}
