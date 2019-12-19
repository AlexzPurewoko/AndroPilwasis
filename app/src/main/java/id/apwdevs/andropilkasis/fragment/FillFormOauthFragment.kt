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

package id.apwdevs.andropilkasis.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import id.apwdevs.andropilkasis.R
import id.apwdevs.andropilkasis.callbacks.LoginCallback
import id.apwdevs.andropilkasis.callbacks.OnUserFailedLogin

class FillFormOauthFragment : Fragment(),
    OnUserFailedLogin {

    private var mLoginCb: LoginCallback? = null
    private lateinit var userNameText: EditText
    private lateinit var passwordText: EditText
    private lateinit var loginButton: Button
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mLoginCb = context as LoginCallback
    }

    override fun onDetach() {
        super.onDetach()
        mLoginCb = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.oauthlogin_fragment_fillform, container, false)

        userNameText = layout.findViewById(R.id.oauthlogin_id_username)
        passwordText = layout.findViewById(R.id.oauthlogin_id_password)
        loginButton = layout.findViewById(R.id.oauthlogin_id_submit_login)
        initializeUI()
        return layout
    }

    fun initializeUI() {
        loginButton.setOnClickListener {
            val username = userNameText.text.toString()
            val password = passwordText.text.toString()
            if (username.isEmpty()) {
                userNameText.error = "Please fill out this field"
            } else if (password.isEmpty()) {
                passwordText.error = "Please fill out this field"
            } else {
                mLoginCb?.submitLogin(username, password)
            }
        }
    }

    override fun onUserNotRegistered(message: String) {
        userNameText.post {
            userNameText.error = message
        }
    }

    override fun onUnMatchPassword(message: String) {
        passwordText.post {
            passwordText.error = message
        }
    }
}