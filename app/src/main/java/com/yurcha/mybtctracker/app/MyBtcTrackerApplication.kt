package com.yurcha.mybtctracker.app

import android.app.Application
import com.yurcha.mybtctracker.timber.conf.timberConf
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyBtcTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        timberConf()
    }
}
