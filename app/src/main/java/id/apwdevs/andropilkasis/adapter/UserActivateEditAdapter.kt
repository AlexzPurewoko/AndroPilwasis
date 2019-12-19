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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.apwdevs.andropilkasis.R
import id.apwdevs.andropilkasis.module.migration.DataTPS
import id.apwdevs.andropilkasis.module.migration.Users

class UserActivateEditAdapter(
    val listUsers: MutableList<UserActivateEdit>,
    val dataTps: MutableList<DataTPS>
) : RecyclerView.Adapter<UserActivateEditAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder =
        UserViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_useradmin_deact,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = listUsers.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val lst = listUsers[position]
        var retTPS: DataTPS? = null
        for (tps in dataTps) {
            if (tps.id == lst.user.tps_id) {
                retTPS = tps
                break
            }
        }
        retTPS = retTPS ?: DataTPS(lst.user.tps_id, "UNKNOWN")
        holder.bind(lst, retTPS)
    }

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.dashboard_user_name)
        val tps: TextView = view.findViewById(R.id.dashboard_user_tps)
        private val deactSwitch: Switch = view.findViewById(R.id.useradmin_switch_deact)
        val username: TextView = view.findViewById(R.id.useradmin_deact_username)

        fun bind(userEdit: UserActivateEdit, dataTPS: DataTPS) {
            name.text = userEdit.user.nama
            username.text = userEdit.user.username

            @SuppressLint("SetTextI18n")
            tps.text = "${userEdit.user.kelas} - ${dataTPS.name}"

            deactSwitch.showText = false
            deactSwitch.isChecked = userEdit.updateActivateCondition
            deactSwitch.setOnCheckedChangeListener { _, isChecked ->
                if (userEdit.updateActivateCondition != isChecked) {
                    userEdit.updateActivateCondition = isChecked
                    deactSwitch.isChecked = isChecked
                }
            }
        }
    }

    data class UserActivateEdit(
        val user: Users,

        @Volatile
        var updateActivateCondition: Boolean
    )
}