package com.yurcha.mybtctracker.timber.conf

import timber.log.Timber

fun timberConf() {
    Timber.uprootAll()
    Timber.plant(Timber.DebugTree())
}