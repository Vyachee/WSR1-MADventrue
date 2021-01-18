package com.grinvald.grinvaldmadventure

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class Rating : Fragment() {


    lateinit var rv_quests : RecyclerView
    lateinit var rv_players : RecyclerView
    lateinit var rv_authors : RecyclerView

    private fun initViews(view: View) {
        rv_quests = view.findViewById(R.id.rv_quests)
        rv_players = view.findViewById(R.id.rv_players)
        rv_authors = view.findViewById(R.id.rv_authors)
    }

    private fun getQuests() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_rating, container, false)
        initViews(view)




        return view
    }

}