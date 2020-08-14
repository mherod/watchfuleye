package com.example.app

import android.app.Application
import com.myunidays.watchfuleye.WatchfulEye

class WatchfulEyeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        WatchfulEye.install(this)
    }

    override fun onTerminate() {
        WatchfulEye.uninstall(this)
        super.onTerminate()
    }
}