package com.e.myapplication

import android.app.Application
import android.content.Context
import okhttp3.internal.Internal.instance

class MyApplication : Application() {

    init {
        instance = this
    }

    companion object {
        lateinit var instance : MyApplication
        fun ApplicationContext() : Context {
            return instance.applicationContext
        }
    }
}