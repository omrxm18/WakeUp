package com.omrxm18.wakeup

import android.app.Application
import android.provider.Settings
import android.text.TextUtils

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: App
        private set
    }
}
