package com.example.altimer.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.PictureDrawable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.ViewGroup
import android.widget.ImageView
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import com.caverock.androidsvg.SVG
import com.example.altimer.R
import com.example.altimer.Solve
import com.google.android.material.snackbar.Snackbar
import org.worldcubeassociation.tnoodle.puzzle.ThreeByThreeCubePuzzle
import java.nio.InvalidMarkException

class TimesAdapter(val context: Context, val slvList: ArrayList<Solve>, private val dnfButtonClickListener: TimesButtonClickListener) : RecyclerView.Adapter<TimesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.times_layout,parent,false)
        return ViewHolder(v)
    }
    override fun onBindViewHolder(holder: TimesAdapter.ViewHolder, position: Int) {
        holder.bindItems(slvList[position])
        holder.statDNF.setOnClickListener {
            dnfButtonClickListener.onDNFButtonClicked(slvList.size - position - 1)
        }
        holder.statDelete.setOnClickListener {
            showDeleteConfirmationDialog(slvList.size - position - 1)
        }
    }
    override fun getItemCount() = slvList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var statImage: ImageView
        var statScramble: TextView
        var statTime: TextView
        var statDNF: ImageButton
        var statDelete: ImageButton
        init {
            statImage = itemView.findViewById(R.id.statimage)
            statScramble = itemView.findViewById(R.id.statscramble)
            statTime = itemView.findViewById(R.id.stattime)
            statDNF = itemView.findViewById(R.id.statdnf)
            statDelete = itemView.findViewById(R.id.statdelete)
            itemView.setOnClickListener{ view ->
                val pos = adapterPosition +1
                Snackbar.make(view, "Click detected on item $pos", Snackbar.LENGTH_LONG)
                    .setAction("Action",null).show()
            }
        }
        @SuppressLint("SetTextI18n")
        fun bindItems(slv : Solve){
            statScramble.text = slv.scramble
            statTime.text = String.format("%.2f", slv.time)
            if (slv.penalty == "DNF") {
                statTime.text = "DNF (${String.format("%.2f", slv.time)})"
            }
            val cubeImage = ThreeByThreeCubePuzzle().drawScramble(slv.scramble, ThreeByThreeCubePuzzle().parseColorScheme("304FFE" + "," + "FDD835" + "," + "02D040" + "," + "EF6C00" + "," + "EC0000" + "," + "FFFFFF")).toString()
            statImage.setImageDrawable(PictureDrawable(SVG.getFromString(cubeImage).renderToPicture()))
        }
    }
    interface TimesButtonClickListener {
        fun onDNFButtonClicked(position: Int)
        fun onDelete(position: Int)
    }
    private fun showDeleteConfirmationDialog(position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(context, R.style.DeleteDialogStyle)
        alertDialogBuilder.setTitle("Confirm Deletion")
        alertDialogBuilder.setMessage("Are you sure you want to delete this solve?")
        alertDialogBuilder.setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            dnfButtonClickListener.onDelete(position)
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
