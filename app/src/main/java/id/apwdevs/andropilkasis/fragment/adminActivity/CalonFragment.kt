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
import androidx.fragment.app.Fragment
import id.apwdevs.andropilkasis.R
import id.apwdevs.andropilkasis.fragment.FragmentCandidateList
import id.apwdevs.andropilkasis.module.migration.DataKandidat
import id.apwdevs.andropilkasis.module.migration.Kandidat

class CalonFragment : Fragment(), FragmentCandidateList.FragmentCandidateCallback {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        LayoutInflater.from(inflater.context).inflate(
            R.layout.fragment_calon_admin,
            container,
            false
        )

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val arg = requireNotNull(arguments?.getParcelable<DataKandidat>(KEY_DATA_CALON))

            childFragmentManager.beginTransaction().apply {
                replace(R.id.calon_list_frame, FragmentCandidateList.newInstance(arg))
            }.commit()
    }

    companion object {
        private const val KEY_DATA_CALON = "KEY_DATA_CALON"
        fun newInstance(kandidat: DataKandidat): CalonFragment =
            CalonFragment().apply {
                arguments = Bundle().also {
                    it.putParcelable(KEY_DATA_CALON, kandidat)
                }
            }
    }

    override fun onClicked(dataKandidat: Kandidat) {

    }
}