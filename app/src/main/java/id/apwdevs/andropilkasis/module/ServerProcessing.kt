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

package id.apwdevs.andropilkasis.module

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject


open class ServerProcessing @Inject constructor(
    serverModule: ServerModule
) {
    private val serverUrl = "${serverModule.serverProtocol}${serverModule.serverHost}"
    fun sendGetRequest(pathUrl: String): Deferred<String?> = GlobalScope.async {
        val okHttpResponse = AndroidNetworking.get("$serverUrl/$pathUrl")
            .setPriority(Priority.LOW)
            .doNotCacheResponse()
            .setTag("GET REQUEST")
            .build().executeForString()
        if (okHttpResponse.isSuccess) {
            return@async okHttpResponse.result.toString()
        }
        null
    }

    open fun sendPostRequest(
        pathUrl: String,
        postDataParams: HashMap<String, String>
    ): Deferred<String?> = GlobalScope.async {
        val okHttpResponse = AndroidNetworking.post("$serverUrl/$pathUrl").apply {
            setPriority(Priority.MEDIUM)
            doNotCacheResponse()
            for ((key, value) in postDataParams) {
                addBodyParameter(key, value)
            }
        }.build().executeForString()

        if (okHttpResponse.isSuccess) {
            return@async okHttpResponse.result.toString()
        }
        null
    }

}