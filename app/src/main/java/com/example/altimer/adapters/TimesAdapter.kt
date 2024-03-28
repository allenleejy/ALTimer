package com.example.altimer.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.PictureDrawable
import android.text.Html
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
import com.example.altimer.SolveManager
import com.google.android.material.snackbar.Snackbar
import org.worldcubeassociation.tnoodle.puzzle.ClockPuzzle
import org.worldcubeassociation.tnoodle.puzzle.PyraminxPuzzle
import org.worldcubeassociation.tnoodle.puzzle.ThreeByThreeCubePuzzle
import org.worldcubeassociation.tnoodle.puzzle.TwoByTwoCubePuzzle
import java.nio.InvalidMarkException

class TimesAdapter(val context: Context, val slvList: ArrayList<Solve>, private val dnfButtonClickListener: TimesButtonClickListener) : RecyclerView.Adapter<TimesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.times_layout,parent,false)
        return ViewHolder(v)
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TimesAdapter.ViewHolder, position: Int) {
        holder.bindItems(slvList[position])
        holder.statDNF.setOnClickListener {
            dnfButtonClickListener.onDNFButtonClicked(slvList.size - position - 1)
            holder.statOkay.visibility = View.VISIBLE
            holder.statDNF.visibility = View.GONE
            holder.statPlusTwo.visibility = View.GONE
        }
        holder.statDelete.setOnClickListener {
            showDeleteConfirmationDialog(slvList.size - position - 1)
        }
        holder.statOkay.setOnClickListener {
            Log.d("testing", position.toString())
            dnfButtonClickListener.onOkayPressed(slvList[position].scramble)
            holder.statOkay.visibility = View.GONE
            holder.statDNF.visibility = View.VISIBLE
            holder.statPlusTwo.visibility = View.VISIBLE
        }
        holder.statPlusTwo.setOnClickListener {
            dnfButtonClickListener.onPlusTwoPressed(slvList[position].scramble)
            holder.statOkay.visibility = View.VISIBLE
            holder.statDNF.visibility = View.GONE
            holder.statPlusTwo.visibility = View.GONE
        }
        holder.statSolve.text = (slvList.size - position).toString() + "."
    }
    override fun getItemCount() = slvList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var statImage: ImageView
        var statScramble: TextView
        var statTime: TextView
        var statDNF: ImageButton
        var statDelete: ImageButton
        var statSolve : TextView
        var statPlusTwoText : TextView
        var statOkay : Button
        var statPlusTwo: Button
        init {
            statImage = itemView.findViewById(R.id.statimage)
            statScramble = itemView.findViewById(R.id.statscramble)
            statTime = itemView.findViewById(R.id.stattime)
            statDNF = itemView.findViewById(R.id.statdnf)
            statDelete = itemView.findViewById(R.id.statdelete)
            statSolve = itemView.findViewById(R.id.statsolve)
            statPlusTwoText = itemView.findViewById(R.id.plustwotext)
            statOkay = itemView.findViewById(R.id.statokay)
            statPlusTwo = itemView.findViewById(R.id.statplustwo)
        }
        @SuppressLint("SetTextI18n")
        fun bindItems(slv : Solve){

            statScramble.text = slv.scramble
            statTime.text = String.format("%.2f", slv.time)
            if (slv.penalty == "DNF") {
                statTime.text = "DNF (${String.format("%.2f", slv.time)})"
            }
            if (slv.penalty == "+2") {
                Log.d("testing", "trying")
                statPlusTwoText.visibility = View.VISIBLE
            }
            Log.d("testering", slv.event)
            if (slv.event == "3x3") {
                val cubeImage = ThreeByThreeCubePuzzle().drawScramble(slv.scramble, ThreeByThreeCubePuzzle().parseColorScheme("304FFE" + "," + "FDD835" + "," + "02D040" + "," + "EF6C00" + "," + "EC0000" + "," + "FFFFFF")).toString()
                statImage.setImageDrawable(PictureDrawable(SVG.getFromString(cubeImage).renderToPicture()))
            }
            else if (slv.event == "2x2") {
                val cubeImage = TwoByTwoCubePuzzle().drawScramble(slv.scramble, TwoByTwoCubePuzzle().parseColorScheme("304FFE" + "," + "FDD835" + "," + "02D040" + "," + "EF6C00" + "," + "EC0000" + "," + "FFFFFF")).toString()
                statImage.setImageDrawable(PictureDrawable(SVG.getFromString(cubeImage).renderToPicture()))
            }
            else if (slv.event == "Clock") {
                val cubeImage = ClockPuzzle().drawScramble(slv.scramble, ClockPuzzle().parseColorScheme("304FFE" + "," + "FDD835" + "," + "02D040" + "," + "EF6C00" + "," + "EC0000" + "," + "FFFFFF")).toString()
                statImage.setImageDrawable(PictureDrawable(SVG.getFromString(cubeImage).renderToPicture()))
                statScramble.textSize = 14.0f
            }
            else {
                val cubeImage = PyraminxPuzzle().drawScramble(slv.scramble, PyraminxPuzzle().parseColorScheme("304FFE" + "," + "FDD835" + "," + "02D040" + "," + "EF6C00" + "," + "EC0000" + "," + "FFFFFF")).toString()
                statImage.setImageDrawable(PictureDrawable(SVG.getFromString(cubeImage).renderToPicture()))
                statImage.scaleX = 0.8f
                statImage.scaleY = 0.8f
            }


            if (slv.penalty != "0") {
                statOkay.visibility = View.VISIBLE
                statDNF.visibility = View.GONE
                statPlusTwo.visibility = View.GONE
            }
        }
    }
    interface TimesButtonClickListener {
        fun onDNFButtonClicked(position: Int)
        fun onDelete(position: Int)
        fun onOkayPressed(scramble: String)
        fun onPlusTwoPressed(scramble: String)
    }
    private fun showDeleteConfirmationDialog(position: Int) {
        val alertDialogBuilder = AlertDialog.Builder(context, R.style.DeleteDialogStyle)
        val title = "<b>Confirm Deletion</b>"
        alertDialogBuilder.setTitle(Html.fromHtml(title))
        alertDialogBuilder.setMessage("Are you sure you want to delete this solve?")
        alertDialogBuilder.setPositiveButton(Html.fromHtml("<font color='#FF0000'>Delete</font>")) { dialogInterface: DialogInterface, _: Int ->
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
