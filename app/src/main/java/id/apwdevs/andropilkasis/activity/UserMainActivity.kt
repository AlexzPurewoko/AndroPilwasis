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
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import id.apwdevs.andropilkasis.R
import id.apwdevs.andropilkasis.fragment.FragmentCandidateList
import id.apwdevs.andropilkasis.module.migration.Kandidat
import id.apwdevs.andropilkasis.module.viewModel.UserModeViewModel
import id.apwdevs.andropilkasis.plugin.restartApplication
import id.apwdevs.andropilkasis.plugin.showAlert
import id.apwdevs.andropilkasis.plugin.showLoadingDialog
import kotlinx.android.synthetic.main.activity_user_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UserMainActivity : AppCompatActivity(), FragmentCandidateList.FragmentCandidateCallback {

    companion object {
        private const val KEY_FRAGMENT_CANDIDATE: String = "KEY_FRAGMENT_CANDIDATE"
        private const val VALUE_COUNTDOWN: Long = 60000
    }

    private lateinit var viewModel: UserModeViewModel
    private var candidateFragment: FragmentCandidateList? = null
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_user_main)
        if (savedInstanceState != null) {
            candidateFragment = supportFragmentManager.getFragment(
                savedInstanceState,
                KEY_FRAGMENT_CANDIDATE
            ) as FragmentCandidateList
        }
        // take-out the Bundles from LoginUserOauth
        val extractedBundles = requireNotNull(intent.extras)
        viewModel = ViewModelProviders.of(this).get(UserModeViewModel::class.java)
        viewModel.dataUser = extractedBundles.getParcelable(LoginUserOauthActivity.KEY_DATA_USER)
        viewModel.dataCalon = extractedBundles.getParcelable(LoginUserOauthActivity.KEY_DATA_CALON)

        main_user_fab_submit.setOnClickListener {
            viewModel.submit()
        }
        dialog = showLoadingDialog(this)
        registerAllObservers()
    }

    private fun registerAllObservers() {
        viewModel.apply {
            loadFinished.observe(this@UserMainActivity, Observer {
                main_user_fab_submit.isEnabled = it

                if (!it)
                    dialog.show()
                else
                    dialog.dismiss()

                if (anyError) {
                    Snackbar.make(
                        main_user_content,
                        "$errorMessage",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    when (loadingType) {
                        UserModeViewModel.LoadingType.SUBMIT_DATA -> {
                            showAlert(this@UserMainActivity) { alert ->
                                alert.setTitle(getString(R.string.okay))
                                alert.setIcon(R.drawable.ic_done)
                                alert.setMessage(getString(R.string.dialog_thanks_vote))
                                alert.setPositiveButton(getString(R.string.restart)) { dialog, _ ->
                                    dialog.dismiss()
                                    logout()
                                }
                            }
                        }
                        UserModeViewModel.LoadingType.LOGOUT -> {
                            restartApplication(this@UserMainActivity)
                        }
                        null -> {
                        }
                    }
                }
            })
            title.observe(this@UserMainActivity, Observer {
                main_user_title?.text = it
            })
            subtitle.observe(this@UserMainActivity, Observer {
                main_user_tps?.text = it
            })
            showNotif.observe(this@UserMainActivity, Observer {
                Snackbar.make(main_user_content, it, Snackbar.LENGTH_INDEFINITE).apply {
                    setAction("Oke") {
                        dismiss()
                    }
                }.show()
            })
            listKandidat.observe(this@UserMainActivity, Observer {
                if (candidateFragment == null) {
                    val selectedPosition = dataUser!!.auth.data!!.pilihan
                    candidateFragment = FragmentCandidateList.newInstance(it, selectedPosition != 0)
                    if (selectedPosition > 0) {
                        GlobalScope.launch {
                            delay(500)
                            (candidateFragment as FragmentCandidateList).candidateNumberPosition =
                                selectedPosition
                            cancelCountDown()
                            launch(Dispatchers.Main) {
                                main_user_text_bottom?.text = getString(R.string.display_cannot_revote)
                            }

                            delay(3000)
                            launch(Dispatchers.Main) {
                                Snackbar.make(
                                    main_user_content,
                                    getString(R.string.display_logout_force),
                                    Snackbar.LENGTH_INDEFINITE
                                ).apply {
                                    setAction(getString(R.string.logout)) {
                                        dismiss()
                                        logout()
                                    }
                                }.show()
                            }

                            delay(2000)
                            logout()
                        }
                    }
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_user_frame, candidateFragment!!)
                    .commit()
            })
            timeTickerText.observe(this@UserMainActivity, Observer {
                main_user_time?.text = it
            })
            timeCondition.observe(this@UserMainActivity, Observer {
                when (it) {
                    UserModeViewModel.TimeCondition.CANCELLED -> {
                    }
                    UserModeViewModel.TimeCondition.RUNNING -> {
                    }
                    UserModeViewModel.TimeCondition.HAS_TO_ENDED -> {
                        sendTimeHasToBeOut()
                        // set the bg into red
                        main_user_content?.backgroundTintList =
                            ColorStateList.valueOf(Color.parseColor("#FFD50000"))
                    }
                    UserModeViewModel.TimeCondition.ENDED -> {
                        showAlert(this@UserMainActivity) { alert ->
                            alert.setMessage(getString(R.string.display_timeout))
                            alert.setTitle(getString(R.string.alert))
                            alert.setPositiveButton(getString(R.string.okay)) { d, _ ->
                                d.dismiss()
                                forceVerifyAndLogout()
                            }
                        }
                    }
                    else -> {}
                }
            })

            bottomText.observe(this@UserMainActivity, Observer {
                main_user_text_bottom.text = it
            })
            setup()
            startCountDown(VALUE_COUNTDOWN)
        }
    }

    override fun onUserLeaveHint() {
        viewModel.playSoundWhenUserLeave()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_POWER || keyCode == KeyEvent.KEYCODE_HOME)
            return true
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelBeep()
    }

    override fun onResume() {
        super.onResume()
        viewModel.cancelBeep()
    }

    override fun onBackPressed() {
        viewModel.playSingleBeep()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        candidateFragment?.apply {
            supportFragmentManager.putFragment(outState, KEY_FRAGMENT_CANDIDATE, this)
        }
    }

    override fun onClicked(dataKandidat: Kandidat) {
        viewModel.selectCard(dataKandidat.nomor_kandidat)
    }


}
