package com.grinvald.grinvaldmadventure

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.flexbox.FlexboxLayout
import com.google.gson.Gson
import com.grinvald.grinvaldmadventure.Adapters.BestQuestsAdapter
import com.grinvald.grinvaldmadventure.Adapters.SearchQuestsAdapter
import com.grinvald.grinvaldmadventure.common.CacheHelper
import com.grinvald.grinvaldmadventure.common.InternetHelper
import com.grinvald.grinvaldmadventure.models.Category
import com.grinvald.grinvaldmadventure.models.QuestItem
import org.json.JSONObject


class Quests : Fragment() {
    lateinit var mContext: Context

    lateinit var s_category : Spinner
    lateinit var ll_tags_container : FlexboxLayout
    lateinit var iv_add_tag : ImageView
    lateinit var ll_search : LinearLayout
    lateinit var rv_quests : RecyclerView

    lateinit var tv_all : TextView
    lateinit var tv_favourites : TextView

    lateinit var tv_popular : TextView
    lateinit var tv_difficult : TextView
    lateinit var tv_new : TextView
    lateinit var et_words : EditText

    lateinit var filter_category : String
    lateinit var filter_tags : MutableList<String>
    lateinit var filter_words : String
    lateinit var filter_show : String
    lateinit var filter_sort : String

    lateinit var show : MutableList<TextView>
    lateinit var sort : MutableList<TextView>

    lateinit var questList : MutableList<QuestItem>
    lateinit var filteredQuestList : MutableList<QuestItem>

    override fun onAttach(context: Context) {

        super.onAttach(context)
        mContext = context

    }

    private fun initViews(view: View) {

        s_category = view.findViewById(R.id.s_category)
        ll_tags_container = view.findViewById(R.id.ll_tags_container)
        iv_add_tag = view.findViewById(R.id.iv_add_tag)
        rv_quests = view.findViewById(R.id.rv_quests)
        ll_search = view.findViewById(R.id.ll_search)

        tv_all = view.findViewById(R.id.tv_all)
        tv_favourites = view.findViewById(R.id.tv_favourites)
        et_words = view.findViewById(R.id.et_words)

        tv_popular = view.findViewById(R.id.tv_popular)
        tv_difficult = view.findViewById(R.id.tv_difficult)
        tv_new = view.findViewById(R.id.tv_new)

        show = mutableListOf()
        sort = mutableListOf()

        show.add(tv_all)
        show.add(tv_favourites)

        sort.add(tv_popular)
        sort.add(tv_difficult)
        sort.add(tv_new)

    }

    private fun getQuests() {
        val queue = Volley.newRequestQueue(mContext)
        val request = object: StringRequest(
            Request.Method.GET,
            "http://wsk2019.mad.hakta.pro/api/quests",
            Response.Listener { response ->

                val json = JSONObject(response).getJSONArray("content")

                val list: MutableList<QuestItem> = mutableListOf()

                for (i in 0..json.length() - 1) {
                    val item = json.getJSONObject(i)
                    val questItem = Gson().fromJson(item.toString(), QuestItem::class.java)
                    list.add(questItem)
                }

                questList = list

                initSpinner(list)
                initList(list)

            },
            Response.ErrorListener { error ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Token"] = CacheHelper(context!!).getToken()
                return headers
            }
        }

        queue.add(request)
    }

    fun initList(list: MutableList<QuestItem>) {
        val adapter = SearchQuestsAdapter(list, mContext)

        var linearLayoutManager = LinearLayoutManager(mContext)

        if(resources.configuration.screenWidthDp >= 1280) {
            linearLayoutManager = GridLayoutManager(mContext, 2)
        }

        rv_quests.adapter = adapter
        rv_quests.layoutManager = linearLayoutManager
    }

