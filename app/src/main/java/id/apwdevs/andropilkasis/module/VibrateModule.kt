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
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import javax.inject.Inject

class VibrateModule @Inject constructor(
    context: Context
) {
    private val vibrateService: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    fun cancel(){
        vibrateService.cancel()
    }

    fun vibrate(milliseconds: Long){
        if(!vibrateService.hasVibrator()) return
        cancel()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            vibrateService.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
        else
            vibrateService.vibrate(milliseconds)
    }
}