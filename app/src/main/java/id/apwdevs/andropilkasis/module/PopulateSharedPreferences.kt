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
import androidx.core.content.edit
import id.apwdevs.andropilkasis.params.PublicConfig

class PopulateSharedPreferences(
    context: Context
) {
    // write your all key and values for sharedpreferences
    private val allEntries: HashMap<String, Any> = hashMapOf(
        PublicConfig.SharedKey.KEY_SERVER_NAME to PublicConfig.ServerConfig.DEF_SERVER_HOST,
        PublicConfig.SharedKey.KEY_SERVER_TIMEOUT to PublicConfig.ServerConfig.DEF_SERVER_TIMEOUT,
        PublicConfig.SharedKey.KEY_PROTOCOL to PublicConfig.ServerConfig.DEF_PROTOCOL
    )

    init {
        context.getSharedPreferences(PublicConfig.SHARED_PREF_NAME, Context.MODE_PRIVATE).apply {
            edit {
                for ((key, value) in allEntries) {
                    if (!contains(key))
                        when (value) {
                            is String -> putString(key, value)
                            is Int -> putInt(key, value)
                            is Float -> putFloat(key, value)
                            is Boolean -> putBoolean(key, value)
                            is Long -> putLong(key, value)
                        }
                }
            }
        }
    }

}