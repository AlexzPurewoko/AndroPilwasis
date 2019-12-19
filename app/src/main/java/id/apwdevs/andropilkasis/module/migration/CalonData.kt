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
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Kandidat(

    @SerializedName(value = "num_id")
    val nomor_kandidat: Int,

    @SerializedName(value = "nama")
    val nama: String,

    @SerializedName(value = "kelas")
    val kelas: String,

    @SerializedName(value = "visi")
    val visi: String,

    @SerializedName(value = "misi")
    val misi: String,

    @SerializedName(value = "avatar")
    val avatar: String,

    @SerializedName(value = "nickname")
    val nickname: String
) : Parcelable

@Parcelize
data class DataKandidat(
    val kandidat: List<Kandidat>
) : Parcelable