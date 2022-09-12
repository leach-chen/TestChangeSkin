package com.example.testchangeskin

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        TSKinManager.install(this)
    }

    companion object {
        @JvmStatic
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            private set
    }
}