    fun initSpinner(quests: MutableList<QuestItem>) {
        val categories = InternetHelper(mContext).getCategories(quests)

        val data = mutableListOf<String>()

        for(x in categories)
            if(!data.contains(x.name))
                data.add(x.name)

        val arrayAdapter = ArrayAdapter<String>(mContext, R.layout.spinner_item, data)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        s_category.prompt = "Title"
        s_category.adapter = arrayAdapter
        s_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                filter_category = data.get(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    fun initFilter() {

        filter_tags = mutableListOf()
        filter_show = "all"
        filter_words = ""
        filter_tags = mutableListOf()

        tv_all.setOnClickListener(View.OnClickListener {
            filter_show = "all"
            tv_all.background = mContext.getDrawable(R.drawable.button_default)
            tv_all.setTextColor(mContext.getColor(R.color.white))

            tv_favourites.background = mContext.getDrawable(R.drawable.button_outline)
            tv_favourites.setTextColor(mContext.getColor(R.color.aqua))
        })

        tv_favourites.setOnClickListener(View.OnClickListener {
            filter_show = "fav";

            tv_all.background = mContext.getDrawable(R.drawable.button_outline)
            tv_all.setTextColor(mContext.getColor(R.color.aqua))

            tv_favourites.background = mContext.getDrawable(R.drawable.button_default)
            tv_favourites.setTextColor(mContext.getColor(R.color.white))
        })

        tv_popular.setOnClickListener(View.OnClickListener {
            filter_sort = "popular"
            for(x in sort) {
                x.background = mContext.getDrawable(R.drawable.button_outline)
                x.setTextColor(mContext.getColor(R.color.aqua))
            }

            tv_popular.background = mContext.getDrawable(R.drawable.button_default)
            tv_popular.setTextColor(mContext.getColor(R.color.white))
        })

        tv_difficult.setOnClickListener(View.OnClickListener {
            filter_sort = "difficult"
            for(x in sort) {
                x.background = mContext.getDrawable(R.drawable.button_outline)
                x.setTextColor(mContext.getColor(R.color.aqua))
            }

            tv_difficult.background = mContext.getDrawable(R.drawable.button_default)
            tv_difficult.setTextColor(mContext.getColor(R.color.white))
        })

        tv_new.setOnClickListener(View.OnClickListener {
            filter_sort = "new"
            for(x in sort) {
                x.background = mContext.getDrawable(R.drawable.button_outline)
                x.setTextColor(mContext.getColor(R.color.aqua))
            }

            tv_new.background = mContext.getDrawable(R.drawable.button_default)
            tv_new.setTextColor(mContext.getColor(R.color.white))
        })

        ll_search.setOnClickListener(View.OnClickListener {
            filter_words = et_words.text.toString()

            filteredQuestList = mutableListOf()
            for(x in questList) {
                if(x.category.name.equals(filter_category)) {

                    var containsAllTags = true

                    for(t in filter_tags) {
                        if(!x.tags.contains(t)) {
                            containsAllTags = false
                            break
                        }
                    }

                    if(containsAllTags) {

                        if(x.name.contains(filter_words) || x.description.contains(filter_words)) {
                            if(filter_show.equals("fav")) {
                                if(CacheHelper(mContext).isInFavourites(x)) {
                                    filteredQuestList.add(x)
                                }
                            }   else {
                                filteredQuestList.add(x)
                            }
                        }

                    }
                }
            }

            initList(filteredQuestList)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_quests, container, false)
        initViews(view)

        initFilter()

        getQuests()
        iv_add_tag.setOnClickListener(View.OnClickListener {
            val layoutInflater = LayoutInflater.from(context)
            val view = layoutInflater.inflate(R.layout.tag, null, false)

            val tv_title = view.findViewById<TextView>(R.id.tv_title)

            val enterTagView = layoutInflater.inflate(R.layout.enter_tag, null, false)
            val dialog : AlertDialog = AlertDialog.Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                .setView(enterTagView)
                .setPositiveButton("ADD", DialogInterface.OnClickListener{ dialogInterface: DialogInterface, i: Int ->
                    val et_title = enterTagView.findViewById<EditText>(R.id.et_title)

                    val tagText = et_title.text.toString()
                    tv_title.text = tagText

                    filter_tags.add(tagText)

                    view.setOnClickListener(View.OnClickListener {
                        filter_tags.remove(tagText)
                        ll_tags_container.removeView(view)
                    })
                    ll_tags_container.addView(view)
                })
                .setNegativeButton("CANCEL", DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->

                })
                .create()
            dialog.setOnShowListener(DialogInterface.OnShowListener {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(mContext.getColor(R.color.aqua))
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mContext.getColor(R.color.aqua))
            })
            dialog.show()
        })

        return view

    }

}