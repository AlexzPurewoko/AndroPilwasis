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

data class UsersByTPSResponse(
    val users: UsersByTPS
)

data class UsersByTPS(

    @SerializedName(value = "isSuccess")
    var isSuccess: Boolean,

    @SerializedName(value = "message")
    var message: String?,

    val data: List<Users>?
)
