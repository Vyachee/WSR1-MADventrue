package com.grinvald.grinvaldmadventure

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.grinvald.grinvaldmadventure.Adapters.BestQuestsAdapter
import com.grinvald.grinvaldmadventure.Adapters.QuestsRatingAdapter
import com.grinvald.grinvaldmadventure.Adapters.TopAuthorAdapter
import com.grinvald.grinvaldmadventure.Adapters.TopPlayersAdapter
import com.grinvald.grinvaldmadventure.common.CacheHelper
import com.grinvald.grinvaldmadventure.models.Profile
import com.grinvald.grinvaldmadventure.models.QuestItem
import org.json.JSONObject

class Rating : Fragment() {


    lateinit var rv_quests : RecyclerView
    lateinit var rv_players : RecyclerView
    lateinit var rv_authors : RecyclerView

    lateinit var mContext : Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initViews(view: View) {
        rv_quests = view.findViewById(R.id.rv_quests)
        rv_players = view.findViewById(R.id.rv_players)
        rv_authors = view.findViewById(R.id.rv_authors)
    }

    private fun getQuests() {
        val queue = Volley.newRequestQueue(mContext)
        val request = object: StringRequest(
            Request.Method.GET,
            "http://wsk2019.mad.hakta.pro/api/quests/rating",
            Response.Listener { response ->

                val json = JSONObject(response).getJSONArray("content")

                val list: MutableList<QuestItem> = mutableListOf()

                for (i in 0..json.length() - 1) {
                    val item = json.getJSONObject(i)
                    val questItem = Gson().fromJson(item.toString(), QuestItem::class.java)
                    list.add(questItem)
                }

                val questRatingAdapter = QuestsRatingAdapter(list, mContext)
                val linearLayoutManager = LinearLayoutManager(mContext)

                rv_quests.adapter = questRatingAdapter
                rv_quests.layoutManager = linearLayoutManager


            },
            Response.ErrorListener { error ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Token"] = CacheHelper(context!!).getToken()
                return headers
            }
        }

        Log.d("DEBUG", "token: " + CacheHelper(mContext).getToken())

        queue.add(request)
    }

    private fun getPlayers() {
        val queue = Volley.newRequestQueue(mContext)
        val request = object: StringRequest(
            Request.Method.GET,
            "http://wsk2019.mad.hakta.pro/api/users/rating",
            Response.Listener { response ->

                val json = JSONObject(response).getJSONArray("content")

                val list: MutableList<Profile> = mutableListOf()

                for (i in 0..json.length() - 1) {
                    val item = json.getJSONObject(i)
                    val questItem = Gson().fromJson(item.toString(), Profile::class.java)
                    list.add(questItem)
                }

                val questRatingAdapter = TopPlayersAdapter(list, mContext)
                val linearLayoutManager = LinearLayoutManager(mContext)

                rv_players.adapter = questRatingAdapter
                rv_players.layoutManager = linearLayoutManager


            },
            Response.ErrorListener { error ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Token"] = CacheHelper(context!!).getToken()
                return headers
            }
        }

        Log.d("DEBUG", "token: " + CacheHelper(mContext).getToken())

        queue.add(request)
    }

    private fun getAuthors() {
        val queue = Volley.newRequestQueue(mContext)
        val request = object: StringRequest(
            Request.Method.GET,
            "http://wsk2019.mad.hakta.pro/api/users/authorRating",
            Response.Listener { response ->

                val json = JSONObject(response).getJSONArray("content")

                val list: MutableList<Profile> = mutableListOf()

                for (i in 0..json.length() - 1) {
                    val item = json.getJSONObject(i)
                    val questItem = Gson().fromJson(item.toString(), Profile::class.java)
                    list.add(questItem)
                }

                val questRatingAdapter = TopAuthorAdapter(list, mContext)
                val linearLayoutManager = LinearLayoutManager(mContext)

                rv_authors.adapter = questRatingAdapter
                rv_authors.layoutManager = linearLayoutManager


            },
            Response.ErrorListener { error ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Token"] = CacheHelper(context!!).getToken()
                return headers
            }
        }

        Log.d("DEBUG", "token: " + CacheHelper(mContext).getToken())

        queue.add(request)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_rating, container, false)
        initViews(view)

        getQuests()
        getPlayers()
        getAuthors()


        return view
    }

}