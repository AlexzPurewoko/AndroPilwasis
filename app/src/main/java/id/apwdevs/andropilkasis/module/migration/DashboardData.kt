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
import kotlinx.android.parcel.IgnoredOnParcel

data class DashboardData(
    val dashboard: AdminDashboard
)

data class AdminDashboard(

    @SerializedName(value = "isSuccess")
    var isSuccess: Boolean,

    @SerializedName(value = "message")
    var message: String?,

    @SerializedName(value = "count_kandidat")
    var totalKandidat: String?,

    @SerializedName(value = "tps")
    val tps: List<DataTPS>?,

    @SerializedName(value = "user")
    var user: UserDashboard?
)

data class UserDashboard(

    @SerializedName(value = "total")
    val total: Int,

    @SerializedName(value = "has_selection")
    val has_selection: Int,

    @SerializedName(value = "nonactivated")
    val nonactivated: Int,

    @SerializedName(value = "ongoing_users")
    val ongoingUsers: List<DashboardOngoingUser>?
)

data class DashboardOngoingUser(

    @SerializedName(value = "nama")
    val nama: String,

    @SerializedName(value = "username")
    val username: String,

    @SerializedName(value = "kelas")
    val kelas: String,

    @SerializedName(value = "tps_id")
    val tps_id: Int,

    @SerializedName(value = "state")
    val _state: Int
) {

    @IgnoredOnParcel
    val userState: UserStateType
        get() = UserStateType.getVal(_state)

    enum class UserStateType(val id: Int) {
        ONGOING(1),
        LOGGED_OUT(0),
        TIME_TO_OUT(2);

        companion object {
            fun getVal(id: Int): UserStateType =
                when (id) {
                    1 -> ONGOING
                    0 -> LOGGED_OUT
                    2 -> TIME_TO_OUT
                    else ->
                        throw UnsupportedOperationException("Invalid ids")
                }
        }
    }
}


