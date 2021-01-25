package com.grinvald.grinvaldmadventure

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.grinvald.grinvaldmadventure.Adapters.AchievementsAdapter
import com.grinvald.grinvaldmadventure.Adapters.BestQuestsAdapter
import com.grinvald.grinvaldmadventure.Adapters.QuestsRatingAdapter
import com.grinvald.grinvaldmadventure.common.CacheHelper
import com.grinvald.grinvaldmadventure.models.Profile
import com.squareup.picasso.Picasso
import org.json.JSONObject
import kotlin.math.roundToInt


class Profile : Fragment(), LocationListener {

    lateinit var mContext : Context
    lateinit var geocoder : Geocoder
    lateinit var currentCity : String
    private lateinit var profile : Profile

    // views
    lateinit var rv_solved : RecyclerView
    lateinit var rv_created : RecyclerView
    lateinit var rv_achievements : RecyclerView

    lateinit var tv_name : TextView
    lateinit var tv_nickname : TextView
    lateinit var tv_level : TextView
    lateinit var tv_change_password : TextView
    lateinit var tv_email : TextView
    lateinit var tv_city : TextView

    lateinit var cv_achievements : CardView

    lateinit var ll_edit_name : LinearLayout
    lateinit var ll_view_name : LinearLayout
    lateinit var ll_view_nickname : LinearLayout
    lateinit var ll_edit_nickname : LinearLayout

    lateinit var et_firstname : EditText
    lateinit var et_lastname : EditText

    lateinit var iv_avatar : ImageView
    lateinit var iv_save_name : ImageView
    lateinit var iv_save_nickname : ImageView
    lateinit var iv_edit_nickname : ImageView
    lateinit var iv_edit_name : ImageView
    lateinit var iv_edit : ImageView // edit avatar icon


    lateinit var iv_star_1 : ImageView
    lateinit var iv_star_2 : ImageView
    lateinit var iv_star_3 : ImageView
    lateinit var iv_star_4 : ImageView
    lateinit var iv_star_5 : ImageView

    lateinit var iv_star_11 : ImageView
    lateinit var iv_star_22 : ImageView
    lateinit var iv_star_33 : ImageView
    lateinit var iv_star_44 : ImageView
    lateinit var iv_star_55 : ImageView

    lateinit var userStarsList : MutableList<ImageView>
    lateinit var authorStarsList : MutableList<ImageView>



    fun initViews(view: View) {
        iv_star_1 = view.findViewById(R.id.iv_star_1)
        iv_star_2 = view.findViewById(R.id.iv_star_2)
        iv_star_3 = view.findViewById(R.id.iv_star_3)
        iv_star_4 = view.findViewById(R.id.iv_star_4)
        iv_star_5 = view.findViewById(R.id.iv_star_5)

        iv_star_11 = view.findViewById(R.id.iv_star_11)
        iv_star_22 = view.findViewById(R.id.iv_star_22)
        iv_star_33 = view.findViewById(R.id.iv_star_33)
        iv_star_44 = view.findViewById(R.id.iv_star_44)
        iv_star_55 = view.findViewById(R.id.iv_star_55)

        userStarsList.add(iv_star_1)
        userStarsList.add(iv_star_2)
        userStarsList.add(iv_star_3)
        userStarsList.add(iv_star_4)
        userStarsList.add(iv_star_5)

        authorStarsList.add(iv_star_11)
        authorStarsList.add(iv_star_22)
        authorStarsList.add(iv_star_33)
        authorStarsList.add(iv_star_44)
        authorStarsList.add(iv_star_55)

        rv_solved = view.findViewById(R.id.rv_solved)
        rv_created = view.findViewById(R.id.rv_created)
        rv_achievements = view.findViewById(R.id.rv_achievements)

        tv_name = view.findViewById(R.id.tv_name)
        tv_nickname = view.findViewById(R.id.tv_nickname)
        tv_change_password = view.findViewById(R.id.tv_change_password)
        tv_email = view.findViewById(R.id.tv_email)
        tv_city = view.findViewById(R.id.tv_city)
        tv_level = view.findViewById(R.id.tv_level)

        cv_achievements = view.findViewById(R.id.cv_achievements)

        et_lastname = view.findViewById(R.id.et_lastname)
        et_firstname = view.findViewById(R.id.et_firstname)

        iv_avatar = view.findViewById(R.id.iv_avatar)
        iv_edit = view.findViewById(R.id.iv_edit)
        iv_edit_name = view.findViewById(R.id.iv_edit_name)
        iv_edit_nickname = view.findViewById(R.id.iv_edit_nickname)
        iv_save_nickname = view.findViewById(R.id.iv_save_nickname)
        iv_save_name = view.findViewById(R.id.iv_save_name)

        ll_edit_name = view.findViewById(R.id.ll_edit_name)
        ll_view_name = view.findViewById(R.id.ll_view_name)
        ll_view_nickname = view.findViewById(R.id.ll_view_nickname)
        ll_edit_nickname = view.findViewById(R.id.ll_edit_nickname)
    }

