package di

import org.koin.dsl.module
import services.StockfishService
import services.WebsocketService

val serviceModule = module {
    single { WebsocketService() }
    single { StockfishService() }
}