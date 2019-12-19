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

package id.apwdevs.andropilkasis.fragment.adminActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import id.apwdevs.andropilkasis.R
import id.apwdevs.andropilkasis.adapter.DashboardOngoingUserAdapter
import id.apwdevs.andropilkasis.module.migration.UserDataResponse
import id.apwdevs.andropilkasis.module.viewModel.DashboardViewModel
import id.apwdevs.andropilkasis.plugin.ItemClickSupport
import id.apwdevs.andropilkasis.plugin.showAlert

class DashboardUserFragment : Fragment() {

    // user selected
    private lateinit var progressUserSelection: CircularProgressBar
    private lateinit var progressTextUserSelection: TextView
    private lateinit var userSelectionDescribe: TextView


    // user deactivated
    private lateinit var progressUserDeactivated: CircularProgressBar
    private lateinit var progressTextUserDeactivated: TextView
    private lateinit var userDeactivatedDescribe: TextView

    private lateinit var countCandidateText: TextView

    private lateinit var ongoingUserListRecycle: RecyclerView


    private lateinit var viewModel: DashboardViewModel
    private lateinit var linearLayout: NestedScrollView

    private val onItemRecyclerLongClick = object : ItemClickSupport.OnItemLongClickListener {
        override fun onItemLongClicked(
            recyclerView: RecyclerView,
            position: Int,
            v: View
        ) : Boolean {
            showAlert(recyclerView.context){
                it.setTitle(getString(R.string.alert))
                it.setMessage(getString(R.string.dialog_force_logout_user))
                it.setCancelable(true)
                it.setPositiveButton(getString(R.string.yes)){dialog, _ ->
                    dialog.dismiss()
                    val adapter = recyclerView.adapter
                    if(adapter is DashboardOngoingUserAdapter){
                        val users = adapter.listOngoingUser[position]
                        viewModel.logoutUserManually(users)
                    }
                }
                it.setNegativeButton(getString(R.string.no)){ dialog, _ ->
                    dialog.dismiss()
                }
            }

            return true
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_admin_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            linearLayout = this as NestedScrollView
            progressUserSelection = findViewById(R.id.dashboard_progress_user_election)
            progressTextUserSelection = findViewById(R.id.dashboard_progress_user_election_text)
            userSelectionDescribe = findViewById(R.id.dashboard_text_user)
            progressUserDeactivated = findViewById(R.id.dashboard_progress_user_deactivated)
            progressTextUserDeactivated =
                findViewById(R.id.dashboard_progress_user_deactivated_text)
            userDeactivatedDescribe = findViewById(R.id.dashboard_text_user_deactivated)
            countCandidateText = findViewById(R.id.dashboard_total_candidates)
            ongoingUserListRecycle = findViewById(R.id.dashboard_ongoing_users)
            ongoingUserListRecycle.adapter =
                DashboardOngoingUserAdapter(mutableListOf(), mutableListOf())
            progressUserSelection.progressMax = 100.0f
            progressUserDeactivated.progressMax = 100.0f
            progressUserSelection.progress = 0.0f
            progressUserDeactivated.progress = 0.0f
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        viewModel.apply {
            if (!hasFirstInitialize) {
                dataUser = requireNotNull(arguments).getParcelable(KEY_DATA)
                hasFirstInitialize = true
            }
            userDashboard.observe(this@DashboardUserFragment, Observer {
                val userProgress = (it.has_selection * 100 / it.total).toFloat()
                val deactProgress = (it.nonactivated * 100 / it.total).toFloat()
                progressUserSelection.progress = userProgress
                progressUserDeactivated.progress = deactProgress

                @SuppressLint("SetTextI18n")
                progressTextUserSelection.text = "$userProgress%"

                @SuppressLint("SetTextI18n")
                progressTextUserDeactivated.text = "$deactProgress%"

                userDeactivatedDescribe.text = getString(R.string.comparison, it.nonactivated.toString(), it.total.toString())
                userSelectionDescribe.text = getString(R.string.comparison, it.has_selection.toString(), it.total.toString())
                countCandidateText.text = getString(R.string.count_candidates, countKandidat)
            })

            listOngoingUsers.observe(this@DashboardUserFragment, Observer {
                val adapter = ongoingUserListRecycle.adapter
                if (adapter is DashboardOngoingUserAdapter) {
                    adapter.listOngoingUser.clear()
                    tempAdminDashboard?.tps?.let { tps ->
                        adapter.listTps.clear()
                        adapter.listTps.addAll(tps)
                    }
                    adapter.listOngoingUser.addAll(it)
                    adapter.notifyDataSetChanged()
                }
            })

            errorMessage.observe(this@DashboardUserFragment, Observer {
                Snackbar.make(linearLayout, it, Snackbar.LENGTH_LONG).show()
            })
        }
    }

    override fun onPause() {
        super.onPause()
        ItemClickSupport.removeFrom(ongoingUserListRecycle)
        viewModel.closeUpdate()
    }

    override fun onResume() {
        super.onResume()
        ItemClickSupport.addTo(ongoingUserListRecycle)?.onItemLongClickListener = onItemRecyclerLongClick
        viewModel.startUpdate()
    }

    companion object {
        private const val KEY_DATA = "dashboard_data"
        fun newInstance(userData: UserDataResponse): DashboardUserFragment =
            DashboardUserFragment().apply {
                arguments = Bundle().also {
                    it.putParcelable(KEY_DATA, userData)
                }
            }
    }
}