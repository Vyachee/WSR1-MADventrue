package com.grinvald.grinvaldmadventure.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grinvald.grinvaldmadventure.R
import com.grinvald.grinvaldmadventure.models.Task

class TasksAdapter(list: MutableList<Task>, context: Context) : RecyclerView.Adapter<TasksAdapter.Holder>() {

    val list = list
    val context = context

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_title : TextView = itemView.findViewById(R.id.tv_title)
        val tv_description : TextView = itemView.findViewById(R.id.tv_description)
        val iv_status : ImageView = itemView.findViewById(R.id.iv_status)
        val iv_line : ImageView = itemView.findViewById(R.id.iv_line)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v = layoutInflater.inflate(R.layout.task_item, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list.get(position)
        holder.tv_description.text = "Find next clue here..."
        holder.tv_title.text = item.name
        if(item.status.equals("NOT_STARTED")) {
            holder.iv_status.setImageDrawable(context.getDrawable(R.drawable.task_progress))
        }

        if(position == list.size - 1) {
            holder.iv_line.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}