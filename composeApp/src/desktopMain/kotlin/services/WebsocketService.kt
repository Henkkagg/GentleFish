package services

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.security.KeyStore

class WebsocketService {

    private lateinit var websocketSession: DefaultWebSocketSession

    fun start(): Flow<String> {

        val serverFlow = MutableSharedFlow<String>()

        embeddedServer(
            factory = Netty,
            configure = { tlsConfig() }
        ) {
            install(WebSockets)

            routing {
                webSocket("/gentlefish") {

                    println("WSS connection from: ${call.request.origin.remoteAddress}")
                    websocketSession = this

                    incoming.receiveAsFlow()
                        .filterIsInstance<Frame.Text>()
                        .mapNotNull { it.readText() }
                        .collect { message ->
                            println("Got message: $message")
                            serverFlow.emit(message)
                        }

                }
            }
        }.start(wait = false)

        return serverFlow
    }

    suspend fun sendMessage(message: String) {
        websocketSession.send(Frame.Text(message))
        println("Sending message: $message")
    }

    private fun ApplicationEngine.Configuration.tlsConfig() {

        val keyStoreFile = File("../keystore.jks")
        if (!keyStoreFile.exists()) {
            throw IllegalStateException("Keystore file not found: ${keyStoreFile.absolutePath}")
        }

        sslConnector(
            keyStore = KeyStore.getInstance(keyStoreFile, "gentlefish".toCharArray()),
            keyAlias = "gentlefish",
            keyStorePassword = { "gentlefish".toCharArray() },
            privateKeyPassword = { "gentlefish".toCharArray() }
        ) {
            host = "0.0.0.0"
            port = 8443
        }
    }
}