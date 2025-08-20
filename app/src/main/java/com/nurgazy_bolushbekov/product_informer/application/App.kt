package com.nurgazy_bolushbekov.product_informer.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {
    lateinit var connectionSettingsPrefRep: SettingsConnectionsPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        connectionSettingsPrefRep = SettingsConnectionsPreferencesRepository(applicationContext)
    }

    override fun onTerminate() {
        super.onTerminate()
        connectionSettingsPrefRep.cancelScope()
    }
}