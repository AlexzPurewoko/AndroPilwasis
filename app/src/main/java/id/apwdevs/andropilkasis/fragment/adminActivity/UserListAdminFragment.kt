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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import id.apwdevs.andropilkasis.R
import id.apwdevs.andropilkasis.adapter.UserActivateEditAdapter
import id.apwdevs.andropilkasis.adapter.UserActivateEditAdapter.UserActivateEdit
import id.apwdevs.andropilkasis.module.migration.UserDataResponse
import id.apwdevs.andropilkasis.module.viewModel.UserAdminViewModel
import org.angmarch.views.NiceSpinner
import org.angmarch.views.OnSpinnerItemSelectedListener

class UserListAdminFragment : Fragment(), OnSpinnerItemSelectedListener {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var userTotal: TextView
    private lateinit var userDeactivatedTotal: TextView
    private lateinit var userAdminTPS: NiceSpinner
    private lateinit var mUserRecyler: RecyclerView
    private lateinit var cardSave: CardView

    private lateinit var viewModel: UserAdminViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_user_admin, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.apply {
            swipeRefreshLayout = this as SwipeRefreshLayout
            userTotal = findViewById(R.id.useradmin_totalusers)
            userDeactivatedTotal = findViewById(R.id.useradmin_userdeactivated)
            userAdminTPS = findViewById(R.id.useradmin_tps)
            mUserRecyler = findViewById(R.id.useradmin_tps_recycler)
            cardSave = findViewById(R.id.useradmin_deact_save)

            mUserRecyler.adapter = UserActivateEditAdapter(mutableListOf(), mutableListOf())
            userAdminTPS.onSpinnerItemSelectedListener = this@UserListAdminFragment
            cardSave.setOnClickListener {
                val adapter = mUserRecyler.adapter
                if (adapter is UserActivateEditAdapter) {
                    viewModel.submitSaveUserCondition(adapter.listUsers)
                }
            }

            swipeRefreshLayout.setOnRefreshListener {
                viewModel.loadData()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(UserAdminViewModel::class.java)
        viewModel.apply {
            viewModel.dataUser = requireArguments().getParcelable(KEY_DATA)

            viewModel.totalUsers.observe(this@UserListAdminFragment, Observer {
                userTotal.text = it.toString()
            })

            viewModel.totalUsersDeactivated.observe(this@UserListAdminFragment, Observer {
                userDeactivatedTotal.text = it.toString()
            })

            viewModel.listTps.observe(this@UserListAdminFragment, Observer {
                userAdminTPS.attachDataSource(it)
            })

            viewModel.listsUsers.observe(this@UserListAdminFragment, Observer {
                val adapter = mUserRecyler.adapter
                if (adapter is UserActivateEditAdapter) {
                    if (it == null || viewModel.listOriginalTPS.isEmpty()) return@Observer
                    adapter.dataTps.clear()
                    adapter.listUsers.clear()
                    adapter.dataTps.addAll(listOriginalTPS)

                    val out = mutableListOf<UserActivateEdit>()
                    for (user in it) {
                        out.add(UserActivateEdit(user.copy(), user.activated))
                    }
                    adapter.listUsers.addAll(out)
                    adapter.notifyDataSetChanged()
                    userAdminTPS.selectedIndex = leastPositionSpinner
                }
            })

            viewModel.loadFinished.observe(this@UserListAdminFragment, Observer {
                swipeRefreshLayout.isRefreshing = !it
            })

            errorMessage.observe(this@UserListAdminFragment, Observer {
                Snackbar.make(swipeRefreshLayout, it, Snackbar.LENGTH_LONG).show()
            })

            loadData()
        }
    }

    override fun onItemSelected(parent: NiceSpinner?, view: View?, position: Int, id: Long) {
        viewModel.filterListsInTps(position)
    }

    companion object {
        private const val KEY_DATA = "dashboard_data"
        fun newInstance(userData: UserDataResponse): UserListAdminFragment =
            UserListAdminFragment().apply {
                arguments = Bundle().also {
                    it.putParcelable(KEY_DATA, userData)
                }
            }
    }

}