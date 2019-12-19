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

package id.apwdevs.andropilkasis.params

object PublicConfig {

    const val SHARED_PREF_NAME = "pilkasis_data"
    const val PASSWORD_CONFIG_SERVER = "525d16875a4affce31179da5ecce253"

    object ServerConfig {
        const val DEF_SERVER_TIMEOUT: String = "10000"
        const val DEF_PROTOCOL = "https://"
        const val DEF_SERVER_HOST = "apwdevs.000webhostapp.com"
        const val DEF_BASE_DIR = "AndroPilwasis"
    }

    object SharedKey {
        const val KEY_PROTOCOL: String = "server_protocol"
        const val KEY_USERNAME = "username"
        const val KEY_PASSWORD = "password"

        const val KEY_SERVER_NAME = "server_url"
        const val KEY_SERVER_TIMEOUT = "server_timeout_milliseconds"

    }
}