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
import id.apwdevs.andropilkasis.module.ServerProcessing
import id.apwdevs.andropilkasis.module.migration.DashboardData
import id.apwdevs.andropilkasis.module.migration.UserData
import id.apwdevs.andropilkasis.params.PublicConfig
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject

class Dashboard @Inject constructor(
    private val serverProcessing: ServerProcessing
) {

    companion object {
        private const val ALL = "${PublicConfig.ServerConfig.DEF_BASE_DIR}/dashboard.php"
        private const val USER_MODE = "userMode"
    }

    fun getDashboardAsync(
        username: String,
        password: String,
        userMode: UserData.UserMode
    ): Deferred<DashboardData> = GlobalScope.async {
        // build the data into hashmaps and send it into server

        val postParams = hashMapOf(
            PublicConfig.SharedKey.KEY_USERNAME to username,
            PublicConfig.SharedKey.KEY_PASSWORD to password,
            USER_MODE to userMode.id.toString()
        )
        val all =
            serverProcessing.sendPostRequest(ALL, postParams).await()

        return@async Gson().fromJson(all, DashboardData::class.java)
    }
}