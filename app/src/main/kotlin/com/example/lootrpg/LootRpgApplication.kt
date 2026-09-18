package com.example.lootrpg

import android.app.Application
import com.example.lootrpg.di.AppContainer

class LootRpgApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}
