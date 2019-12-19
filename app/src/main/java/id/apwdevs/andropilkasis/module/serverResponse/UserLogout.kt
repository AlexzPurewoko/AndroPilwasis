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
import id.apwdevs.andropilkasis.module.migration.BasicResultServerResponse
import id.apwdevs.andropilkasis.params.PublicConfig
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject

class UserLogout @Inject constructor(
    private val serverProcessing: ServerProcessing
) {

    companion object {
        private const val FILES = "${PublicConfig.ServerConfig.DEF_BASE_DIR}/UserLogout.php"
        private const val FILES_2 = "${PublicConfig.ServerConfig.DEF_BASE_DIR}/UserForceLogoutByAdmin.php"
    }

    fun logoutAsync(
        username: String,
        password: String
    ): Deferred<BasicResultServerResponse> = GlobalScope.async {
        // build the data into hashmaps and send it into server

        val postParams = hashMapOf(
            PublicConfig.SharedKey.KEY_USERNAME to username,
            PublicConfig.SharedKey.KEY_PASSWORD to password
        )
        val oauthLogin =
            serverProcessing.sendPostRequest(FILES, postParams).await()

        return@async Gson().fromJson(oauthLogin, BasicResultServerResponse::class.java)
    }

    fun forceLogoutAsync(
        username: String
    ): Deferred<BasicResultServerResponse> = GlobalScope.async {
        // build the data into hashmaps and send it into server

        val postParams = hashMapOf(
            PublicConfig.SharedKey.KEY_USERNAME to username
        )
        val oauthLogin =
            serverProcessing.sendPostRequest(FILES_2, postParams).await()

        return@async Gson().fromJson(oauthLogin, BasicResultServerResponse::class.java)
    }
}