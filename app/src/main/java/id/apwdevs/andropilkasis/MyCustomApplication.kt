/*
 * Copyright @2019 by Alexzander Purwoko Widiantoro <purwoko908@gmail.com>
 * This prototype is used for testing, and educating about APIs
 * @author APWDevs
 *
 * Licensed under GNU GPLv3
 *
 * This module is provided by "AS IS" and if you want to take
 * a copy or modifying this module, you must include this @author
 * Thanks! Happy Coding!
 */

package id.apwdevs.andropilkasis

import android.app.Application
import com.androidnetworking.AndroidNetworking
import id.apwdevs.andropilkasis.di.AppComponent
import id.apwdevs.andropilkasis.di.DaggerAppComponent
import id.apwdevs.andropilkasis.module.BeepModule
import id.apwdevs.andropilkasis.module.PopulateSharedPreferences
import id.apwdevs.andropilkasis.module.ServerModule
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MyCustomApplication : Application() {

    val appComponent: AppComponent by lazy {
        PopulateSharedPreferences(applicationContext)
        DaggerAppComponent.factory().create(applicationContext)
    }

    @Inject
    lateinit var serverModule: ServerModule

    @Inject
    lateinit var beepModule: BeepModule

    override fun onCreate() {
        super.onCreate()
        appComponent.inject(this)
        val okHttpClient = OkHttpClient().newBuilder().apply {
            connectTimeout(serverModule.serverTimeout, TimeUnit.MILLISECONDS)
            readTimeout(serverModule.serverTimeout, TimeUnit.MILLISECONDS)
            writeTimeout(serverModule.serverTimeout, TimeUnit.MILLISECONDS)
        }.build()
        AndroidNetworking.initialize(applicationContext, okHttpClient)
    }

    override fun onTerminate() {
        beepModule.release()
        super.onTerminate()

    }
}