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

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import id.apwdevs.andropilkasis.R
import id.apwdevs.andropilkasis.callbacks.LoginCallback
import id.apwdevs.andropilkasis.callbacks.OnUserFailedLogin
import id.apwdevs.andropilkasis.fragment.FillFormOauthFragment
import id.apwdevs.andropilkasis.fragment.LoadingOauthFragment
import id.apwdevs.andropilkasis.module.migration.UserData
import id.apwdevs.andropilkasis.module.viewModel.LoginUserOauthViewModel
import id.apwdevs.andropilkasis.plugin.restartApplication
import id.apwdevs.andropilkasis.plugin.showAlert
import id.apwdevs.andropilkasis.plugin.showServerConfig
import kotlinx.android.synthetic.main.login_user_oauth.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginUserOauthActivity : AppCompatActivity(), LoginCallback {

    private lateinit var fragments: ArrayList<Fragment>

    companion object {
        private const val FRAG_TAGS = "login_"
        private const val CURRENT_KEY_POSITION = "CURRENT_KEY"

        const val KEY_DATA_PARAMS = "KEY_PARAMS"
        const val KEY_DATA_USER = "KEY_DATA_USER"
        const val KEY_DATA_CALON = "KEY_DATA_CALON"
    }

    private var currentFragmentPosition: Int = 0

    private lateinit var viewModel: LoginUserOauthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_user_oauth)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        viewModel = ViewModelProviders.of(this).get(LoginUserOauthViewModel::class.java)

        if (savedInstanceState != null) {
            savedInstanceState.apply {
                currentFragmentPosition = getInt(CURRENT_KEY_POSITION)
                var idx = 0
                while (true) {
                    val fragment =
                        supportFragmentManager.getFragment(this, "$FRAG_TAGS${idx++}") ?: break
                    fragments.add(fragment)
                }
            }
        } else {
            fragments = arrayListOf(
                LoadingOauthFragment(),
                FillFormOauthFragment()
            )
        }

        initializeUI()
        viewModel.loadFinished.observe(this, Observer {
            if (!it) {
                switchFragment(fragments[0], fragments[1])
                currentFragmentPosition = 0
            } else {
                switchFragment(fragments[1], fragments[0])
                currentFragmentPosition = 1
                if (viewModel.anyError) {
                    when (viewModel.errorType) {
                        LoginUserOauthViewModel.ErrorType.USER_NOT_FOUND -> {
                            (fragments[1] as OnUserFailedLogin).onUserNotRegistered(viewModel.errorMessage)
                        }
                        LoginUserOauthViewModel.ErrorType.PASSWORD_NOT_REGISTERED -> {
                            (fragments[1] as OnUserFailedLogin).onUnMatchPassword(viewModel.errorMessage)
                        }
                        LoginUserOauthViewModel.ErrorType.CANNOT_RE_ELECTION -> {
                            showAlert(this) { alert ->
                                alert.setTitle(getString(R.string.alert))
                                alert.setMessage(getString(R.string.dialog_user_not_re_election))
                                alert.setPositiveButton(getString(R.string.restart)) { dialog, _ ->
                                    dialog.dismiss()
                                    restartApplication(this)
                                }
                            }
                        }
                        LoginUserOauthViewModel.ErrorType.CANNOT_TELL_SERVER -> {
                            showAlert(this) { alert ->
                                alert.setTitle(getString(R.string.error))
                                alert.setMessage(getString(R.string.dialog_cannot_tell_server))
                                alert.setPositiveButton(getString(R.string.configure)) { dialog, _ ->
                                    dialog.dismiss()
                                    showServerConfig(this)
                                }
                                alert.setNegativeButton(getString(R.string.restart)) { dialog, _ ->
                                    dialog.dismiss()
                                    restartApplication(this)
                                }
                            }
                        }
                        LoginUserOauthViewModel.ErrorType.USER_NOT_ACTIVATED -> {
                            showAlert(this) { alert ->
                                alert.setTitle(getString(R.string.alert))
                                alert.setMessage(getString(R.string.dialog_user_not_activated))
                                alert.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                                    dialog.dismiss()
                                }
                            }
                        }
                        LoginUserOauthViewModel.ErrorType.CANNOT_RETRIEVE_CONFIGURATION -> {
                            showAlert(this) { alert ->
                                alert.setTitle(getString(R.string.alert))
                                alert.setMessage(getString(R.string.dialog_cannot_retrieve_conf))
                                alert.setNegativeButton(getString(R.string.restart)) { dialog, _ ->
                                    dialog.dismiss()
                                    restartApplication(this)
                                }
                            }
                        }
                        null -> {
                            showAlert(this) { alert ->
                                alert.setTitle(getString(R.string.alert))
                                alert.setMessage(viewModel.errorMessage)
                                alert.setNegativeButton(getString(R.string.okay)) { dialog, _ ->
                                    dialog.dismiss()
                                }
                            }
                        }
                    }

                } else {

                    if (viewModel.mode == LoginUserOauthViewModel.Mode.MODE_LOAD && !viewModel.isLogged) return@Observer

                    val user = viewModel.userData.auth.data
                    if (user?.userMode != UserData.UserMode.USER && user?.activated == true && user.pilihan == 0) {
                        showAlert(this) { alert ->
                            alert.setTitle(getString(R.string.dialog_title_admin_not_election))
                            alert.setMessage(getString(R.string.dialog_message_admin_not_election))
                            alert.setPositiveButton(getString(R.string.election_first)) { dialog, _ ->
                                dialog.dismiss()
                                startActivity(
                                    Intent(this, UserMainActivity::class.java).apply {
                                        putExtras(Bundle().also { bun ->
                                            bun.putParcelable(KEY_DATA_CALON, viewModel.dataCalon)
                                            bun.putSerializable(
                                                KEY_DATA_PARAMS,
                                                viewModel.confParams
                                            )
                                            bun.putParcelable(KEY_DATA_USER, viewModel.userData)
                                        })
                                    }
                                )
                            }
                            alert.setNegativeButton(getString(R.string.admin_mode_login)) { dialog, _ ->
                                dialog.dismiss()
                                startActivity(
                                    Intent(this, AdminActivity::class.java).apply {
                                        putExtras(Bundle().also { bun ->
                                            bun.putParcelable(KEY_DATA_CALON, viewModel.dataCalon)
                                            bun.putSerializable(
                                                KEY_DATA_PARAMS,
                                                viewModel.confParams
                                            )
                                            bun.putParcelable(KEY_DATA_USER, viewModel.userData)
                                        })
                                    }
                                )
                            }
                        }

                        return@Observer
                    }
                    startActivity(
                        Intent(
                            this,
                            if (viewModel.userData.auth.data?.userMode == UserData.UserMode.USER)
                                UserMainActivity::class.java
                            else
                                AdminActivity::class.java
                        ).apply {
                            putExtras(Bundle().also { bun ->
                                bun.putParcelable(KEY_DATA_CALON, viewModel.dataCalon)
                                bun.putSerializable(KEY_DATA_PARAMS, viewModel.confParams)
                                bun.putParcelable(KEY_DATA_USER, viewModel.userData)
                            })
                        }

                    )


                }
            }
        })

        GlobalScope.launch {
            delay(500)
            viewModel.load()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        for ((index, fragment) in fragments.withIndex()) {
            supportFragmentManager.putFragment(outState, "$FRAG_TAGS$index", fragment)
        }
        outState.putInt(CURRENT_KEY_POSITION, currentFragmentPosition)
    }

    private fun switchFragment(fragment: Fragment, hideFragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            show(fragment)
            hide(hideFragment)
        }.commit()
    }

    // Don't let users press the back button
    // just overrides it, and makes the method overrides parent
    override fun onBackPressed() {

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP && keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
            return true

        return super.onKeyDown(keyCode, event)
    }

    private fun initializeUI() {
        val colorDark = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        // Tinting status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.statusBarColor = colorDark
        // drawing a AndroPilkasis text's
        val titleText = oauthlogin_id_title.text
        val spannableString = SpannableString(titleText)
        spannableString.setSpan(
            ForegroundColorSpan(
                Color.parseColor("#B71C1C")
            ),
            5,
            titleText.length,
            0
        )
        spannableString.setSpan(
            android.text.style.StyleSpan(Typeface.BOLD),
            0, titleText.length, 0
        )
        oauthlogin_id_title.text = spannableString

        server_configs.setOnClickListener {
            showServerConfig(this)
        }

        // adding all fragments into the activity
        supportFragmentManager.beginTransaction().apply {
            for (fragment in fragments) {
                add(R.id.oauthlogin_id_frameholder, fragment)
                hide(fragment)
            }

            show(fragments[currentFragmentPosition])
        }.commit()
    }

    override fun submitLogin(username: String, password: String) {
        Log.e("Receive Login", "username : $username & password : $password")
        viewModel.submitLogin(username, password)
    }
}
