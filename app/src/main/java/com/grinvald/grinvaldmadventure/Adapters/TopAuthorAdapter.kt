package com.grinvald.grinvaldmadventure.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grinvald.grinvaldmadventure.R
import com.grinvald.grinvaldmadventure.models.Profile
import com.grinvald.grinvaldmadventure.models.QuestItem
import com.squareup.picasso.Picasso
import kotlin.math.roundToInt

class TopAuthorAdapter(questsList: MutableList<Profile>, context: Context) : RecyclerView.Adapter<TopAuthorAdapter.Holder>() {

    var questsList = questsList
    var context = context

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val v = itemView

        val iv_avatar : ImageView = v.findViewById<ImageView>(R.id.iv_avatar)
        val tv_nickname : TextView = v.findViewById<TextView>(R.id.tv_nickname)

        val iv_star_1 : ImageView = v.findViewById(R.id.iv_star_1)
        val iv_star_2 : ImageView = v.findViewById(R.id.iv_star_2)
        val iv_star_3 : ImageView = v.findViewById(R.id.iv_star_3)
        val iv_star_4 : ImageView = v.findViewById(R.id.iv_star_4)
        val iv_star_5 : ImageView = v.findViewById(R.id.iv_star_5)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.top_human, parent, false)
        return Holder(view)

    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val quest = questsList.get(position)

        holder.tv_nickname.setText("${quest.firstName} ${quest.lastName}")
        Picasso.get().load(quest.avatar).into(holder.iv_avatar)

        val stars = mutableListOf<ImageView>()
        stars.add(holder.iv_star_1)
        stars.add(holder.iv_star_2)
        stars.add(holder.iv_star_3)
        stars.add(holder.iv_star_4)
        stars.add(holder.iv_star_5)

        var rating = quest.authorRating.toDouble().roundToInt()
        if(rating > 5) rating = 5
        if(rating < 0) rating = 0
        for(x in 0 until rating) {
            stars.get(x).setImageDrawable(context.getDrawable(R.drawable.star_filled))
        }

    }

    override fun getItemCount(): Int {
        return questsList.size
    }

}
