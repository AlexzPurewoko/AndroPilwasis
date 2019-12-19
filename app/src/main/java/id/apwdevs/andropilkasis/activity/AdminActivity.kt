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

package id.apwdevs.andropilkasis.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import id.apwdevs.andropilkasis.R
import id.apwdevs.andropilkasis.fragment.FragmentCandidateList
import id.apwdevs.andropilkasis.fragment.adminActivity.CalonFragment
import id.apwdevs.andropilkasis.fragment.adminActivity.DashboardUserFragment
import id.apwdevs.andropilkasis.fragment.adminActivity.RealCountFragment
import id.apwdevs.andropilkasis.fragment.adminActivity.UserListAdminFragment
import id.apwdevs.andropilkasis.module.migration.DataKandidat
import id.apwdevs.andropilkasis.module.migration.Kandidat
import id.apwdevs.andropilkasis.module.migration.UserDataResponse
import id.apwdevs.andropilkasis.module.viewModel.AdminViewModel
import id.apwdevs.andropilkasis.plugin.restartApplication
import id.apwdevs.andropilkasis.plugin.showAlert
import id.apwdevs.andropilkasis.plugin.showLoadingDialog
import kotlinx.android.synthetic.main.activity_admin.*

class AdminActivity : AppCompatActivity(), FragmentCandidateList.FragmentCandidateCallback {

    private lateinit var mFragments: MutableList<Fragment>
    private var mPosition: Int = 0

    private lateinit var viewModel: AdminViewModel
    private lateinit var dialog: AlertDialog

    private var onPageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                bottomNavigationView.selectedItemId =
                    bottomNavigationView.menu.getItem(position).itemId
            }

        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        viewModel = ViewModelProviders.of(this).get(AdminViewModel::class.java)

        if (savedInstanceState != null) {
            var index = 0
            mFragments = mutableListOf()
            while (true) {
                val fragment = supportFragmentManager.getFragment(
                    savedInstanceState,
                    "$FRAGMENT_KEY_STATE${index++}"
                ) ?: break
                mFragments.add(fragment)
            }

            mPosition = savedInstanceState.getInt(FRAGMENT_KEY_POS)
        } else {
            intent.extras?.apply {
                @Suppress("UNCHECKED_CAST")
                val params =
                    getSerializable(LoginUserOauthActivity.KEY_DATA_PARAMS) as HashMap<String, String>
                val userData =
                    requireNotNull(getParcelable<UserDataResponse>(LoginUserOauthActivity.KEY_DATA_USER))
                val candidates =
                    requireNotNull(getParcelable<DataKandidat>(LoginUserOauthActivity.KEY_DATA_CALON))

                mFragments = mutableListOf(
                    DashboardUserFragment.newInstance(userData),
                    RealCountFragment.newInstance(userData, candidates, params),
                    UserListAdminFragment.newInstance(userData),
                    CalonFragment.newInstance(candidates)
                )

                viewModel.userData = userData
                viewModel.setUpper()
            }
            mPosition = 0
        }

        view_pager.adapter = VFragmentStatePageAdapter(supportFragmentManager, mFragments)
        view_pager.offscreenPageLimit = 10
        view_pager.setCurrentItem(mPosition, true)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    view_pager.currentItem = 0
                }
                R.id.navigation_realcount -> {
                    view_pager.currentItem = 1
                }
                R.id.navigation_user -> {
                    view_pager.currentItem = 2
                }
                R.id.navigation_candidates -> {
                    view_pager.currentItem = 3
                }
                else -> {
                }
            }
            true
        }

        dialog = showLoadingDialog(this)
        viewModel.apply {
            title.observe(this@AdminActivity, Observer {
                main_title?.text = it
            })

            subtitle.observe(this@AdminActivity, Observer {
                this@AdminActivity.subtitle.text = it
            })

            loadFinished.observe(this@AdminActivity, Observer {
                if (!it)
                    dialog.show()
                else {
                    dialog.dismiss()
                    restartApplication(this@AdminActivity)
                }
            })
        }

        admin_logout.setOnClickListener {
            showAlert(this) {
                it.setMessage(getString(R.string.dialog_out))
                it.setTitle(getString(R.string.logout))
                it.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    dialog.dismiss()
                    viewModel.logout()
                }

                it.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        view_pager.addOnPageChangeListener(onPageChangeListener)
    }

    override fun onPause() {
        super.onPause()
        view_pager.removeOnPageChangeListener(onPageChangeListener)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        for ((index, fragment) in mFragments.withIndex()) {
            supportFragmentManager.putFragment(outState, "$FRAGMENT_KEY_STATE$index", fragment)
        }
        outState.putInt(FRAGMENT_KEY_POS, mPosition)
    }

    companion object {
        private const val FRAGMENT_KEY_STATE = "FRAGMENT_KEY_STATE"
        private const val FRAGMENT_KEY_POS = "FRAGMENT_KEY_POS"
    }

    override fun onClicked(dataKandidat: Kandidat) {

    }
}


class VFragmentStatePageAdapter(
    fm: FragmentManager,
    private val fragmentList: List<Fragment>
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment = fragmentList[position]
    override fun getCount(): Int = fragmentList.size
}
