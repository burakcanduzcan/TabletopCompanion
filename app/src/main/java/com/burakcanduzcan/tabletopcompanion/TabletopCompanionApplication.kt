package com.burakcanduzcan.tabletopcompanion

import android.app.Application
import timber.log.Timber

class TabletopCompanionApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initTimber()
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }
}