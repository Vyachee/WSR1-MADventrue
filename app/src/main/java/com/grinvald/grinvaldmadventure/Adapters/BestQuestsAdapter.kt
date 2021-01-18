package com.grinvald.grinvaldmadventure.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grinvald.grinvaldmadventure.R
import com.grinvald.grinvaldmadventure.models.QuestItem
import com.squareup.picasso.Picasso

class BestQuestsAdapter(questsList: MutableList<QuestItem>, context: Context) : RecyclerView.Adapter<BestQuestsAdapter.Holder>() {

    var questsList = questsList
    var context = context

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val v = itemView

        val iv_preview : ImageView = v.findViewById<ImageView>(R.id.iv_preview)
        val tv_title : TextView = v.findViewById<TextView>(R.id.tv_title)
        val tv_description : TextView = v.findViewById<TextView>(R.id.tv_description)
        val tv_details : TextView = v.findViewById<TextView>(R.id.tv_details)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.best_quest, parent, false)
        return Holder(view)

    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val quest = questsList.get(position)

        holder.tv_title.setText(quest.name)
        holder.tv_description.text = quest.description.substring(0, 100)
        Picasso.get().load(quest.mainPhoto).into(holder.iv_preview)

    }

    override fun getItemCount(): Int {
        return questsList.size
    }

}
