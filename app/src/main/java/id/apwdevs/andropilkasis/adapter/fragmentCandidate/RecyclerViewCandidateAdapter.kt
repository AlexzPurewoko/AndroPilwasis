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

package id.apwdevs.andropilkasis.adapter.fragmentCandidate

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.apwdevs.andropilkasis.R
import id.apwdevs.andropilkasis.module.migration.Kandidat

class RecyclerViewCandidateAdapter(
    val listCandidates: List<KandidatView>,
    private val urlImage: String
) : RecyclerView.Adapter<RecyclerViewCandidateAdapter.RViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemViewType(position: Int): Int = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_candidate_custom, parent, false)
        return RViewHolder(layout)
    }

    override fun getItemCount(): Int = listCandidates.size

    override fun onBindViewHolder(holder: RViewHolder, position: Int) {
        holder.bind(listCandidates[position], urlImage)
    }

    class RViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nickname: TextView = view.findViewById(R.id.candidate_nickname)
        private val kelas: TextView = view.findViewById(R.id.candidate_kelas)
        private val num: TextView = view.findViewById(R.id.candidate_num)
        private val profile: ImageView = view.findViewById(R.id.candidate_image)
        private val others: CardView = view.findViewById(R.id.candidate_others)
        private val background: ImageView = view.findViewById(R.id.candidate_bg)

        var tempCandidateView: KandidatView? = null

        fun bind(candidate: KandidatView, filePath: String) {
            tempCandidateView = candidate
            nickname.text = candidate.data.nickname
            kelas.text = candidate.data.kelas
            num.text = candidate.data.nomor_kandidat.toString()
            others.setOnClickListener {
                showOthersDialog(candidate, filePath)
            }

            background.setImageResource(
                if (candidate.isSelected) R.drawable.selected_card_candidate else R.drawable.unselected_card_candidate
            )

            Glide.with(itemView.context.applicationContext)
                .load("$filePath/${candidate.data.avatar}")
                .into(profile)
        }

        private fun showOthersDialog(candidate: KandidatView, filePath: String) {
            AlertDialog.Builder(itemView.context).apply {

                @SuppressLint("InflateParams")
                val res = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.dialog_candidate_others, null, false)

                // setting the resources
                res.findViewById<TextView>(R.id.others_candidate_name).text = candidate.data.nama
                res.findViewById<TextView>(R.id.others_candidate_nickname).text =
                    candidate.data.nickname
                res.findViewById<TextView>(R.id.others_candidate_kelas).text = candidate.data.kelas
                res.findViewById<TextView>(R.id.others_candidate_visi).text = candidate.data.visi
                res.findViewById<TextView>(R.id.others_candidate_misi).text = candidate.data.misi
                res.findViewById<TextView>(R.id.others_candidate_num).text =
                    candidate.data.nomor_kandidat.toString()

                Glide.with(itemView.context.applicationContext)
                    .load("$filePath/${candidate.data.avatar}")
                    .into(res.findViewById(R.id.others_candidate_image))

                setView(res)
                setPositiveButton("Okay!") { dialog, _ -> dialog.dismiss() }
            }.show()
        }
    }

    data class KandidatView(
        val data: Kandidat,
        var isSelected: Boolean
    )
}