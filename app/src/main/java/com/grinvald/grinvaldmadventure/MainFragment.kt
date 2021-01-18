package com.grinvald.grinvaldmadventure

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.grinvald.grinvaldmadventure.Adapters.AchievementsAdapter
import com.grinvald.grinvaldmadventure.Adapters.BestQuestsAdapter
import com.grinvald.grinvaldmadventure.Adapters.TasksAdapter
import com.grinvald.grinvaldmadventure.common.CacheHelper
import com.grinvald.grinvaldmadventure.common.InternetHelper
import com.grinvald.grinvaldmadventure.common.XMLParser
import com.grinvald.grinvaldmadventure.models.CurrentTask
import com.grinvald.grinvaldmadventure.models.Profile
import com.grinvald.grinvaldmadventure.models.QuestItem
import com.grinvald.grinvaldmadventure.models.Task
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.ln
import kotlin.math.round

class MainFragment : Fragment(), OnMapReadyCallback, LocationListener {


    lateinit var cl_container : ConstraintLayout
    lateinit var tv_quest_title : TextView
    lateinit var tv_quest_description : TextView
    lateinit var tv_name : TextView
    lateinit var iv_quest_preview : ImageView
    lateinit var iv_avatar : ImageView
    lateinit var tv_details : TextView
    lateinit var tv_random_quest : TextView
    lateinit var tv_level_circle : TextView
    lateinit var tv_level : TextView
    lateinit var rv_quests : RecyclerView
    lateinit var rv_tasks : RecyclerView
    lateinit var rv_achievements : RecyclerView
    lateinit var mapView : MapView


    lateinit var tv_pressure : TextView
    lateinit var tv_wind : TextView
    lateinit var tv_humidity : TextView
    lateinit var tv_date : TextView
    lateinit var tv_city : TextView
    lateinit var tv_temp : TextView

    lateinit var map : GoogleMap
    private lateinit var profile : Profile
    var columnCount = 2
    var isWeatherReceived = false

