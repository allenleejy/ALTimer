package com.example.altimer.adapters

import android.graphics.drawable.PictureDrawable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.ViewGroup
import android.widget.ImageView
import android.view.LayoutInflater
import com.caverock.androidsvg.SVG
import com.example.altimer.R
import com.example.altimer.Solve
import com.google.android.material.snackbar.Snackbar
import org.worldcubeassociation.tnoodle.puzzle.ThreeByThreeCubePuzzle

class TimesAdapter(val slvList: ArrayList<Solve>) : RecyclerView.Adapter<TimesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.times_layout,parent,false)
        return ViewHolder(v)
    }
    override fun onBindViewHolder(holder: TimesAdapter.ViewHolder, position: Int) {
        holder.bindItems(slvList[position])
    }
    override fun getItemCount() = slvList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var statImage: ImageView
        var statScramble: TextView
        var statTime: TextView

        init {
            statImage = itemView.findViewById(R.id.statimage)
            statScramble = itemView.findViewById(R.id.statscramble)
            statTime = itemView.findViewById(R.id.stattime)

            itemView.setOnClickListener{ view ->
                val pos = adapterPosition +1
                Snackbar.make(view, "Click detected on item $pos", Snackbar.LENGTH_LONG)
                    .setAction("Action",null).show()
            }
        }
        fun bindItems(slv : Solve){
            statScramble.text = slv.scramble
            statTime.text = String.format("%.2f", slv.time)
            if (slv.penalty == "DNF") {
                statTime.text = "DNF (${slv.time})"
            }
            val cubeImage = ThreeByThreeCubePuzzle().drawScramble(slv.scramble, ThreeByThreeCubePuzzle().parseColorScheme("304FFE" + "," + "FDD835" + "," + "02D040" + "," + "EF6C00" + "," + "EC0000" + "," + "FFFFFF")).toString()
            statImage.setImageDrawable(PictureDrawable(SVG.getFromString(cubeImage).renderToPicture()))
        }
    }
}
