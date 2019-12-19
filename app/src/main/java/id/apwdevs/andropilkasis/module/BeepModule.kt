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
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import id.apwdevs.andropilkasis.R
import javax.inject.Inject

class BeepModule @Inject constructor(
    context: Context
) {
    @Suppress("DEPRECATION")
    private val soundPool : SoundPool =
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            SoundPool.Builder()
                .setMaxStreams(10).build()
        else
            SoundPool(10, AudioManager.STREAM_MUSIC, 1)

    private var ready: Boolean = false
    private var wavID = 0

    init {
        soundPool.setOnLoadCompleteListener { _, _, _ ->
            ready = true
        }
        wavID = soundPool.load(context, R.raw.beep, 1)
    }

    fun play(loop: Int){
        if(!ready) return
        soundPool.stop(wavID)
        soundPool.play(wavID, 1f, 1f, 0, loop, 1f)
    }

    fun stop(){
        if(!ready) return
        soundPool.stop(wavID)
    }

    fun release() {
        soundPool.release()
    }
}