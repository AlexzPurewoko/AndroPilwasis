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

import android.content.Context
import id.apwdevs.andropilkasis.params.PublicConfig
import javax.inject.Inject


class ServerModule @Inject constructor(
    context: Context
) {
    val serverProtocol: String
        get() = requireNotNull(
            sharedPreferences.getString(
                PublicConfig.SharedKey.KEY_PROTOCOL,
                PublicConfig.ServerConfig.DEF_PROTOCOL
            )
        )
    val serverHost: String
        get() = requireNotNull(
            sharedPreferences.getString(
                PublicConfig.SharedKey.KEY_SERVER_NAME,
                PublicConfig.ServerConfig.DEF_SERVER_HOST
            )
        )
    val serverTimeout: Long
        get() = requireNotNull(
            sharedPreferences.getString(
                PublicConfig.SharedKey.KEY_SERVER_TIMEOUT,
                PublicConfig.ServerConfig.DEF_SERVER_TIMEOUT
            )
        ).toLong()

    private val sharedPreferences =
        context.getSharedPreferences(PublicConfig.SHARED_PREF_NAME, Context.MODE_PRIVATE)


}