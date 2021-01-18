package com.grinvald.grinvaldmadventure.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.grinvald.grinvaldmadventure.R
import com.grinvald.grinvaldmadventure.models.Achievement
import com.grinvald.grinvaldmadventure.models.Task
import com.squareup.picasso.Picasso

class AchievementsAdapter(list: MutableList<Achievement>, context: Context) : RecyclerView.Adapter<AchievementsAdapter.Holder>() {

    val list = list
    val context = context

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv_image : ImageView = itemView.findViewById(R.id.iv_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v = layoutInflater.inflate(R.layout.achievement_item, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list.get(position)
        Picasso.get().load(item.icon).into(holder.iv_image)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}