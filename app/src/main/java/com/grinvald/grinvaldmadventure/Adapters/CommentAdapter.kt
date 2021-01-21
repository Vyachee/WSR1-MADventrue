package com.grinvald.grinvaldmadventure.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.grinvald.grinvaldmadventure.QuestDetail
import com.grinvald.grinvaldmadventure.R
import com.grinvald.grinvaldmadventure.models.Comment
import com.grinvald.grinvaldmadventure.models.Task
import com.squareup.picasso.Picasso

class CommentAdapter(list: MutableList<Comment>, context: Context) : RecyclerView.Adapter<CommentAdapter.Holder>() {

    var list = list
    var context = context


    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_avatar : ImageView
        var iv_star_1 : ImageView
        var iv_star_2 : ImageView
        var iv_star_3 : ImageView
        var iv_star_4 : ImageView
        var iv_star_5 : ImageView

        var tv_nickname : TextView
        var tv_date : TextView
        var tv_text : TextView

        lateinit var stars_list : MutableList<ImageView>

        init {
            iv_avatar = itemView.findViewById(R.id.iv_avatar)
            iv_star_1 = itemView.findViewById(R.id.iv_star_1)
            iv_star_2 = itemView.findViewById(R.id.iv_star_2)
            iv_star_3 = itemView.findViewById(R.id.iv_star_3)
            iv_star_4 = itemView.findViewById(R.id.iv_star_4)
            iv_star_5 = itemView.findViewById(R.id.iv_star_5)
            tv_nickname = itemView.findViewById(R.id.tv_nickname)
            tv_date = itemView.findViewById(R.id.tv_date)
            tv_text = itemView.findViewById(R.id.tv_text)

            stars_list = mutableListOf()

            stars_list.add(iv_star_1)
            stars_list.add(iv_star_2)
            stars_list.add(iv_star_3)
            stars_list.add(iv_star_4)
            stars_list.add(iv_star_5)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v = layoutInflater.inflate(R.layout.comment_item, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list.get(position)

        Picasso.get().load(item.author.avatar).into(holder.iv_avatar)
        holder.tv_text.text = item.text
        holder.tv_nickname.text = item.author.name
        holder.tv_date.text = item.date

        for(x in 0 until item.rating.toInt()) {
            holder.stars_list.get(x).setImageDrawable(context.getDrawable(R.drawable.star_filled))
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}