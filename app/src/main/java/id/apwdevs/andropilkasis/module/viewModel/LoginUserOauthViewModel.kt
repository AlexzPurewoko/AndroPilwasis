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
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import id.apwdevs.andropilkasis.MyCustomApplication
import id.apwdevs.andropilkasis.module.VibrateModule
import id.apwdevs.andropilkasis.module.migration.DataKandidat
import id.apwdevs.andropilkasis.module.migration.UserData
import id.apwdevs.andropilkasis.module.migration.UserDataResponse
import id.apwdevs.andropilkasis.module.serverResponse.GetCalon
import id.apwdevs.andropilkasis.module.serverResponse.GetParams
import id.apwdevs.andropilkasis.module.serverResponse.UserLogin
import id.apwdevs.andropilkasis.module.serverResponse.UserLogout
import id.apwdevs.andropilkasis.params.PublicConfig
import id.apwdevs.andropilkasis.plugin.CheckConnections
import kotlinx.coroutines.*
import javax.inject.Inject

class LoginUserOauthViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var _getParams: GetParams

    @Inject
    lateinit var _userLogin: UserLogin

    @Inject
    lateinit var _calon: GetCalon

    @Inject
    lateinit var _checkConnections: CheckConnections

    @Inject
    lateinit var logout: UserLogout

    @Inject
    lateinit var vibrateModule: VibrateModule

    val loadFinished: MutableLiveData<Boolean> = MutableLiveData()

    var isLogged: Boolean = false
    var anyError: Boolean = false
    var errorType: ErrorType? = null
    var errorMessage: String = ""

    var mode: Mode = Mode.MODE_LOAD

    lateinit var confParams: HashMap<String, String>
    lateinit var dataCalon: DataKandidat
    lateinit var userData: UserDataResponse

    init {
        (application as MyCustomApplication).appComponent.inject(this)
    }

    fun load() {
        GlobalScope.launch(Dispatchers.IO) {
            mode = Mode.MODE_LOAD
            loadFinished.postValue(false)
            val checkConnections = _checkConnections.isConnectedAsync().await()
            Log.e("HHHHH", "connections -> $checkConnections")
            if (checkConnections) {
                val params = _getParams.getAllAsync().await()
                dataCalon = _calon.getCalonAsync().await()
                if (params.isNullOrEmpty()) {
                    anyError = true
                    errorMessage = "Tidak dapat mengambil konfigurasi dari server!"
                    errorType = ErrorType.CANNOT_RETRIEVE_CONFIGURATION
                } else {
                    anyError = false
                    errorMessage = ""
                    confParams = params
                }

                val login = tryLoginAsync().await()
                isLogged = login
            } else {
                anyError = true
                errorMessage = "Tidak bisa terhubung ke server!"
                errorType = ErrorType.CANNOT_TELL_SERVER
            }
            loadFinished.postValue(true)
        }
    }

    fun submitLogin(username: String, password: String) {
        GlobalScope.launch(Dispatchers.IO) {
            mode = Mode.MODE_LOGIN
            loadFinished.postValue(false)
            val sharedPreferences = (getApplication() as Context).getSharedPreferences(
                PublicConfig.SHARED_PREF_NAME,
                Context.MODE_PRIVATE
            )

            val login = _userLogin.loginAsync(username, password, false).await()

            if (login.auth.isSuccess) {
                if (login.auth.data?.activated == false) {
                    anyError = true
                    errorMessage = "Pengguna belum teraktivasi, Silahkan hubungi administrator!"
                    errorType = ErrorType.USER_NOT_ACTIVATED

                    logout.logoutAsync(login.auth.data!!.username, login.auth.data!!.password).await()
                } else {
                    anyError = false
                    errorMessage = ""
                    userData = login
                    vibrateModule.vibrate(500)
                    sharedPreferences.edit(commit = true) {
                        putString(PublicConfig.SharedKey.KEY_USERNAME, username)
                        putString(PublicConfig.SharedKey.KEY_PASSWORD, password)
                    }
                }
            } else {
                anyError = true
                login.auth.apply {
                    if (!isUserRegistered) {
                        errorType = ErrorType.USER_NOT_FOUND
                    } else if (!passwordVerification) {
                        errorType = ErrorType.PASSWORD_NOT_REGISTERED
                    }
                }
                errorMessage = login.auth.message
            }
            loadFinished.postValue(true)
        }
    }

    private fun tryLoginAsync(): Deferred<Boolean> = GlobalScope.async {
        val sharedPreferences = (getApplication() as Context).getSharedPreferences(
            PublicConfig.SHARED_PREF_NAME,
            Context.MODE_PRIVATE
        )
        val username = sharedPreferences.getString(PublicConfig.SharedKey.KEY_USERNAME, null)
        val password = sharedPreferences.getString(PublicConfig.SharedKey.KEY_PASSWORD, null)

        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            return@async false
        }
        val out = _userLogin.loginAsync(username, password, true).await()
        if (out.auth.data?.userMode == UserData.UserMode.USER && confParams["auto_out_when_nextlogin_hasselected"]?.toBoolean() == true) {
            logout.logoutAsync(username, password).await()
            anyError = true
            errorMessage = "Pengguna ini tidak dapat memilih ulang!"
            errorType = ErrorType.CANNOT_RE_ELECTION
            sharedPreferences.edit(commit = true) {
                remove(PublicConfig.SharedKey.KEY_USERNAME)
                remove(PublicConfig.SharedKey.KEY_PASSWORD)
            }
            return@async false
        }
        vibrateModule.vibrate(500)
        userData = out
        return@async out.auth.isSuccess
    }

    enum class Mode {
        MODE_LOAD,
        MODE_LOGIN
    }

    enum class ErrorType {
        USER_NOT_FOUND,
        PASSWORD_NOT_REGISTERED,
        CANNOT_RE_ELECTION,
        CANNOT_TELL_SERVER,
        USER_NOT_ACTIVATED,
        CANNOT_RETRIEVE_CONFIGURATION
    }
}