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
import id.apwdevs.andropilkasis.adapter.UserActivateEditAdapter
import id.apwdevs.andropilkasis.module.migration.*
import id.apwdevs.andropilkasis.module.serverResponse.GetUsers
import id.apwdevs.andropilkasis.module.serverResponse.UserConditionEdit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserAdminViewModel(application: Application) : AndroidViewModel(application) {

    var dataUser: UserDataResponse? = null
    var anyError: Boolean = false
    //val listOfModifiedUsers: MutableList<Users> = mutableListOf()

    val listSpinnerItem: MutableList<String> = mutableListOf()
    val listOriginalTPS: MutableList<DataTPS> = mutableListOf()
    val allUsersTemp: MutableList<Users> = mutableListOf()

    var leastPositionSpinner: Int = 0

    @Inject
    lateinit var getAllUsers: GetUsers

    @Inject
    lateinit var saveUserConditionEdit: UserConditionEdit

    val listsUsers: MutableLiveData<List<Users>> = MutableLiveData()
    val listTps: MutableLiveData<List<String>> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()
    val loadFinished: MutableLiveData<Boolean> = MutableLiveData()
    val totalUsers: MutableLiveData<Int> = MutableLiveData()
    val totalUsersDeactivated: MutableLiveData<Int> = MutableLiveData()

    init {
        (application as MyCustomApplication).appComponent.inject(this)
    }

    fun loadData() {
        GlobalScope.launch(Dispatchers.IO) {
            loadFinished.postValue(false)

            triggerLoadAsync().await()

            loadFinished.postValue(true)
        }
    }

    @Synchronized
    private fun triggerLoadAsync() = GlobalScope.async {
        val f = requireNotNull(dataUser?.auth?.data)
        val load = getAllUsers.getAllAsync(
            f.username,
            f.password,
            f.userMode
        ).await()
        val res = load.all_user
        anyError = !res.isSuccess
        if (anyError) errorMessage.postValue(res.message)

        updateTPS(res)
        allUsersTemp.clear()
        allUsersTemp.addAll(res.dataUser!!)
        listsUsers.postValue(res.dataUser)
        totalUsers.postValue(res.allUserCount)
        totalUsersDeactivated.postValue(res.deactivatedUserCount)
        filterListsInTps(leastPositionSpinner)
    }

    @Synchronized
    private fun updateTPS(res: UserAdminResult) {
        val tps = res.tps ?: return
        val ret = mutableListOf<String>()
        when (dataUser?.auth?.data?.userMode) {
            UserData.UserMode.ADMIN_TPS -> {
                for (i in res.dataUser!!) {
                    if (ret.isEmpty()) {
                        ret.add(i.kelas)
                        continue
                    }
                    val savedSize = ret.size
                    var occurences = 0
                    for (idxr in 0 until savedSize) {

                        if (i.kelas == ret[idxr]) {
                            occurences++
                        }
                    }
                    if (occurences == 0) ret.add(i.kelas)
                }
            }
            UserData.UserMode.ADMIN_ALL -> {
                for (t in tps) {
                    ret.add(t.name)
                }
            }
            else -> return
        }
        listOriginalTPS.clear()
        listOriginalTPS.addAll(res.tps)
        listSpinnerItem.clear()
        listSpinnerItem.addAll(ret)
        listTps.postValue(ret)
    }

    @Synchronized
    fun filterListsInTps(tpsPositionData: Int) {
        GlobalScope.launch {
            leastPositionSpinner = tpsPositionData
            val selectedItem = listSpinnerItem[tpsPositionData]
            val nLists = mutableListOf<Users>()
            when (dataUser?.auth?.data?.userMode) {
                UserData.UserMode.ADMIN_TPS -> {
                    for (item in allUsersTemp) {
                        if (item.kelas == selectedItem) {
                            nLists.add(item)
                        }
                    }
                }
                UserData.UserMode.ADMIN_ALL -> {
                    var tpsId = 0
                    for (atps in listOriginalTPS) {
                        if (atps.name == selectedItem) {
                            tpsId = atps.id
                            break
                        }
                    }
                    for (item in allUsersTemp) {
                        if (item.tps_id == tpsId) {
                            nLists.add(item)
                        }
                    }
                }
                else -> return@launch
            }

            listsUsers.postValue(nLists)
        }
    }

    @Synchronized
    fun submitSaveUserCondition(listData: List<UserActivateEditAdapter.UserActivateEdit>) {
        GlobalScope.launch(Dispatchers.IO) {
            loadFinished.postValue(false)

            val listOfModifiedUsers = mutableListOf<Users>()
            for (data in listData) {
                if (data.updateActivateCondition != data.user.activated) {
                    listOfModifiedUsers.add(
                        data.user.copy().apply {
                            _activated = if (data.updateActivateCondition) 1 else 0
                        }
                    )
                }
            }

            // saves into the database
            val data = requireNotNull(dataUser?.auth?.data)
            val save = saveUserConditionEdit.saveAsync(
                data.username,
                data.password,
                data.userMode,
                listOfModifiedUsers
            ).await()
            anyError = !save.data.isSuccess
            if (anyError) errorMessage.postValue(save.data.message)
            else listOfModifiedUsers.clear()

            triggerLoadAsync().await()

            loadFinished.postValue(true)
        }
    }
}