    private fun initViews(view: View) {
        tv_random_quest = view.findViewById(R.id.tv_random_quest)
        cl_container = view.findViewById(R.id.cl_container)

        tv_pressure = view.findViewById(R.id.tv_pressure)
        tv_wind  = view.findViewById(R.id.tv_wind)
        tv_humidity = view.findViewById(R.id.tv_humidity)
        tv_date  = view.findViewById(R.id.tv_date)
        tv_city  = view.findViewById(R.id.tv_city)
        tv_temp  = view.findViewById(R.id.tv_temp)

        tv_quest_title = view.findViewById(R.id.tv_quest_title)
        tv_quest_description = view.findViewById(R.id.tv_quest_description)
        tv_name = view.findViewById(R.id.tv_name)

        iv_avatar = view.findViewById(R.id.iv_avatar)
        tv_level_circle = view.findViewById(R.id.tv_level_circle)
        tv_level = view.findViewById(R.id.tv_level)

        iv_quest_preview = view.findViewById(R.id.iv_quest_preview)
        tv_details = view.findViewById(R.id.tv_details)

        mapView = view.findViewById(R.id.mapView2)
        rv_quests = view.findViewById(R.id.rv_quests)
        rv_tasks = view.findViewById(R.id.rv_tasks)
        rv_achievements = view.findViewById(R.id.rv_achievements)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty()
            && grantResults.get(0) == PackageManager.PERMISSION_GRANTED) {

        }   else {
            Toast.makeText(context, "This permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    fun initProfile() {
        if(profile.firstName.isEmpty()
            || profile.lastName.isEmpty()
            || profile.firstName.equals("null")
            || profile.lastName.equals("null")) {
            tv_name.text = profile.nickName
        }   else tv_name.text = "${profile.firstName} ${profile.lastName}"

        Picasso.get().load(profile.avatar).into(iv_avatar)

        calcLevel()
    }

    @SuppressLint("SetTextI18n")
    fun calcLevel() {

        val level = round(ln((calcPoints() / 5) + 1) + 1).toInt()
        tv_level_circle.text = level.toString()
        tv_level.text = "Level $level"

    }

    fun calcPoints() : Double {

        var points = 0.00

        for(x in 0 until profile.completedQuests.size) {
            val quest = profile.completedQuests.get(x)
            points += calcQuestPoint(x, quest)
        }

        return points
    }

    fun calcQuestPoint(i: Int, quest: QuestItem) : Double {

        var questPoint = quest.difficulty.toDouble()

        var sum = 0.00
        for(x in quest.tasks) {
            if(x.status.equals("COMPLETED")) {

                val format = SimpleDateFormat("yyyy-MM-dd")
                val date1: Date = format.parse(x.startDate)
                val date2: Date = format.parse(x.endDate)

                val diff: Long = date2.time - date1.time
                val seconds = diff / 1000

                val minutes = seconds / 60
                val needleMinutes = x.taskCompletionTime.toDouble()

                val result = needleMinutes / minutes
                sum += result
            }
        }

        questPoint *= sum

        return questPoint
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


                initProfile()
                setTasks()
                setQuest()
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

    fun setTasks() {
        val tasks : MutableList<Task> = profile.createdQuests.get(0).tasks
        val adapter : TasksAdapter = TasksAdapter(tasks, requireContext())
        val layoutManager : LinearLayoutManager = LinearLayoutManager(context)
        rv_tasks.adapter = adapter
        rv_tasks.layoutManager = layoutManager
    }

    fun setQuest() {
        val quest = profile.createdQuests.get(0)
        tv_quest_title.text = quest.name
        tv_quest_description.text = quest.description
        Picasso.get().load(quest.mainPhoto).into(iv_quest_preview)
    }

    fun getCurrentTask() {
        val queue = Volley.newRequestQueue(requireContext())
        val request = object: StringRequest(
            Request.Method.GET,
            "http://wsk2019.mad.hakta.pro/api/user/currentTask",
            Response.Listener { response ->
                val json = JSONObject(response).getJSONObject("content").toString()

                val currentTask: CurrentTask = Gson().fromJson(json, CurrentTask::class.java)

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

    fun getWeather(lat: Double, lon: Double) {

        Log.d("DEBUG", "lat $lat lon $lon")

        val queue = Volley.newRequestQueue(requireContext())
        val request = object: StringRequest(
            Request.Method.GET,
            "http://wsk2019.mad.hakta.pro/api/weather?lat=$lat&lon=$lon",
            Response.Listener { response ->

                val w = XMLParser().parseWeather(response)
                tv_pressure.text = w!!.pressure
                tv_wind.text = w!!.windSpeed + " m/s"
                tv_humidity.text = w!!.humidity
                tv_temp.text = w!!.temperature

                val date = Date()
                val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
                tv_date.text = simpleDateFormat.format(date)

            },
            Response.ErrorListener { error ->
// 67536c4b-bfd9-cbaa-c46f-67e71577089d
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Token"] = CacheHelper(context!!).getToken()
                return headers
            }
        }

        queue.add(request)

        isWeatherReceived = true
    }

    override fun onLocationChanged(location: Location) {
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude),
                17f
            )
        )

        if(!isWeatherReceived)
            getWeather(location.latitude, location.longitude)
    }

    override fun onProviderDisabled(provider: String) {
        Log.d("DEBUG", "disabled")
    }


    override fun onMapReady(p0: GoogleMap?) {
        map = p0!!

    }

    private fun loadQuests() {
        val queue = Volley.newRequestQueue(requireContext())
        val request = object: StringRequest(
            Request.Method.GET,
            "http://wsk2019.mad.hakta.pro/api/quests/popular",
            Response.Listener { response ->

                val json = JSONObject(response).getJSONArray("content")

                val list: MutableList<QuestItem> = mutableListOf()

                for (i in 0..json.length() - 1) {
                    val item = json.getJSONObject(i)
                    val questItem = Gson().fromJson(item.toString(), QuestItem::class.java)
                    list.add(questItem)
                }

                val adapter = BestQuestsAdapter(list, requireContext())
                val layoutManager = GridLayoutManager(requireContext(), columnCount)

                rv_quests.adapter = adapter
                rv_quests.layoutManager = layoutManager

            },
            Response.ErrorListener { error ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Token"] = CacheHelper(context!!).getToken()
                return headers
            }
        }

        Log.d("DEBUG", "token: " + CacheHelper(requireContext()).getToken())

        queue.add(request)
    }

    private fun initAchievements() {
        val adapter = AchievementsAdapter(profile.achievements, requireContext())
        val lManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        rv_achievements.adapter = adapter
        rv_achievements.layoutManager = lManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        initViews(view)

        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        }   else {
            columnCount = 1
        }

        getProfile()
        loadQuests()

        if(InternetHelper(requireContext()).checkConnection() == false) {
            val layoutInflater = LayoutInflater.from(context)
            val view : View = layoutInflater.inflate(R.layout.dialog_error, null, false)

            view.id = View.generateViewId()

            val set = ConstraintSet()

            set.connect(view.id, ConstraintSet.LEFT, R.id.cl_container, ConstraintSet.LEFT)
            set.connect(view.id, ConstraintSet.TOP, R.id.cl_container, ConstraintSet.TOP)
            set.connect(view.id, ConstraintSet.BOTTOM, R.id.cl_container, ConstraintSet.BOTTOM)
            set.connect(view.id, ConstraintSet.RIGHT, R.id.cl_container, ConstraintSet.RIGHT)

            val layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            view.layoutParams = layoutParams

            set.applyTo(cl_container)

            val tv_ok = view.findViewById<TextView>(R.id.tv_ok)

            val tv_description = view.findViewById<TextView>(R.id.tv_description)
            tv_description.text = "No internet connection"

            tv_ok!!.setOnClickListener(View.OnClickListener {
                cl_container.removeView(view)
            })

            cl_container.addView(view)
        }

        tv_random_quest.setOnClickListener(View.OnClickListener {
            val intent = Intent(requireContext(), QuestDetails::class.java)
            startActivity(intent)
        })

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(OnMapReadyCallback {

            map = it
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(55.151060, 61.377293), 17f)
            )

            val locationManager: LocationManager =
                requireContext().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
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

        })



        return view
    }

}