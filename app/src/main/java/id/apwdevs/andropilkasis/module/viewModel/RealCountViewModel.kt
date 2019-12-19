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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import id.apwdevs.andropilkasis.MyCustomApplication
import id.apwdevs.andropilkasis.module.ServerModule
import id.apwdevs.andropilkasis.module.migration.*
import id.apwdevs.andropilkasis.module.serverResponse.RealCount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class RealCountViewModel(application: Application) : AndroidViewModel(application) {
    var dataUser: UserDataResponse? = null
    var candidate: DataKandidat? = null
    var parameters: HashMap<String, String>? = null
    var anyError: Boolean = false

    @Inject
    lateinit var realCount: RealCount

    @Inject
    lateinit var serverModule: ServerModule

    init {
        (application as MyCustomApplication).appComponent.inject(this)
    }

    val listCandidateRealCount: MutableLiveData<RealcountMessage> = MutableLiveData()
    val loadFinished: MutableLiveData<Boolean> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()

    fun load() {
        GlobalScope.launch(Dispatchers.IO) {
            if (dataUser == null || candidate == null || parameters == null) return@launch
            loadFinished.postValue(false)
            val isPermitted = when (dataUser?.auth?.data?.userMode) {
                UserData.UserMode.ADMIN_TPS ->
                    requireNotNull(parameters)["allowed_tpsadmin_access_realcount"]?.toBoolean() == true
                UserData.UserMode.ADMIN_ALL ->
                    requireNotNull(parameters)["allow_user_access_realcount"]?.toBoolean() == true
                else -> false
            }
            if (isPermitted) {
                val user = requireNotNull(dataUser?.auth?.data)
                val data = realCount.countAsync(
                    user.username,
                    user.password,
                    user.userMode
                ).await()

                anyError = !data.realcount.isSuccess
                if (anyError) {
                    errorMessage.postValue(data.realcount.message)
                    listCandidateRealCount.postValue(fakeUser())
                } else {
                    listCandidateRealCount.postValue(data.realcount)
                }
            } else {
                listCandidateRealCount.postValue(fakeUser())
            }
            loadFinished.postValue(true)
        }
    }

    private fun fakeUser(): RealcountMessage {
        val golput = Golput(0, 0, 0.0, 0.0)
        val listCount = mutableListOf<CountSelectedReal>()
        requireNotNull(candidate?.kandidat).forEach {
            listCount.add(CountSelectedReal(it.nomor_kandidat, 0.0, 0))
        }
        return RealcountMessage(true, null, "0", listCount, golput)
    }
}