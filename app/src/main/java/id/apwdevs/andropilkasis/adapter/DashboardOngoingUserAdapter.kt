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

package id.apwdevs.andropilkasis.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.apwdevs.andropilkasis.R
import id.apwdevs.andropilkasis.module.migration.DashboardOngoingUser
import id.apwdevs.andropilkasis.module.migration.DataTPS

class DashboardOngoingUserAdapter(
    val listOngoingUser: MutableList<DashboardOngoingUser>,
    val listTps: MutableList<DataTPS>
) : RecyclerView.Adapter<DashboardOngoingUserAdapter.OngoingViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OngoingViewHolder =
        OngoingViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_ongoing_users, parent, false)
        )

    override fun getItemCount(): Int = listOngoingUser.size

    override fun onBindViewHolder(holder: OngoingViewHolder, position: Int) {
        holder.bindItem(listOngoingUser[position], listTps)
    }


    class OngoingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.dashboard_user_name)
        val userTps: TextView = view.findViewById(R.id.dashboard_user_tps)
        val userState: TextView = view.findViewById(R.id.dashboard_user_state)

        @SuppressLint("SetTextI18n")
        fun bindItem(userOngoing: DashboardOngoingUser, listTps: List<DataTPS>) {


            userName.apply {
                text = userOngoing.nama
            }
            userState.apply {
                when (userOngoing.userState) {
                    DashboardOngoingUser.UserStateType.ONGOING -> {
                        setTextColor(Color.parseColor("#094EC4"))
                    }
                    DashboardOngoingUser.UserStateType.LOGGED_OUT -> {
                    }
                    DashboardOngoingUser.UserStateType.TIME_TO_OUT -> {
                        setTextColor(Color.parseColor("#B71C1C"))
                    }
                }
                text = userOngoing.userState.toString()
            }
            userTps.apply {


                var tps = ""
                for (tpsL in listTps) {
                    if (tpsL.id == userOngoing.tps_id) {
                        tps = tpsL.name
                        break
                    }
                }
                text = "${userOngoing.kelas} - $tps"

            }


        }

    }
}