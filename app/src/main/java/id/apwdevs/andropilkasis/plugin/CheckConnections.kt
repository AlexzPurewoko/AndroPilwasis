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

package id.apwdevs.andropilkasis.plugin

import android.content.Context
import android.net.ConnectivityManager
import com.androidnetworking.AndroidNetworking
import id.apwdevs.andropilkasis.module.ServerModule
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject

class CheckConnections @Inject constructor(
    private val context: Context,
    private val serverModule: ServerModule
) {
    fun isConnectedAsync(): Deferred<Boolean> = GlobalScope.async {
        val okHttpREsponse =
            AndroidNetworking.head("${serverModule.serverProtocol}${serverModule.serverHost}")
                .build()
                .executeForOkHttpResponse()
        okHttpREsponse.isSuccess
    }
}