    private fun initAchievements() {
        if(profile.achievements.size > 0) {
            val adapter = AchievementsAdapter(profile.achievements, mContext)
            val lManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
            rv_achievements.adapter = adapter
            rv_achievements.layoutManager = lManager
        }   else {
            cv_achievements.visibility = View.GONE
        }
    }

    fun initHandlers() {

        iv_edit_name.setOnClickListener(View.OnClickListener {
            ll_view_name.visibility = View.GONE
            ll_edit_name.visibility = View.VISIBLE
        })

        iv_save_name.setOnClickListener(View.OnClickListener {
            ll_view_name.visibility = View.VISIBLE
            ll_edit_name.visibility = View.GONE
        })

        iv_edit_nickname.setOnClickListener(View.OnClickListener {
            ll_edit_nickname.visibility = View.VISIBLE
            ll_view_nickname.visibility = View.GONE
        })

        iv_save_nickname.setOnClickListener(View.OnClickListener {

            val firstname = et_firstname.text.toString()
            val lastname = et_lastname.text.toString()

            if(firstname.isEmpty() || lastname.isEmpty()) {
                Toast.makeText(mContext, "Fill all fields!", LENGTH_LONG).show()
            }   else {

                val data = JSONObject()
                data.put("firstName", firstname)
                data.put("lastName", lastname)

                editProfile(data)

                ll_view_nickname.visibility = View.VISIBLE
                ll_edit_nickname.visibility = View.GONE
            }


        })
    }

    fun initProfile() {
        tv_nickname.text = profile.nickName
        tv_name.text = "${profile.firstName} ${profile.lastName}"
        tv_email.text = profile.email

        Picasso.get().load(profile.avatar).into(iv_avatar)

        var userRating = profile.userRating.toDouble().roundToInt()
        if(userRating > 5) userRating = 5

        var authorRating = profile.authorRating.toDouble().roundToInt()
        if(authorRating > 5) authorRating = 5

        for(x in 0 until userRating) {
            userStarsList.get(x).setImageDrawable(mContext.getDrawable(R.drawable.star_filled))
        }

        for(x in 0 until authorRating) {
            authorStarsList.get(x).setImageDrawable(mContext.getDrawable(R.drawable.star_filled))
        }

        tv_level.text = CacheHelper(mContext).getLevel()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        userStarsList = mutableListOf()
        authorStarsList = mutableListOf()

        geocoder = Geocoder(mContext)
        initLocationListener()
        initViews(view)
        getProfile()
        initHandlers()

        return view
    }

    fun editProfile(data: JSONObject) {
        val queue = Volley.newRequestQueue(context)
        val request = object: StringRequest(
            Request.Method.PATCH,
            "http://wsk2019.mad.hakta.pro/api/user/profile",
            Response.Listener { response ->
                Log.d("DEBUG", "r: $response")
            },
            Response.ErrorListener { error ->
                Log.d("DEBUG", "r: ${error.message}")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Token"] = CacheHelper(context!!).getToken()
                return headers
            }

            override fun getBody(): ByteArray {
                return data.toString().toByteArray()
            }
        }

        queue.add(request)
    }

    fun initAdapters() {

        val solvedQuests = profile.completedQuests
        val solvedAdapter = BestQuestsAdapter(solvedQuests, mContext)
        val solvedLayoutManager = LinearLayoutManager(mContext)

        rv_solved.adapter = solvedAdapter
        rv_solved.layoutManager = solvedLayoutManager

        val createdQuests = profile.completedQuests
        val createdAdapter = QuestsRatingAdapter(createdQuests, mContext)

        val createdLayoutManager = LinearLayoutManager(mContext)
        rv_created.adapter = createdAdapter
        rv_created.layoutManager = createdLayoutManager


    }

    fun getProfile() {
        val queue = Volley.newRequestQueue(context)
        val request = object: StringRequest(
            Request.Method.GET,
            "http://wsk2019.mad.hakta.pro/api/user/profile",
            Response.Listener { response ->

                val json = JSONObject(response).getJSONObject("content").toString()
                val profile: Profile = Gson().fromJson(json, Profile::class.java)
                this.profile = profile
                initAdapters()
                initProfile()
                initAchievements()
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

    fun initLocationListener() {
        val locationManager: LocationManager =
            mContext.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1
            )
        } else
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, this)
    }

    override fun onProviderDisabled(provider: String) {
        Log.d("DEBUG", "provider disabled")
    }

    override fun onProviderEnabled(provider: String) {
        Log.d("DEBUG", "provider enabled")
    }

    override fun onLocationChanged(location: Location) {
        val matches : MutableList<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        val address = matches.get(0)
        currentCity = address.subAdminArea
        tv_city.text = currentCity
    }

}