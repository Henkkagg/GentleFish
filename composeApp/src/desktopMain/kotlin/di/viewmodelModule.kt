package di

import org.koin.dsl.module
import viewmodels.RootViewmodel

val viewmodelModule = module {
    single { RootViewmodel(get(), get()) }
}