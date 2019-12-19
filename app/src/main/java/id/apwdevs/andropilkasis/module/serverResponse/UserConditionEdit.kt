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
import id.apwdevs.andropilkasis.module.migration.UserData
import id.apwdevs.andropilkasis.module.migration.Users
import id.apwdevs.andropilkasis.params.PublicConfig
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import javax.inject.Inject

class UserConditionEdit @Inject constructor(
    private val serverProcessing: ServerProcessing
) {

    companion object {
        private const val FILES = "${PublicConfig.ServerConfig.DEF_BASE_DIR}/saveUserEdit.php"
        private const val USER_MODE = "userMode"
        private const val LIST_USER = "list_user"
        private const val LIST_CONDITION = "list_cond"
    }

    fun saveAsync(
        username: String,
        password: String,
        userMode: UserData.UserMode,
        listUser: List<Users>
    ): Deferred<BasicResultServerResponse> = GlobalScope.async {

        // get the username and condition, separated them

        val cond = StringBuffer()
        val users = StringBuffer()
        listUser.forEach {
            cond.append(it._activated.toString())
            cond.append(',')
            users.append(it.username)
            users.append(',')
        }
        if (users.isNotEmpty()) {
            users.deleteCharAt(users.length - 1)
            cond.deleteCharAt(cond.length - 1)
        }
        val postParams = hashMapOf(
            PublicConfig.SharedKey.KEY_USERNAME to username,
            PublicConfig.SharedKey.KEY_PASSWORD to password,
            USER_MODE to userMode.id.toString(),
            LIST_USER to users.toString(),
            LIST_CONDITION to cond.toString()
        )
        val postUpdate =
            serverProcessing.sendPostRequest(FILES, postParams).await()

        return@async Gson().fromJson(postUpdate, BasicResultServerResponse::class.java)
    }
}