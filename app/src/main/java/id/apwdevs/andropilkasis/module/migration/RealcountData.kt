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

data class RealcountData(
    val realcount: RealcountMessage
)

data class RealcountMessage(

    @SerializedName(value = "isSuccess")
    var isSuccess: Boolean,

    @SerializedName(value = "message")
    var message: String?,

    @SerializedName(value = "total_user")
    var countUser: String?,

    @SerializedName(value = "count_selected")
    val countSelected: List<CountSelectedReal>?,

    @SerializedName(value = "golput")
    var golput: Golput?
)

data class CountSelectedReal(

    @SerializedName(value = "calon_id")
    val kandidatID: Int,

    @SerializedName(value = "total_percent")
    val totalPercent: Double,

    @SerializedName(value = "total_selected")
    val totalSelected: Int
)

data class Golput(

    @SerializedName(value = "not_selected")
    val notSelected: Int,

    @SerializedName(value = "deactivated")
    val deactivatedCount: Int,

    @SerializedName(value = "not_selected_percent")
    val notSelectedPercent: Double,

    @SerializedName(value = "deactivated_percent")
    val deactivatedCountPercent: Double
)
