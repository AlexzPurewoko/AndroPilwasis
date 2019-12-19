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
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import id.apwdevs.andropilkasis.MyCustomApplication
import id.apwdevs.andropilkasis.R
import id.apwdevs.andropilkasis.adapter.fragmentCandidate.RecyclerViewCandidateAdapter
import id.apwdevs.andropilkasis.adapter.fragmentCandidate.RecyclerViewCandidateAdapter.KandidatView
import id.apwdevs.andropilkasis.module.ServerModule
import id.apwdevs.andropilkasis.module.migration.DataKandidat
import id.apwdevs.andropilkasis.module.migration.Kandidat
import id.apwdevs.andropilkasis.params.PublicConfig
import id.apwdevs.andropilkasis.plugin.ItemClickSupport
import javax.inject.Inject

class FragmentCandidateList : Fragment() {

    @Inject
    lateinit var serverModule: ServerModule

    private lateinit var recyclerView: RecyclerView

    private var onItemClick: FragmentCandidateCallback? = null

    private var viewModel: FragmentCandidateViewModel? = null

    var candidateNumberPosition: Int = 0
        get() = viewModel?.selectedCandidatePosition?.value ?: 0
        set(value) {
            viewModel?.selectedCandidatePosition?.postValue(value)
            field = value
        }


    private var onItemClickListenerSupport: ItemClickSupport.OnItemClickListener =
        object : ItemClickSupport.OnItemClickListener {
            override fun onItemClicked(recyclerView: RecyclerView, position: Int, v: View) {
                if (viewModel?.disableClick == true) return
                val vh =
                    recyclerView.getChildViewHolder(v) as RecyclerViewCandidateAdapter.RViewHolder
                val selectedData = vh.tempCandidateView ?: return

                candidateNumberPosition = selectedData.data.nomor_kandidat

                onItemClick?.onClicked(selectedData.data.copy())
            }

        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onItemClick = context as FragmentCandidateCallback
        (context.applicationContext as MyCustomApplication).appComponent.inject(this)
    }

    override fun onDetach() {
        super.onDetach()
        onItemClick = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_candidate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val candidateData =
            requireNotNull(arguments).getParcelable<DataKandidat>(KEY_PARAM_DATA_CALON)

        viewModel = ViewModelProviders.of(this).get(FragmentCandidateViewModel::class.java)
        viewModel?.apply {
            disableClick = requireNotNull(arguments).getBoolean(KEY_PARAM_DISABLE_CLICK)

            // selects by number candidates
            selectedCandidatePosition.observe(this@FragmentCandidateList, Observer { selected ->
                if (recyclerView.adapter !is RecyclerViewCandidateAdapter) return@Observer
                val rAdapter = recyclerView.adapter as RecyclerViewCandidateAdapter
                rAdapter.listCandidates.forEach {
                    it.isSelected = selected == it.data.nomor_kandidat
                }
                rAdapter.notifyDataSetChanged()
            })
        }

        val buildedCandidate = mutableListOf<KandidatView>()
        candidateData!!.kandidat.forEach {
            buildedCandidate.add(KandidatView(it, false))
        }
        recyclerView = view as RecyclerView
        recyclerView.adapter = RecyclerViewCandidateAdapter(
            buildedCandidate,
            "${serverModule.serverProtocol}${serverModule.serverHost}/${PublicConfig.ServerConfig.DEF_BASE_DIR}/kandidat_img"
        )

    }

    override fun onResume() {
        super.onResume()
        ItemClickSupport.addTo(recyclerView)?.onItemClickListener = onItemClickListenerSupport
    }

    override fun onPause() {
        super.onPause()
        ItemClickSupport.removeFrom(recyclerView)
    }

    interface FragmentCandidateCallback {
        fun onClicked(dataKandidat: Kandidat)
    }

    companion object {
        private const val KEY_PARAM_DATA_CALON = "KEY_PARAM_DATA_CALON"
        private const val KEY_PARAM_DISABLE_CLICK = "KEY_PARAM_DISABLE_CLICK"

        @JvmStatic
        fun newInstance(
            candidateData: DataKandidat,
            disableClick: Boolean = false
        ): FragmentCandidateList =
            FragmentCandidateList().apply {
                arguments = Bundle().also {
                    it.putParcelable(KEY_PARAM_DATA_CALON, candidateData)
                    it.putBoolean(KEY_PARAM_DISABLE_CLICK, disableClick)
                }
            }
    }
}

class FragmentCandidateViewModel : ViewModel() {
    var disableClick: Boolean = false
    val selectedCandidatePosition: MutableLiveData<Int> = MutableLiveData()
}