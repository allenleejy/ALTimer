package com.example.altimer.adapters

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.altimer.Algorithm
import com.example.altimer.R
import com.example.altimer.Solve

class AlgAdapter(val context: Context, val algList: ArrayList<Algorithm>) : RecyclerView.Adapter<AlgAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlgAdapter.ViewHolder {
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.alg_layout,parent,false)
        return AlgAdapter.ViewHolder(v)
    }
    override fun onBindViewHolder(holder: AlgAdapter.ViewHolder, position: Int) {
        holder.bindItems(algList[position])
        val resourceId = context.resources.getIdentifier(algList[position].image, "drawable", context.packageName)
        holder.algImage.setImageResource(resourceId)

        holder.itemView.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.alg_dialog)

            val dialogAlgImage = dialog.findViewById<ImageView>(R.id.dialogAlgImage)
            val dialogAlgText = dialog.findViewById<TextView>(R.id.dialogAlgText)
            val dialogAlgName = dialog.findViewById<TextView>(R.id.dialogAlgName)

            dialog.window?.setBackgroundDrawableResource(R.drawable.deletedialogbackground)

            val alg = algList[position]
            val algImageResourceId = context.resources.getIdentifier(alg.image, "drawable", context.packageName)

            dialogAlgImage.setImageResource(algImageResourceId)
            dialogAlgText.text = algList.get(position).algorithm
            dialogAlgName.text = algList.get(position).name

            dialog.show()
        }
    }
    override fun getItemCount() = algList.size
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var algImage: ImageView
        var algName: TextView
        init {
            algImage = itemView.findViewById(R.id.algimage)
            algName = itemView.findViewById(R.id.algname)
        }
        fun bindItems(alg : Algorithm){
            algName.text = alg.name
        }
    }
}