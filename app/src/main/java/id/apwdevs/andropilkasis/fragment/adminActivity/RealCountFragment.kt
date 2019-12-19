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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import id.apwdevs.andropilkasis.R
import id.apwdevs.andropilkasis.adapter.RealCountListAdapter
import id.apwdevs.andropilkasis.adapter.RealCountListAdapter.TotalUser
import id.apwdevs.andropilkasis.module.migration.DataKandidat
import id.apwdevs.andropilkasis.module.migration.UserDataResponse
import id.apwdevs.andropilkasis.module.viewModel.RealCountViewModel
import id.apwdevs.andropilkasis.params.PublicConfig

class RealCountFragment : Fragment() {

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var mRecyclerRealCount: RecyclerView


    // user selected
    private lateinit var progressUserNotSelection: CircularProgressBar
    private lateinit var progressTextUserNotSelection: TextView
    private lateinit var userNotSelectionDescribe: TextView


    // user deactivated
    private lateinit var progressUserDeactivated: CircularProgressBar
    private lateinit var progressTextUserDeactivated: TextView
    private lateinit var userDeactivatedDescribe: TextView

    private lateinit var viewModel: RealCountViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_realcount, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = view as SwipeRefreshLayout
        view.apply {
            mRecyclerRealCount = findViewById(R.id.fragment_realcount_candidate_list)
            progressUserNotSelection = findViewById(R.id.realcount_progress_user_election)
            progressTextUserNotSelection = findViewById(R.id.realcount_progress_user_election_text)
            userNotSelectionDescribe = findViewById(R.id.realcount_text_user)

            progressUserDeactivated = findViewById(R.id.realcount_progress_user_deactivated)
            progressTextUserDeactivated =
                findViewById(R.id.realcount_progress_user_deactivated_text)
            userDeactivatedDescribe = findViewById(R.id.realcount_text_user_deactivated)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RealCountViewModel::class.java)
        viewModel.apply {
            requireArguments().apply {
                dataUser = getParcelable(KEY_DATA_USER)
                @Suppress("UNCHECKED_CAST")
                parameters = getSerializable(KEY_PARAMETERS) as HashMap<String, String>
                candidate = getParcelable(KEY_DATA_CANDIDATE)
            }

            mRecyclerRealCount.adapter = RealCountListAdapter(
                viewModel.candidate!!.kandidat,
                mutableListOf(),
                TotalUser(0),
                "${serverModule.serverProtocol}${serverModule.serverHost}/${PublicConfig.ServerConfig.DEF_BASE_DIR}/kandidat_img"
            )

            loadFinished.observe(this@RealCountFragment, Observer {
                swipeRefreshLayout.isRefreshing = !it
            })

            errorMessage.observe(this@RealCountFragment, Observer {
                Snackbar.make(swipeRefreshLayout, it, Snackbar.LENGTH_LONG).show()
            })

            listCandidateRealCount.observe(this@RealCountFragment, Observer {
                val adapter = mRecyclerRealCount.adapter
                if (adapter is RealCountListAdapter) {
                    it.countSelected?.apply {
                        adapter.listCounts.clear()
                        adapter.listCounts.addAll(this)
                        it.countUser?.let { t ->
                            if (t.isNotEmpty()) {
                                adapter.totalUser.data = t.toInt()
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }

                }

                // golput setting
                it.golput?.apply {
                    progressUserNotSelection.progress = notSelectedPercent.toFloat()

                    @SuppressLint("SetTextI18n")
                    progressTextUserNotSelection.text = "${notSelectedPercent.toInt()}%"

                    userNotSelectionDescribe.text = getString(R.string.comparison, notSelected.toString(), it.countUser.toString())//"$notSelected dari ${it.countUser}"

                    progressUserDeactivated.progress = deactivatedCountPercent.toFloat()

                    @SuppressLint("SetTextI18n")
                    progressTextUserDeactivated.text = "${deactivatedCountPercent.toInt()}%"

                    userDeactivatedDescribe.text = getString(R.string.comparison, deactivatedCount.toString(), it.countUser.toString())//"$deactivatedCount dari ${it.countUser}"
                }
            })

            swipeRefreshLayout.setOnRefreshListener {
                load()
            }

            load()
        }
    }

    companion object {
        private const val KEY_PARAMETERS: String = "PARAMETERS"
        private const val KEY_DATA_CANDIDATE = "CANDIDATES"
        private const val KEY_DATA_USER = "USER_DATA"

        @JvmStatic
        fun newInstance(
            dataUser: UserDataResponse,
            dataKandidat: DataKandidat,
            parameters: HashMap<String, String>
        ): RealCountFragment =
            RealCountFragment().apply {
                arguments = Bundle().also {
                    it.putParcelable(KEY_DATA_CANDIDATE, dataKandidat)
                    it.putParcelable(KEY_DATA_USER, dataUser)
                    it.putSerializable(KEY_PARAMETERS, parameters)
                }
            }
    }
}