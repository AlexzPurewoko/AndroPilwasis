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
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import id.apwdevs.andropilkasis.MyCustomApplication
import id.apwdevs.andropilkasis.module.migration.UserData
import id.apwdevs.andropilkasis.module.migration.UserDataResponse
import id.apwdevs.andropilkasis.module.serverResponse.UserLogout
import id.apwdevs.andropilkasis.params.PublicConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AdminViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var logout: UserLogout


    var anyError: Boolean = false
    var errorMessage: String? = null

    var userData: UserDataResponse? = null
    val title: MutableLiveData<SpannableString> = MutableLiveData()
    val subtitle: MutableLiveData<String> = MutableLiveData()
    val loadFinished: MutableLiveData<Boolean> = MutableLiveData()

    init {
        (getApplication() as MyCustomApplication).appComponent.inject(this)
    }

    fun setUpper() {
        val spans = when (userData?.auth?.data?.userMode) {
            UserData.UserMode.ADMIN_TPS -> {
                SpannableString("AdminTPS").apply {
                    setSpan(StyleSpan(Typeface.BOLD), 0, this.length, 0)
                    setSpan(android.text.style.ForegroundColorSpan(Color.RED), 5, length, 0)
                }
            }
            UserData.UserMode.ADMIN_ALL -> {
                SpannableString("AdminALL").apply {
                    setSpan(StyleSpan(Typeface.BOLD), 0, this.length, 0)
                    setSpan(android.text.style.ForegroundColorSpan(Color.BLUE), 5, length, 0)
                }
            }
            else -> {
                SpannableString("Unknown").apply {
                    setSpan(StyleSpan(Typeface.BOLD), 0, this.length, 0)
                }
            }
        }
        title.postValue(spans)
        subtitle.postValue(userData?.auth?.data?.nama)
    }

    fun logout() {
        GlobalScope.launch(Dispatchers.IO) {
            loadFinished.postValue(false)
            val user = requireNotNull(userData?.auth?.data)
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
            loadFinished.postValue(true)
        }
    }
}