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
import id.apwdevs.andropilkasis.module.migration.AdminDashboard
import id.apwdevs.andropilkasis.module.migration.DashboardOngoingUser
import id.apwdevs.andropilkasis.module.migration.UserDashboard
import id.apwdevs.andropilkasis.module.migration.UserDataResponse
import id.apwdevs.andropilkasis.module.serverResponse.Dashboard
import id.apwdevs.andropilkasis.module.serverResponse.UserLogout
import kotlinx.coroutines.*
import javax.inject.Inject

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    var dataUser: UserDataResponse? = null

    var anyError: Boolean = false
    var countKandidat: String = ""
    var tempAdminDashboard: AdminDashboard? = null

    @Inject
    lateinit var dashboard: Dashboard

    @Inject
    lateinit var logout: UserLogout

    val errorMessage: MutableLiveData<String> = MutableLiveData()
    val loadFinished: MutableLiveData<Boolean> = MutableLiveData()

    val listOngoingUsers: MutableLiveData<List<DashboardOngoingUser>> = MutableLiveData()
    val userDashboard: MutableLiveData<UserDashboard> = MutableLiveData()

    var deferredUpdate: Deferred<Boolean>? = null

    var hasFirstInitialize: Boolean = false

    init {
        (application as MyCustomApplication).appComponent.inject(this)
    }

    private fun setDeferredUpdate() {
        if (deferredUpdate != null && deferredUpdate?.isActive == true) return
        deferredUpdate = GlobalScope.async {
            while (isActive) {
                updateDashboard().await()
                delay(UPDATE_MILLIS)
            }
            false
        }
    }

    private fun closeDeferred() {
        deferredUpdate?.cancel()
        deferredUpdate = null
    }

    fun startUpdate() {
        setDeferredUpdate()
    }

    fun closeUpdate() {
        closeDeferred()
    }

    fun updateDashboard() = GlobalScope.async {
        loadFinished.postValue(false)
        val user = requireNotNull(dataUser?.auth?.data)
        //Log.e("Dashboard", "Updating...")
        val result = dashboard.getDashboardAsync(
            user.username,
            user.password,
            user.userMode
        ).await()

        //Log.e("Dashboard", "result is -> $result")
        tempAdminDashboard = result.dashboard

        val dh = result.dashboard
        if (dh.isSuccess) {
            anyError = false
            val ongoing = dh.user?.ongoingUsers
            countKandidat = "${dh.totalKandidat}"
            val composeOngoing = composeUserList(dh.user?.ongoingUsers)
            userDashboard.postValue(dh.user)

            listOngoingUsers.postValue(
                composeOngoing
            )

        } else {
            anyError = true
            errorMessage.postValue(dh.message)
        }

        loadFinished.postValue(true)
    }

    private fun composeUserList(ongoingUsers: List<DashboardOngoingUser>?): List<DashboardOngoingUser> {
        if(ongoingUsers.isNullOrEmpty()) return emptyList()
        val nlist = mutableListOf<DashboardOngoingUser>()
        ongoingUsers.forEach {
            if(it.userState == DashboardOngoingUser.UserStateType.TIME_TO_OUT)
                nlist.add(it)
        }

        ongoingUsers.forEach {
            if(it.userState == DashboardOngoingUser.UserStateType.ONGOING)
                nlist.add(it)
        }
        return nlist
    }

    fun logoutUserManually(user: DashboardOngoingUser){
        if(user.username == dataUser?.auth?.data?.username)return
        GlobalScope.launch {
            val outs = logout.forceLogoutAsync(user.username).await()

            if(outs.data.isSuccess){
                // load the data again
                closeUpdate()
                updateDashboard().await()
                startUpdate()
            }
        }
    }

    override fun onCleared() {
        closeUpdate()
    }

    companion object {
        const val UPDATE_MILLIS: Long = 1000
    }
}