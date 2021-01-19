package com.grinvald.grinvaldmadventure.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grinvald.grinvaldmadventure.R
import com.grinvald.grinvaldmadventure.models.Message
import com.grinvald.grinvaldmadventure.models.QuestItem
import com.squareup.picasso.Picasso
import org.json.JSONObject
import kotlin.math.roundToInt

class ChatAdapter(questsList: MutableList<Message>, context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var questsList = questsList
    var context = context

    class OutgoingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val v = itemView

        val tv_text : TextView = v.findViewById(R.id.tv_text)
        val tv_date : TextView = v.findViewById(R.id.tv_date)

    }

    class IncomingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val v = itemView

        val iv_avatar : ImageView = v.findViewById(R.id.iv_avatar)
        val tv_nickname : TextView = v.findViewById(R.id.tv_nickname)
        val tv_text : TextView = v.findViewById(R.id.tv_text)
        val tv_date : TextView = v.findViewById(R.id.tv_date)

    }

    override fun getItemViewType(position: Int): Int {
        val message = questsList.get(position)
        if(message.isOutgoing)
            return 1
        else return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val layoutInflater = LayoutInflater.from(parent.context)
        var view = layoutInflater.inflate(R.layout.message_outgoing_item, parent, false)

        if(viewType == 1)  {
            // outgoing
            return OutgoingHolder(view)
        }   else {
            // incoming
            view = layoutInflater.inflate(R.layout.message_incoming_item, parent, false)
            return IncomingHolder(view)
        }


    }

    override fun getItemCount(): Int {
        return questsList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = questsList.get(position)


        val json = JSONObject(message.text)
        val text = json.getString("text")


        if(message.isOutgoing) {
            val h : OutgoingHolder = holder as OutgoingHolder
            h.tv_date.text = message.date
            h.tv_text.text = text
        }   else {
            val h : IncomingHolder = holder as IncomingHolder
            h.tv_date.text = message.date
            h.tv_text.text = text
            h.tv_nickname.text = message.author.name
            Picasso.get().load(message.author.avatar).into(h.iv_avatar)
        }
    }

}
