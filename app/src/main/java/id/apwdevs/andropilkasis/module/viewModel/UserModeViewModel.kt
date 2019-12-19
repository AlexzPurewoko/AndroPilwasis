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

package id.apwdevs.andropilkasis.module.viewModel

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import id.apwdevs.andropilkasis.MyCustomApplication
import id.apwdevs.andropilkasis.module.BeepModule
import id.apwdevs.andropilkasis.module.VibrateModule
import id.apwdevs.andropilkasis.module.migration.DashboardOngoingUser
import id.apwdevs.andropilkasis.module.migration.DataKandidat
import id.apwdevs.andropilkasis.module.migration.UserDataResponse
import id.apwdevs.andropilkasis.module.serverResponse.SelectCandidates
import id.apwdevs.andropilkasis.module.serverResponse.UpdateUserRunning
import id.apwdevs.andropilkasis.module.serverResponse.UserLogout
import id.apwdevs.andropilkasis.params.PublicConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class UserModeViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var selectCandidate: SelectCandidates

    @Inject
    lateinit var updateUserRunning: UpdateUserRunning

    @Inject
    lateinit var logout: UserLogout

    @Inject
    lateinit var beepModule: BeepModule

    @Inject
    lateinit var vibrateModule: VibrateModule


    var dataUser: UserDataResponse? = null //preferred!. Must be not null before load()
    var dataCalon: DataKandidat? = null //preferred!. Must be not null before load()


    var anyError: Boolean = false
    var errorMessage: String? = null

    private var hasFirstInitialize = false

    val showNotif: MutableLiveData<String> = MutableLiveData()
    val loadFinished: MutableLiveData<Boolean> = MutableLiveData()
    val title: MutableLiveData<String> = MutableLiveData()
    val subtitle: MutableLiveData<String> = MutableLiveData()
    val listKandidat: MutableLiveData<DataKandidat> = MutableLiveData()
    val selectedIdCalon: MutableLiveData<Int> = MutableLiveData()
    val timeTickerText: MutableLiveData<String> = MutableLiveData()
    val bottomText: MutableLiveData<String> = MutableLiveData()
    val timeCondition: MutableLiveData<TimeCondition> = MutableLiveData()
    var loadingType: LoadingType? = null

    private var countDownTimer: CountDownTimer? = null

    val bottomStrings = listOf(
        "Pilihlah sesuai dengan pilihan anda. Jangan pilih si dia yang telah meninggalkanmu :(",
        "Ingat, kamu harus jujur kepada dia. Karena dia telah memberikan beragam pilihan kepadamu dengan penuh harapan akan memilihnya :)",
        "Jangan mau terpengaruh dengan janji - janji palsu si doi",
        "Background biru pada kartu menandakan itulah calon hidupmu masa depan :). Pilihlah dengan bijaksana",
        "Toolbar berwarna merah, menandakan waktu akan berakhir",
        "Gak boleh lama - lama, keburu dia nungguin janjimu yang tak pasti. Satu menit saja :)"
    )

    val updateIntervalInSeconds = 3 // 4seconds
    var counterIntervalSeconds = 0

    var isIn10SecBelow : Boolean = false

    init {
        (application as MyCustomApplication).appComponent.inject(this)
    }

    fun setup() {
        if (!hasFirstInitialize) {
            if (dataUser == null || dataCalon == null) {
                return
            }

            val dataAuth = requireNotNull(dataUser?.auth?.data)

            title.postValue(dataAuth.nama)

            val sub = "${dataAuth.kelas} - ${dataAuth.tpsName}"
            subtitle.postValue(sub)
            listKandidat.postValue(dataCalon)
            hasFirstInitialize = true
        }
    }

    fun selectCard(idCalon: Int) {
        selectedIdCalon.postValue(idCalon)
    }

    fun submit() {
        GlobalScope.launch(Dispatchers.IO) {
            loadFinished.postValue(false)
            _submitAsync().await()
            loadFinished.postValue(true)
        }
    }

    private fun _submitAsync() = GlobalScope.async {
        val sId = selectedIdCalon.value
        sId?.let {
            vibrateModule.cancel()
            beepModule.stop()
            val submit = selectCandidate.selectAsync(
                requireNotNull(dataUser?.auth?.data?.username),
                requireNotNull(dataUser?.auth?.data?.password),
                it
            ).await()

            anyError = !submit.data.isSuccess
            errorMessage = submit.data.message

            if (anyError) cancelCountDown()

            loadingType = LoadingType.SUBMIT_DATA
        }
    }

    fun playSoundWhenUserLeave() {

        beepModule.play(3)
        vibrateModule.vibrate(1000)
    }

    fun cancelBeep() {
        beepModule.stop()
    }

    fun playSingleBeep() {
        beepModule.play(0)
        vibrateModule.vibrate(300)
    }

    fun sendTimeHasToBeOut() {
        GlobalScope.launch(Dispatchers.IO) {
            val submit = updateUserRunning.updateAsync(
                requireNotNull(dataUser?.auth?.data?.username),
                requireNotNull(dataUser?.auth?.data?.password),
                DashboardOngoingUser.UserStateType.TIME_TO_OUT
            ).await()

            submit.data.message?.apply {
                showNotif.postValue("Waktu anda akan segera berakhir!")
            }
        }
    }

    fun startCountDown(logMillis: Long) {
        if (countDownTimer != null) return
        countDownTimer = object : CountDownTimer(logMillis, 1000) {
            override fun onFinish() {
                beepModule.stop()
                timeCondition.postValue(TimeCondition.ENDED)
            }

            override fun onTick(millisUntilFinished: Long) {

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = millisUntilFinished
                val minute = calendar.get(Calendar.MINUTE)
                val seconds = calendar.get(Calendar.SECOND)
                if (millisUntilFinished < 30000) {
                    if (!(timeCondition.value != null && timeCondition.value == TimeCondition.HAS_TO_ENDED)) {
                        timeCondition.postValue(TimeCondition.HAS_TO_ENDED)
                        vibrateModule.vibrate(600)
                        beepModule.play(1)
                    } else if(millisUntilFinished < 10000 && !isIn10SecBelow){
                        isIn10SecBelow = true
                        beepModule.play(10)
                        vibrateModule.vibrate(1000)
                    }
                } else {
                    timeCondition.postValue(TimeCondition.RUNNING)
                }

                if (counterIntervalSeconds < updateIntervalInSeconds) {
                    counterIntervalSeconds++
                } else {
                    counterIntervalSeconds = 0
                    bottomText.postValue(
                        bottomStrings[(bottomStrings.indices).random()]
                    )
                }
                @Suppress("IMPLICIT_CAST_TO_ANY")
                timeTickerText.postValue("${if (minute < 10) "0$minute" else minute}:${if (seconds < 10) "0$seconds" else seconds}")
            }

        }
        bottomText.postValue(bottomStrings[2])
        countDownTimer?.start()
    }

    override fun onCleared() {
        super.onCleared()
        beepModule.stop()
        beepModule.release()
        vibrateModule.cancel()
        cancelCountDown()
    }

    fun cancelCountDown() {
        countDownTimer?.apply {
            timeCondition.postValue(TimeCondition.CANCELLED)
            cancel()
        }
    }

    fun forceVerifyAndLogout() {
        GlobalScope.launch(Dispatchers.IO) {
            loadFinished.postValue(false)

            _submitAsync().await()
            _logoutAsync().await()

            loadFinished.postValue(true)
        }
    }

    fun logout() {
        GlobalScope.launch(Dispatchers.IO) {
            loadFinished.postValue(false)

            _logoutAsync().await()

            loadFinished.postValue(true)
        }
    }

    private fun _logoutAsync() = GlobalScope.async{
            val user = requireNotNull(dataUser?.auth?.data)
            val load = logout.logoutAsync(user.username, user.password).await()

            anyError = !load.data.isSuccess
            if (anyError) {
                errorMessage = load.data.message
            }
            (getApplication() as Context).getSharedPreferences(
                PublicConfig.SHARED_PREF_NAME,
                Context.MODE_PRIVATE
            ).apply {
                edit(commit = true) {
                    remove(PublicConfig.SharedKey.KEY_PASSWORD)
                    remove(PublicConfig.SharedKey.KEY_USERNAME)
                }
            }
            loadingType = LoadingType.LOGOUT
    }

    enum class LoadingType {
        SUBMIT_DATA,
        LOGOUT
    }

    enum class TimeCondition {
        CANCELLED,
        RUNNING,
        HAS_TO_ENDED,
        ENDED
    }
}