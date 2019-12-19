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

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import id.apwdevs.andropilkasis.MyCustomApplication
import id.apwdevs.andropilkasis.R
import id.apwdevs.andropilkasis.fragment.FragmentCandidateList
import id.apwdevs.andropilkasis.fragment.adminActivity.UserListAdminFragment
import id.apwdevs.andropilkasis.module.migration.Kandidat
import id.apwdevs.andropilkasis.module.serverResponse.GetCalon
import id.apwdevs.andropilkasis.module.serverResponse.GetParams
import id.apwdevs.andropilkasis.module.serverResponse.UserLogin
import kotlinx.android.synthetic.main.activity_tester.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class TesterActivity : AppCompatActivity(), FragmentCandidateList.FragmentCandidateCallback {

    @Inject
    lateinit var getCalon: GetCalon

    @Inject
    lateinit var login: UserLogin

    @Inject
    lateinit var params: GetParams

    lateinit var snack: Snackbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tester)
        (application as MyCustomApplication).appComponent.inject(this)

        snack = Snackbar.make(tester, "Loading", Snackbar.LENGTH_INDEFINITE)
        snack.show()
        GlobalScope.launch {
            val o = login.loginAsync("Brian", "vfre4t342", true).await()
            //val p = getCalon.getCalon().await()
            //val q = params.getAll().await()

            launch {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.tester, UserListAdminFragment.newInstance(o))//, p, q!!))
                    .commit()
                snack.dismiss()
            }

        }
    }

    override fun onClicked(dataKandidat: Kandidat) {
        Toast.makeText(
            this,
            "${dataKandidat.nama} -> ${dataKandidat.nomor_kandidat}",
            Toast.LENGTH_LONG
        ).show()
    }
}
