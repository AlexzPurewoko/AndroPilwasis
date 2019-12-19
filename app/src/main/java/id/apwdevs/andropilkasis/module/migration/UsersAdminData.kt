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

package id.apwdevs.andropilkasis.module.migration

import com.google.gson.annotations.SerializedName

data class UsersAdminData(
    val all_user: UserAdminResult
)

data class UserAdminResult(
    @SerializedName(value = "isSuccess")
    var isSuccess: Boolean,

    @SerializedName(value = "message")
    var message: String?,

    @SerializedName(value = "all_user_count")
    val allUserCount: Int,

    @SerializedName(value = "all_deactivated_user_count")
    val deactivatedUserCount: Int,

    @SerializedName(value = "tps")
    val tps: List<DataTPS>?,

    @SerializedName(value = "user")
    val dataUser: List<Users>?
)

data class Users(
    val username: String,
    val nama: String,
    val kelas: String,
    val tps_id: Int,

    @SerializedName(value = "activated")
    var _activated: Int
) {
    val activated: Boolean
        get() = (_activated == 1)
}
