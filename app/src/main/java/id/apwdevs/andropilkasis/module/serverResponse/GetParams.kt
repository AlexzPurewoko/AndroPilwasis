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

package id.apwdevs.andropilkasis.module.serverResponse

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.apwdevs.andropilkasis.module.ServerProcessing
import id.apwdevs.andropilkasis.params.PublicConfig
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject

class GetParams @Inject constructor(
    private val serverProcessing: ServerProcessing
) {

    companion object {
        private const val FILES = "${PublicConfig.ServerConfig.DEF_BASE_DIR}/get_params.php"
    }

    fun getAllAsync(): Deferred<HashMap<String, String>?> = GlobalScope.async {

        val retJSONStr = serverProcessing.sendGetRequest(
            FILES
        ).await()
        val type = object : TypeToken<HashMap<String, String>>() {}.type
        return@async Gson().fromJson<HashMap<String, String>>(retJSONStr, type)
    }
}