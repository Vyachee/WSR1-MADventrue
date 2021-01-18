package com.grinvald.grinvaldmadventure

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
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
import com.grinvald.grinvaldmadventure.Adapters.BestQuestsAdapter
import com.grinvald.grinvaldmadventure.Adapters.TasksAdapter
import com.grinvald.grinvaldmadventure.common.CacheHelper
import com.grinvald.grinvaldmadventure.common.InternetHelper
import com.grinvald.grinvaldmadventure.models.CurrentTask
import com.grinvald.grinvaldmadventure.models.Profile
import com.grinvald.grinvaldmadventure.models.QuestItem
import com.grinvald.grinvaldmadventure.models.Task
import com.squareup.picasso.Picasso
import org.json.JSONObject

class MainScreen : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    lateinit var iv_menu : ImageView

    lateinit var ll_menu : LinearLayout
    lateinit var ll_slide_menu : LinearLayout
    lateinit var ll_toggler : LinearLayout
    lateinit var ll_arrowContainer : LinearLayout
    lateinit var ll_logout : LinearLayout

    lateinit var tv_minimize : TextView
    lateinit var tv_random_quest : TextView

    lateinit var iv_minimize : ImageView
    lateinit var iv_maximze : ImageView

    lateinit var tv_quest_title : TextView
    lateinit var tv_quest_description : TextView
    lateinit var iv_quest_preview : ImageView
    lateinit var tv_details : TextView

    lateinit var rv_quests : RecyclerView
    lateinit var rv_tasks : RecyclerView

    lateinit var mapView : MapView

    lateinit var cl_container : ConstraintLayout

    var isToggled = false
    var isSlided = false
    var firstX = 0.00
    var currentX = 0.00

    var columnCount = 2 // default value for landscape

    lateinit var map : GoogleMap

    private lateinit var profile : Profile

    private fun initViews() {

        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ll_slide_menu = findViewById(R.id.ll_slide_menu)
            ll_toggler = findViewById(R.id.ll_toggler)

            tv_minimize = findViewById(R.id.tv_minimize)
            iv_minimize = findViewById(R.id.iv_minimize)
            iv_maximze = findViewById(R.id.iv_maximze)
            ll_arrowContainer = findViewById(R.id.ll_arrowContainer)

        }   else {
            iv_menu = findViewById(R.id.iv_menu)
            ll_menu = findViewById(R.id.ll_menu)
        }

        ll_logout = findViewById(R.id.ll_logout)
        cl_container = findViewById(R.id.cl_container)
        tv_random_quest = findViewById(R.id.tv_random_quest)

        tv_quest_title = findViewById(R.id.tv_quest_title)
        tv_quest_description = findViewById(R.id.tv_quest_description)
        iv_quest_preview = findViewById(R.id.iv_quest_preview)
        tv_details = findViewById(R.id.tv_details)

        mapView = findViewById(R.id.mapView2)
        rv_quests = findViewById(R.id.rv_quests)
        rv_tasks = findViewById(R.id.rv_tasks)

    }

    fun show() {
        val params = ConstraintLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        ll_slide_menu.layoutParams = params
        iv_maximze.visibility = View.GONE
        ll_arrowContainer.visibility = View.GONE
        iv_minimize.visibility = View.VISIBLE
        tv_minimize.visibility = View.VISIBLE
    }

    fun hide() {
        val params = ConstraintLayout.LayoutParams(convertPixels(48f), MATCH_PARENT)
        ll_slide_menu.layoutParams = params
        ll_arrowContainer.visibility = View.VISIBLE
        iv_maximze.visibility = View.VISIBLE
        iv_minimize.visibility = View.GONE
        tv_minimize.visibility = View.GONE
    }

    override fun onMapReady(p0: GoogleMap?) {
        map = p0!!

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
            Toast.makeText(baseContext, "This permission is required", LENGTH_SHORT).show()
        }
    }

    fun getProfile() {
        val queue = Volley.newRequestQueue(this)
        val request = object: StringRequest(
            Request.Method.GET,
            "http://wsk2019.mad.hakta.pro/api/user/profile",
            Response.Listener { response ->
                val json = JSONObject(response).getJSONObject("content").toString()

                val profile: Profile = Gson().fromJson(json, Profile::class.java)

                this.profile = profile

                setTasks()
                setQuest()

            },
            Response.ErrorListener { error ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Token"] = CacheHelper(baseContext).getToken()
                return headers
            }
        }

        queue.add(request)

    }

    fun setTasks() {
        val tasks : MutableList<Task> = profile.createdQuests.get(0).tasks
        val adapter : TasksAdapter = TasksAdapter(tasks, baseContext)
        val layoutManager : LinearLayoutManager = LinearLayoutManager(baseContext)
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
        val queue = Volley.newRequestQueue(this)
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
                headers["Token"] = CacheHelper(baseContext).getToken()
                return headers
            }
        }

        queue.add(request)
    }



    override fun onLocationChanged(location: Location) {
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude),
                17f
            )
        )
    }

    override fun onProviderDisabled(provider: String) {
        Log.d("DEBUG", "disabled")
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        initViews()

        if(InternetHelper(baseContext).checkConnection() == false) {
            val layoutInflater = LayoutInflater.from(this)
            val view : View = layoutInflater.inflate(R.layout.dialog_error, null, false)

            view.id = View.generateViewId()

            val set = ConstraintSet()

            set.connect(view.id, ConstraintSet.LEFT, R.id.cl_container, ConstraintSet.LEFT)
            set.connect(view.id, ConstraintSet.TOP, R.id.cl_container, ConstraintSet.TOP)
            set.connect(view.id, ConstraintSet.BOTTOM, R.id.cl_container, ConstraintSet.BOTTOM)
            set.connect(view.id, ConstraintSet.RIGHT, R.id.cl_container, ConstraintSet.RIGHT)

            val layoutParams = ConstraintLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
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

        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            // landscape orientation
            ll_toggler.setOnClickListener(View.OnClickListener {

                if (!isSlided) {
                    show()
                } else {
                    hide()
                }
                isSlided = !isSlided
            })

            ll_slide_menu.setOnTouchListener(View.OnTouchListener { view: View, motionEvent: MotionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        firstX = motionEvent.x.toDouble()
                    }

                    MotionEvent.ACTION_UP -> {
                        if (currentX > firstX) {
                            // swiped right
                            if (currentX - firstX > 30)
                                show()

                        } else {
                            // swiped left
                            if (firstX - currentX > 30)
                                hide()
                        }

                        Log.d("DEBUG", "currentX: $currentX firstX: $firstX")
                    }

                    MotionEvent.ACTION_MOVE -> {
                        currentX = motionEvent.x.toDouble()
                    }
                }

                true
            })

        }
        else {

            // portrait orientation
            columnCount = 1

            iv_menu.setOnClickListener(View.OnClickListener {
                if (!isToggled) {
                    ll_menu.visibility = View.VISIBLE
                    ll_menu.animate().alpha(1f).setDuration(200)
                } else {
                    ll_menu.animate().alpha(0f).setDuration(200)
                    Handler().postDelayed(Runnable {
                        ll_menu.visibility = View.INVISIBLE
                    }, 200)
                }

                isToggled = !isToggled
            })
        }

        tv_random_quest.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, QuestDetails::class.java)
            startActivity(intent)
        })

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(OnMapReadyCallback {

            map = it
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(55.151060, 61.377293), 17f)
            )

            val locationManager: LocationManager =
                getSystemService(LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
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

        ll_logout.setOnClickListener(View.OnClickListener {
            val queue = Volley.newRequestQueue(baseContext)
            val request = object : StringRequest(
                Request.Method.PUT,
                "http://wsk2019.mad.hakta.pro/api/user/logout",
                Response.Listener { response ->
                    CacheHelper(baseContext).removeAuthData()
                    Toast.makeText(baseContext, "Logout successful", LENGTH_LONG).show()
                    val intent = Intent(baseContext, SignIn::class.java)
                    startActivity(intent)
                    finish()
                },
                Response.ErrorListener { error ->

                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params.put("Token", CacheHelper(baseContext).getToken())
                    return params
                }
            }

            queue.add(request)
        })


        getProfile()
        loadQuests()

    }



    private fun loadQuests() {
        val queue = Volley.newRequestQueue(this)
        val request = object: StringRequest(
            Request.Method.GET,
            "http://wsk2019.mad.hakta.pro/api/quests/popular",
            Response.Listener { response ->

                Log.d("DEBUG", "response: $response")


                val json = JSONObject(response).getJSONArray("content")

                val list: MutableList<QuestItem> = mutableListOf()

                for (i in 0..json.length() - 1) {
                    val item = json.getJSONObject(i)
//                Log.d("DEBUG", "item: $item")
//                 TODO: 16.01.2021
                    val questItem = Gson().fromJson(item.toString(), QuestItem::class.java)
                    list.add(questItem)
                }

                val adapter = BestQuestsAdapter(list, baseContext)
                val layoutManager = GridLayoutManager(baseContext, columnCount)

                rv_quests.adapter = adapter
                rv_quests.layoutManager = layoutManager

            },
            Response.ErrorListener { error ->

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Token"] = CacheHelper(baseContext).getToken()
                return headers
            }
        }

        Log.d("DEBUG", "token: " + CacheHelper(baseContext).getToken())

        queue.add(request)
    }

    private fun convertPixels(dps: Float) : Int {
        val scale = baseContext.resources.displayMetrics.density
        return (dps * scale + 0.5f).toInt()
    }
}