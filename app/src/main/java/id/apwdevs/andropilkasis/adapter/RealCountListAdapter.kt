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

package id.apwdevs.andropilkasis.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import id.apwdevs.andropilkasis.R
import id.apwdevs.andropilkasis.module.migration.CountSelectedReal
import id.apwdevs.andropilkasis.module.migration.Kandidat

class RealCountListAdapter(
    private val listCandidates: List<Kandidat>,
    val listCounts: MutableList<CountSelectedReal>,
    val totalUser: TotalUser,
    private val urlImage: String
) : RecyclerView.Adapter<RealCountListAdapter.RViewHolder>() {

    init {
        setHasStableIds(true)
        Int
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemViewType(position: Int): Int = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RViewHolder {
        val layout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_candidate_realcount, parent, false)
        return RViewHolder(layout)
    }

    override fun getItemCount(): Int = listCandidates.size

    override fun onBindViewHolder(holder: RViewHolder, position: Int) {

        // find the matches of listCounts
        var counts: CountSelectedReal? = null
        for (count in listCounts) {
            if (count.kandidatID == listCandidates[position].nomor_kandidat) {
                counts = count
                break
            }
        }
        holder.bind(
            listCandidates[position],
            counts ?: CountSelectedReal(0, 0.0, 0), totalUser, urlImage
        )
    }

    class RViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nickname: TextView = view.findViewById(R.id.candidate_nickname)
        private val kelas: TextView = view.findViewById(R.id.candidate_kelas)
        private val num: TextView = view.findViewById(R.id.candidate_num)
        private val profile: ImageView = view.findViewById(R.id.candidate_image)
        private val others: CardView = view.findViewById(R.id.candidate_others)
        private val candidateProgress: CircularProgressBar =
            view.findViewById(R.id.candidate_progress)
        private val candidateProgressText: TextView =
            view.findViewById(R.id.candidate_progress_text)

        @SuppressLint("SetTextI18n")
        fun bind(
            candidate: Kandidat,
            countData: CountSelectedReal,
            totalUser: TotalUser,
            filePath: String
        ) {
            nickname.text = candidate.nickname
            kelas.text = candidate.kelas
            num.text = candidate.nomor_kandidat.toString()
            others.setOnClickListener {
                showOthersDialog(candidate, filePath)
            }
            candidateProgress.progress = countData.totalPercent.toFloat()
            candidateProgressText.text =
                "${countData.totalPercent.toInt()}%\n(${countData.totalSelected} dari ${totalUser.data})"

            (itemView as CardView).setCardBackgroundColor(
                when {
                    countData.totalPercent < 20 -> {
                        CardBg.RED.color
                    }
                    countData.totalPercent < 35 -> {
                        CardBg.YELLOW.color
                    }
                    countData.totalPercent < 45 && countData.totalPercent < 50 -> {
                        CardBg.GREEN.color
                    }
                    else -> {
                        CardBg.BLUE.color
                    }
                }
            )


            Glide.with(itemView.context.applicationContext)
                .load("$filePath/${candidate.avatar}")
                .into(profile)
        }

        private fun showOthersDialog(candidate: Kandidat, filePath: String) {
            AlertDialog.Builder(itemView.context).apply {
                @SuppressLint("InflateParams")
                val res = LayoutInflater.from(itemView.context)
                    .inflate(R.layout.dialog_candidate_others, null, false)

                // setting the resources
                res.findViewById<TextView>(R.id.others_candidate_name).text = candidate.nama
                res.findViewById<TextView>(R.id.others_candidate_nickname).text = candidate.nickname
                res.findViewById<TextView>(R.id.others_candidate_kelas).text = candidate.kelas
                res.findViewById<TextView>(R.id.others_candidate_visi).text = candidate.visi
                res.findViewById<TextView>(R.id.others_candidate_misi).text = candidate.misi
                res.findViewById<TextView>(R.id.others_candidate_num).text =
                    candidate.nomor_kandidat.toString()

                Glide.with(itemView.context.applicationContext)
                    .load("$filePath/${candidate.avatar}")
                    .into(res.findViewById(R.id.others_candidate_image))

                setView(res)
                setPositiveButton("Okay!") { dialog, _ -> dialog.dismiss() }
            }.show()
        }
    }

    enum class CardBg(val color: Int) {
        RED(Color.parseColor("#B71C1C")),
        YELLOW(Color.parseColor("#D14116")),
        GREEN(Color.parseColor("#00614F")),
        BLUE(Color.parseColor("#175EC9"))
    }

    data class TotalUser(
        var data: Int
    )
}