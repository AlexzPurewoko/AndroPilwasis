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

package id.apwdevs.andropilkasis.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import id.apwdevs.andropilkasis.MyCustomApplication
import id.apwdevs.andropilkasis.activity.TesterActivity
import id.apwdevs.andropilkasis.fragment.FragmentCandidateList
import id.apwdevs.andropilkasis.module.viewModel.*
import javax.inject.Singleton

@Component
interface AppComponent {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance context: Context): AppComponent
    }

    fun inject(application: MyCustomApplication)
    fun inject(viewModel: LoginUserOauthViewModel)
    fun inject(viewModel: UserModeViewModel)
    fun inject(viewModel: DashboardViewModel)
    fun inject(viewModel: UserAdminViewModel)
    fun inject(viewModel: RealCountViewModel)
    fun inject(viewModel: AdminViewModel)
    fun inject(fragment: FragmentCandidateList)
    fun inject(activity: TesterActivity)
}