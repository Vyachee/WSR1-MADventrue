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
import androidx.fragment.app.FragmentTransaction
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
import com.grinvald.grinvaldmadventure.models.CurrentTask
import com.grinvald.grinvaldmadventure.models.Profile
import com.grinvald.grinvaldmadventure.models.QuestItem
import com.grinvald.grinvaldmadventure.models.Task
import com.squareup.picasso.Picasso
import org.json.JSONObject

class MainScreen : AppCompatActivity() {

    lateinit var iv_menu : ImageView

    lateinit var ll_menu : LinearLayout
    lateinit var ll_slide_menu : LinearLayout
    lateinit var ll_toggler : LinearLayout
    lateinit var ll_arrowContainer : LinearLayout
    lateinit var ll_compass : LinearLayout
    lateinit var ll_logout : LinearLayout
    lateinit var tv_minimize : TextView

    lateinit var iv_minimize : ImageView
    lateinit var iv_maximze : ImageView


    var isToggled = false
    var isSlided = false
    var firstX = 0.00
    var currentX = 0.00

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
        ll_compass = findViewById(R.id.ll_compass)
        ll_logout = findViewById(R.id.ll_logout)

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

    fun toggleMenu() {
        if (!isSlided) {
            show()
        } else {
            hide()
        }
        isSlided = !isSlided
    }

    fun toggleMenuPortrait() {
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
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        initViews()

        ll_compass.setOnClickListener(View.OnClickListener {
            val manager = supportFragmentManager
            val transaction = manager.beginTransaction()
            toggleMenuPortrait()
            transaction.replace(R.id.fragment, Compass())
            transaction.commit()
        })

        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            // landscape orientation
            ll_toggler.setOnClickListener(View.OnClickListener {
                toggleMenu()
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
            iv_menu.setOnClickListener(View.OnClickListener {
                toggleMenuPortrait()
            })
        }

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

    }


    private fun convertPixels(dps: Float) : Int {
        val scale = baseContext.resources.displayMetrics.density
        return (dps * scale + 0.5f).toInt()
    }
}