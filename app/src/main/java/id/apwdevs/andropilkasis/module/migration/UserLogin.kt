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

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData(

    @SerializedName(value = "nama")
    val nama: String,

    @SerializedName(value = "kelas")
    val kelas: String,

    @SerializedName(value = "tps_name")
    val tpsName: String,

    @SerializedName(value = "tps_id")
    val tpsId: Int,

    @SerializedName(value = "username")
    val username: String,

    @SerializedName(value = "password")
    val password: String,

    @SerializedName(value = "activated")
    val _activated: Int,

    @SerializedName(value = "pilihan")
    val pilihan: Int,

    @SerializedName(value = "user_mode")
    val _user_mode: Int
) : Parcelable {
    @IgnoredOnParcel
    val activated: Boolean
        get() = _activated == 1

    @IgnoredOnParcel
    val userMode: UserMode
        get() = UserMode.getVal(_user_mode)

    //val userMode:

    enum class UserMode(val id: Int) {
        USER(0),
        ADMIN_TPS(1),
        ADMIN_ALL(2);

        companion object {
            fun getVal(id: Int): UserMode =
                when (id) {
                    1 -> ADMIN_TPS
                    0 -> USER
                    2 -> ADMIN_ALL
                    else ->
                        throw UnsupportedOperationException("Invalid ids")
                }
        }
    }
}

@Parcelize
data class LoginUserAuth(

    @SerializedName(value = "isUserRegistered")
    var isUserRegistered: Boolean,

    @SerializedName(value = "passwordVerification")
    var passwordVerification: Boolean,

    @SerializedName(value = "isSuccess")
    var isSuccess: Boolean,

    @SerializedName(value = "message")
    var message: String,

    @SerializedName(value = "data")
    var data: UserData?
) : Parcelable

@Parcelize
data class UserDataResponse(
    val auth: LoginUserAuth
) : Parcelable