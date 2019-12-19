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
import id.apwdevs.andropilkasis.module.migration.UserData
import id.apwdevs.andropilkasis.module.migration.UsersAdminData
import id.apwdevs.andropilkasis.module.migration.UsersByTPSResponse
import id.apwdevs.andropilkasis.params.PublicConfig
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject

class GetUsers @Inject constructor(
    private val serverProcessing: ServerProcessing
) {
    companion object {
        private const val BY_TPS = "${PublicConfig.ServerConfig.DEF_BASE_DIR}/getUsersByTPS.php"
        private const val ALL = "${PublicConfig.ServerConfig.DEF_BASE_DIR}/getAllUsers.php"
        private const val USER_MODE = "userMode"
        private const val TPS_ID = "tps_id"
    }

    @Suppress("unused")
    fun getByTpsAsync(
        username: String,
        password: String,
        tpsId: Int
    ): Deferred<UsersByTPSResponse> = GlobalScope.async {
        // build the data into hashmaps and send it into server

        val postParams = hashMapOf(
            PublicConfig.SharedKey.KEY_USERNAME to username,
            PublicConfig.SharedKey.KEY_PASSWORD to password,
            TPS_ID to tpsId.toString()
        )
        val byTps =
            serverProcessing.sendPostRequest(BY_TPS, postParams).await()

        return@async Gson().fromJson(byTps, UsersByTPSResponse::class.java)
    }

    fun getAllAsync(
        username: String,
        password: String,
        userMode: UserData.UserMode
    ): Deferred<UsersAdminData> = GlobalScope.async {
        // build the data into hashmaps and send it into server

        val postParams = hashMapOf(
            PublicConfig.SharedKey.KEY_USERNAME to username,
            PublicConfig.SharedKey.KEY_PASSWORD to password,
            USER_MODE to userMode.id.toString()
        )
        val all =
            serverProcessing.sendPostRequest(ALL, postParams).await()

        return@async Gson().fromJson(all, UsersAdminData::class.java)
    }
}