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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import id.apwdevs.andropilkasis.R

class LoadingOauthFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.oauthlogin_fragment_loading, container, false)
}