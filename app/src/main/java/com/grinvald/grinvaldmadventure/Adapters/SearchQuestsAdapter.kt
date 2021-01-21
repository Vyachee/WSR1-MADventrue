package com.grinvald.grinvaldmadventure.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grinvald.grinvaldmadventure.MainScreen
import com.grinvald.grinvaldmadventure.QuestDetail
import com.grinvald.grinvaldmadventure.R
import com.grinvald.grinvaldmadventure.common.CacheHelper
import com.grinvald.grinvaldmadventure.models.QuestItem
import com.squareup.picasso.Picasso

class SearchQuestsAdapter(questsList: MutableList<QuestItem>, context: Context) : RecyclerView.Adapter<SearchQuestsAdapter.Holder>() {

    var questsList = questsList
    var context = context

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val v = itemView

        var iv_preview : ImageView?
        var tv_title : TextView?
        var tv_description : TextView?
        var tv_details : TextView?
        var iv_like : ImageView?

        init {
            iv_preview = v.findViewById(R.id.iv_preview)
            tv_title = v.findViewById(R.id.tv_title)
            tv_description = v.findViewById(R.id.tv_description)
            tv_details = v.findViewById(R.id.tv_details)
            iv_like = v.findViewById(R.id.iv_like)

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.search_quest, parent, false)
        return Holder(view)

    }

    override fun onBindViewHolder(holder: Holder, position: Int) {

        val quest = questsList.get(position)

        holder.tv_title!!.setText(quest.name)
        var description = quest.description
        if(description.length > 100) description = description.substring(0, 100)
        holder.tv_description!!.text = description

        val cacheHelper = CacheHelper(context)
        var isFavourite = cacheHelper.isInFavourites(quest)

        if(isFavourite) {
            holder.iv_like!!.setImageDrawable(context.getDrawable(R.drawable.unlike))
        }

        Picasso.get().load(quest.mainPhoto).into(holder.iv_preview)

        holder.tv_details!!.setOnClickListener(View.OnClickListener {

            val fragment = QuestDetail()
            val extras = Bundle()
            extras.putSerializable("quest", quest)
            fragment.arguments = extras

            (context as MainScreen).changeFragment(fragment)
        })

        holder.iv_like!!.setOnClickListener(View.OnClickListener {
            if(isFavourite) {
                holder.iv_like!!.setImageDrawable(context.getDrawable(R.drawable.favourites))
                cacheHelper.removeFromFavourites(quest)
                isFavourite = false
            }   else {
                holder.iv_like!!.setImageDrawable(context.getDrawable(R.drawable.unlike))
                cacheHelper.addToFavourites(quest)
                isFavourite = true
            }
        })

    }

    override fun getItemCount(): Int {
        return questsList.size
    }

}
