package com.grinvald.grinvaldmadventure

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.budiyev.android.codescanner.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Scope
import com.google.android.gms.common.api.Status
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataPoint
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Value
import com.google.android.gms.fitness.request.DataSourcesRequest
import com.google.android.gms.fitness.request.OnDataPointListener
import com.google.android.gms.fitness.request.SensorRequest
import com.google.android.gms.fitness.result.DataSourcesResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.grinvald.grinvaldmadventure.Adapters.ChatAdapter
import com.grinvald.grinvaldmadventure.Adapters.CommentAdapter
import com.grinvald.grinvaldmadventure.Adapters.TasksAdapter
import com.grinvald.grinvaldmadventure.common.CacheHelper
import com.grinvald.grinvaldmadventure.fragments.Photo
import com.grinvald.grinvaldmadventure.models.*
import com.grinvald.grinvaldmadventure.models.Message
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap
import kotlin.math.roundToInt

class QuestDetail : Fragment(), OnMapReadyCallback, LocationListener {

    lateinit var mContext: Context

    lateinit var tv_difficulty: TextView
    lateinit var tv_date: TextView
    lateinit var tv_author: TextView
    lateinit var tv_category: TextView
    lateinit var tv_description: TextView
    lateinit var tv_title: TextView
    lateinit var tv_status: TextView
    lateinit var tv_description_task: TextView
    lateinit var tv_task_title: TextView
    lateinit var tv_send_comment: TextView
    lateinit var et_comment_text: EditText
    lateinit var et_chat_text: EditText

    lateinit var codeScanner: CodeScanner
    lateinit var codeScannerView: CodeScannerView

    lateinit var vp_images_quest: ViewPager2
    lateinit var vp_images_task: ViewPager2
    lateinit var ll_dots: LinearLayout
    lateinit var ll_tags: LinearLayout
    lateinit var ll_dots_task: LinearLayout
    lateinit var ll_media_container: LinearLayout
    lateinit var cv_photos_task_container: CardView
    lateinit var ll_audio_control: LinearLayout
    lateinit var ll_task_info: LinearLayout
    lateinit var ll_add_comment: LinearLayout
    lateinit var ll_tasks: LinearLayout
    lateinit var ll_chat_toggle: LinearLayout
    lateinit var ll_chat: LinearLayout

    lateinit var rv_tasks: RecyclerView
    lateinit var rv_comments: RecyclerView
    lateinit var rv_chat: RecyclerView

    lateinit var iv_star_1: ImageView
    lateinit var iv_star_2: ImageView
    lateinit var iv_star_3: ImageView
    lateinit var iv_star_4: ImageView
    lateinit var iv_star_5: ImageView

    lateinit var iv_star_11: ImageView
    lateinit var iv_star_22: ImageView
    lateinit var iv_star_33: ImageView
    lateinit var iv_star_44: ImageView
    lateinit var iv_star_55: ImageView


    lateinit var iv_goal_type: ImageView
    lateinit var iv_send_message: ImageView
    lateinit var tv_goal_value: TextView
    lateinit var tv_steps_counter: TextView

    lateinit var iv_time_icon: ImageView
    lateinit var tv_time_value: TextView

    lateinit var iv_date: ImageView
    lateinit var tv_date_value: TextView

    lateinit var mediaPlayer: MediaPlayer

    lateinit var mv_map: MapView

    lateinit var photos: MutableList<String>
    lateinit var photosTask: MutableList<String>
    lateinit var dotsList: MutableList<LinearLayout>
    lateinit var dotsTaskList: MutableList<LinearLayout>
    lateinit var stars: MutableList<ImageView>
    lateinit var commentStars: MutableList<ImageView>
    lateinit var quest: QuestItem
    lateinit var detailedTask: TaskDetails

    lateinit var tv_progress: TextView
    lateinit var iv_audio_toggle: ImageView
    lateinit var sb_progress: SeekBar
    lateinit var fragment: Fragment
    lateinit var cl_container: ConstraintLayout
    lateinit var tv_action_button: TextView
    lateinit var tv_date_constraint: TextView

    lateinit var currentTask: Task
    lateinit var map: GoogleMap

    var isAudioToggled = false
    var isInterrupted = false
    var isChatToggled = false
    var commentRating = 0

    lateinit var mClient: GoogleApiClient
    lateinit var TAG: String
    var listener: OnDataPointListener? = null

    private fun createClient() {
        mClient = GoogleApiClient.Builder(mContext)
                .addApi(Fitness.SENSORS_API)
                .addScope(Scope(Scopes.FITNESS_LOCATION_READ)) // GET STEP VALUES
                .addConnectionCallbacks(object : ConnectionCallbacks {
                    override fun onConnected(bundle: Bundle?) {
                        findFitnessDataSources()
                    }

                    override fun onConnectionSuspended(i: Int) {
                    }
                }
                )
                .enableAutoManage(mContext as FragmentActivity, 0, object : OnConnectionFailedListener {
                    override fun onConnectionFailed(result: ConnectionResult) {
                    }
                })
                .build()
    }

    override fun onPause() {
        super.onPause()
        mClient.stopAutoManage(requireActivity())
        mClient.disconnect()
    }

    private fun connectFitness() {
        if(this::mClient.isInitialized) {
            if(!mClient.isConnected) {
                createClient()
            }
        }   else
            createClient()

    }

    private fun findFitnessDataSources() {
        Fitness.SensorsApi.findDataSources(
                mClient,
                DataSourcesRequest.Builder()
                        .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                        .setDataSourceTypes(DataSource.TYPE_DERIVED)
                        .build())
                .setResultCallback(object : ResultCallback<DataSourcesResult?> {
                    override fun onResult(dataSourcesResult: DataSourcesResult) {
                        for (dataSource in dataSourcesResult.getDataSources()) {
                            if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA) && listener == null) {
                                registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_DELTA)
                            }
                        }
                    }
                })
    }

    private fun registerFitnessDataListener(dataSource: DataSource, dataType: DataType) {
        listener = object : OnDataPointListener {
            override fun onDataPoint(dataPoint: DataPoint) {
                for (field in dataPoint.getDataType().getFields()) {
                    val steps: Value = dataPoint.getValue(field)

                    CacheHelper(mContext).writeSteps(steps.asInt(), Date())
                }
            }
        }
        Fitness.SensorsApi.add(
                mClient,
                SensorRequest.Builder()
                        .setDataSource(dataSource)
                        .setDataType(dataType)
                        .setSamplingRate(1, TimeUnit.SECONDS)
                        .build(),
                listener).setResultCallback(object : ResultCallback<Status?> {
            override fun onResult(status: Status) {

            }
        })
    }

    fun initViews(view: View) {
        codeScannerView = view.findViewById(R.id.scanner)

        ll_chat_toggle = view.findViewById(R.id.ll_chat_toggle)
        et_chat_text = view.findViewById(R.id.et_chat_text)
        ll_chat = view.findViewById(R.id.ll_chat)
        iv_send_message = view.findViewById(R.id.iv_send_message)

        tv_difficulty = view.findViewById(R.id.tv_difficulty)
        et_comment_text = view.findViewById(R.id.et_comment_text)
        tv_send_comment = view.findViewById(R.id.tv_send_comment)
        rv_chat = view.findViewById(R.id.rv_chat)

        ll_task_info = view.findViewById(R.id.ll_task_info)
        cl_container = view.findViewById(R.id.cl_container)
        tv_status = view.findViewById(R.id.tv_status)
        tv_action_button = view.findViewById(R.id.tv_action_button)
        tv_date_constraint = view.findViewById(R.id.tv_date_constraint)

        iv_goal_type = view.findViewById(R.id.iv_goal_type)
        tv_goal_value = view.findViewById(R.id.tv_goal_value)
        iv_time_icon = view.findViewById(R.id.iv_time_icon)
        tv_time_value = view.findViewById(R.id.tv_time_value)
        tv_date_value = view.findViewById(R.id.tv_date_value)
        tv_steps_counter = view.findViewById(R.id.tv_steps_counter)
        tv_date = view.findViewById(R.id.tv_date)
        iv_date = view.findViewById(R.id.iv_date)

        tv_progress = view.findViewById(R.id.tv_progress)
        rv_comments = view.findViewById(R.id.rv_comments)

        iv_audio_toggle = view.findViewById(R.id.iv_audio_toggle)
        tv_task_title = view.findViewById(R.id.tv_task_title)
        sb_progress = view.findViewById(R.id.sb_progress)

        ll_audio_control = view.findViewById(R.id.ll_audio_control)
        tv_description_task = view.findViewById(R.id.tv_description_task)
        tv_title = view.findViewById(R.id.tv_title)
        tv_author = view.findViewById(R.id.tv_author)
        tv_category = view.findViewById(R.id.tv_category)
        tv_description = view.findViewById(R.id.tv_description)
        vp_images_quest = view.findViewById(R.id.vp_images_quest)
        vp_images_task = view.findViewById(R.id.vp_images_task)
        ll_dots = view.findViewById(R.id.ll_dots)
        ll_dots_task = view.findViewById(R.id.ll_dots_task)
        ll_tags = view.findViewById(R.id.ll_tags)
        rv_tasks = view.findViewById(R.id.rv_tasks)
        ll_media_container = view.findViewById(R.id.ll_media_container)
        cv_photos_task_container = view.findViewById(R.id.cv_photos_task_container)
        ll_tasks = view.findViewById(R.id.ll_tasks)
        ll_add_comment = view.findViewById(R.id.ll_add_comment)

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

        mv_map = view.findViewById(R.id.mv_map)

        stars = mutableListOf()
        commentStars = mutableListOf()
        stars.add(iv_star_1)
        stars.add(iv_star_2)
        stars.add(iv_star_3)
        stars.add(iv_star_4)
        stars.add(iv_star_5)

        commentStars.add(iv_star_11)
        commentStars.add(iv_star_22)
        commentStars.add(iv_star_33)
        commentStars.add(iv_star_44)
        commentStars.add(iv_star_55)

    }

    fun initComments() {
        val queue = Volley.newRequestQueue(mContext)

        val mStringRequest = object : StringRequest(
                Request.Method.GET,
                "http://wsk2019.mad.hakta.pro/api/quests/${quest.id}/comment",
                Response.Listener { response ->

                    val o = JSONObject(response).getJSONArray("content")

                    val list: MutableList<Comment> = mutableListOf()

                    for (x in 0 until o.length()) {
                        val jsonComment = o.getJSONObject(x)
                        val comment: Comment = Gson().fromJson(jsonComment.toString(), Comment::class.java)
                        list.add(comment)
                    }

                    val adapter = CommentAdapter(list, mContext)
                    val layoutManager = LinearLayoutManager(mContext)
                    rv_comments.adapter = adapter
                    rv_comments.layoutManager = layoutManager


                },
                Response.ErrorListener { error ->
                    Log.d("DEBUG", "error: ${error.message}")
                }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val m = HashMap<String, String>()
                m.put("Token", CacheHelper(mContext).getToken())
                return m
            }
        }
        queue.add(mStringRequest)
    }

    fun initScanner() {
        codeScannerView.visibility = View.VISIBLE
        codeScanner = CodeScanner(mContext, codeScannerView)

        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread(Runnable {
                finishQrTask(it.text)
                Toast.makeText(mContext, "${it.text}", LENGTH_LONG).show()
                codeScannerView.visibility = View.GONE
            })
        }
        codeScanner.errorCallback = ErrorCallback {
            requireActivity().runOnUiThread(Runnable {
                Toast.makeText(mContext, "Camera initialization error: ${it.message}",
                        LENGTH_LONG).show()
            })
        }

        codeScannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    fun initDots(ll: LinearLayout, dots: MutableList<LinearLayout>, photosList: MutableList<String>) {
        for (i in 0 until photosList.size) {
            val layoutInflater = LayoutInflater.from(requireContext())
            val dot: LinearLayout = layoutInflater.inflate(R.layout.dot, ll, false) as LinearLayout

            val params: ViewGroup.MarginLayoutParams = dot.layoutParams as ViewGroup.MarginLayoutParams
            params.leftMargin = 16;
            dot.layoutParams = params

            if (i != 1)
                dot.background = requireContext().getDrawable(R.drawable.item_inactive)

            dots.add(dot)
            ll.addView(dot)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    fun killMediaPlayer() {
        try {
            mediaPlayer.release()
        } catch (e: Exception) {
        }
    }

    fun initAudio(url: String) {
        killMediaPlayer()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepare()
        mediaPlayer.setOnPreparedListener(MediaPlayer.OnPreparedListener {


            val duration = mediaPlayer.duration
            val formattedDuration = DateUtils.formatElapsedTime((duration / 1000).toLong())
            tv_progress.text = "0:00/${formattedDuration}"

            sb_progress.max = duration
            mediaPlayer.start()
        })

        Thread(Runnable {
            while (true) {
                val duration = mediaPlayer.duration
                val current = mediaPlayer.currentPosition
                val formattedDuration = DateUtils.formatElapsedTime((duration / 1000).toLong())
                val formattedCurrentPosition = DateUtils.formatElapsedTime((current / 1000).toLong())
                requireActivity().runOnUiThread(Runnable {
                    tv_progress.text = "$formattedCurrentPosition/$formattedDuration"
                    sb_progress.progress = current
                })
            }
        }).start()
    }

    override fun onDestroy() {
        super.onDestroy()
        killMediaPlayer()
    }

    fun initMedia(audioFiles: MutableList<String>, videoFiles: MutableList<String>) {
        ll_media_container.removeAllViews()
        if (audioFiles.size > 0) {

            for (x in audioFiles) {

                val inflater = LayoutInflater.from(mContext)
                val view = inflater.inflate(R.layout.audio_item, null, false)
                val tv_title = view.findViewById<TextView>(R.id.tv_title)
                tv_title.text = "Secret transmission"

                view.setOnClickListener(View.OnClickListener {
                    if (!isAudioToggled) {

                        ll_audio_control.translationY = 150f
                        ll_audio_control.visibility = View.VISIBLE
                        ll_audio_control.animate().translationY(0f).setDuration(500)

                        isAudioToggled = true

                    }

                    initAudio(x)

                })

                ll_media_container.addView(view)
            }
        }

        if (videoFiles.size > 0) {
            for (x in videoFiles) {
                val inflater = LayoutInflater.from(mContext)
                val view = inflater.inflate(R.layout.video_item, null, false)

                val tv_title = view.findViewById<TextView>(R.id.tv_title)
                tv_title.text = "Secret video"

                view.setOnClickListener(View.OnClickListener {
                    val layoutInflater = LayoutInflater.from(mContext)
                    val v = layoutInflater.inflate(R.layout.video_view, null, false)
                    val videoView = v.findViewById<VideoView>(R.id.videoView)
                    val tv_progress = v.findViewById<TextView>(R.id.tv_progress)
                    val sb_progress = v.findViewById<SeekBar>(R.id.sb_progress)
                    val iv_audio_toggle = v.findViewById<ImageView>(R.id.iv_audio_toggle)

                    videoView.setVideoPath(x)
                    videoView.start()

                    iv_audio_toggle.setOnClickListener(View.OnClickListener {
                        if (videoView.isPlaying) {
                            videoView.pause()
                            iv_audio_toggle.setImageDrawable(mContext.getDrawable(R.drawable.play))
                        } else {
                            videoView.resume()
                            iv_audio_toggle.setImageDrawable(mContext.getDrawable(R.drawable.play))
                        }
                    })

                    val t = Thread(Runnable {
                        while (!isInterrupted) {
                            val current = videoView.currentPosition
                            val duration = videoView.duration

                            val formattedDuration = DateUtils.formatElapsedTime((duration / 1000).toLong())
                            val formattedCurrent = DateUtils.formatElapsedTime((current / 1000).toLong())

                            requireActivity().runOnUiThread(Runnable {
                                tv_progress.text = "${formattedCurrent}/${formattedDuration}"
                                sb_progress.max = duration
                                sb_progress.progress = current
                            })
                        }
                    })
                    t.start()

                    v.id = View.generateViewId()

                    val set = ConstraintSet()

                    set.connect(v.id, ConstraintSet.LEFT, R.id.cl_container, ConstraintSet.LEFT)
                    set.connect(v.id, ConstraintSet.TOP, R.id.cl_container, ConstraintSet.TOP)
                    set.connect(v.id, ConstraintSet.BOTTOM, R.id.cl_container, ConstraintSet.BOTTOM)
                    set.connect(v.id, ConstraintSet.RIGHT, R.id.cl_container, ConstraintSet.RIGHT)

                    val layoutParams = ConstraintLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    v.layoutParams = layoutParams

                    val tv_close = v.findViewById<TextView>(R.id.tv_close)

                    tv_close.setOnClickListener(View.OnClickListener {
                        t.interrupt()
                        isInterrupted = true
                        cl_container.removeView(v)
                    })


                    set.applyTo(cl_container)


                    cl_container.addView(v)
                })

                ll_media_container.addView(view)
            }

        }
    }

    fun sendCommentSection() {
        for (x in 0 until commentStars.size) {
            commentStars.get(x).setOnClickListener(View.OnClickListener {
                commentRating = x + 1
                fillStars(commentRating)
            })
        }

        tv_send_comment.setOnClickListener(View.OnClickListener {
            sendComment()
        })

    }

    fun fillStars(until: Int) {
        for (x in commentStars) {
            x.setImageDrawable(mContext.getDrawable(R.drawable.star_empty))
        }
        for (x in 0 until until) {
            commentStars.get(x).setImageDrawable(mContext.getDrawable(R.drawable.star_filled))
        }
    }

    fun sendComment() {
        val queue = Volley.newRequestQueue(mContext)

        val mStringRequest = object : StringRequest(
                Request.Method.PUT,
                "http://wsk2019.mad.hakta.pro/api/quests/${quest.id}/comment",
                Response.Listener { response ->

                    if (response.equals("\"Success\"")) {
                        et_comment_text.text = null
                        fillStars(0)
                        commentRating = 0
                    }

                },
                Response.ErrorListener { error ->
                    Log.d("DEBUG", "error: ${error.message}")
                }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val m = HashMap<String, String>()
                m.put("Token", CacheHelper(mContext).getToken())
                return m
            }

            override fun getBody(): ByteArray {
                val jsonObject = JSONObject()
                val text = et_comment_text.text.toString()
                jsonObject.put("text", text)
                jsonObject.put("rating", commentRating)
                return jsonObject.toString().toByteArray()
            }
        }
        queue.add(mStringRequest)
    }

    fun initTask(detailedTask: TaskDetails) {
        tv_description_task.text = detailedTask.description
        tv_task_title.text = detailedTask.name
        ll_media_container.removeAllViews()
        this.detailedTask = detailedTask

        Log.d("DEBUG", "status: ${currentTask.status}")

        if (currentTask.status == "NOT_STARTED") {
            tv_action_button.visibility = View.VISIBLE
            tv_action_button.text = "Start"
            tv_status.text = "New"
        }

        if (currentTask.status == "IN_PROGRESS") {
            tv_status.text = "In progress"
            tv_action_button.visibility = View.VISIBLE
            tv_action_button.text = "Finish"

        }

        if (currentTask.status == "COMPLETED") {
            tv_status.text = "Completed"
            tv_action_button.visibility = View.GONE
            ll_add_comment.visibility = View.VISIBLE
        }

        val audio = detailedTask.audios
        val video = detailedTask.videos
        initMedia(audio, video)


        if (quest.startDate != null && quest.endDate != null) {

            tv_date_constraint.text = "Quest is valid from ${quest.startDate} to ${quest.endDate}"

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val startDate: Date = simpleDateFormat.parse(quest.startDate)
            val endDate: Date = simpleDateFormat.parse(quest.endDate)
            val currentDate = Date()

            // TODO: 21.01.2021 uncomment

            if (currentDate.time >= startDate.time && currentDate.time <= endDate.time) {

                tv_date_constraint.setTextColor(mContext.getColor(R.color.aqua))
                tv_action_button.visibility = View.VISIBLE
                tv_title.setTextColor(mContext.getColor(R.color.aqua))
                tv_task_title.setTextColor(mContext.getColor(R.color.aqua))

            } else {

                tv_date_constraint.setTextColor(mContext.getColor(R.color.light_red))
                tv_status.text = "Blocked"
                tv_action_button.visibility = View.GONE
                tv_title.setTextColor(mContext.getColor(R.color.gray))
                tv_task_title.setTextColor(mContext.getColor(R.color.gray))

            }


        }
        if (detailedTask.finishDateConstraint != null && detailedTask.startDateConstraint != null) {
            iv_date.visibility = View.VISIBLE
            tv_date_value.visibility = View.VISIBLE
            tv_date_value.text = "${detailedTask.startDateConstraint} - ${detailedTask.finishDateConstraint}"
        } else {
            tv_date_value.visibility = View.GONE
            iv_date.visibility = View.GONE

        }



        when (detailedTask.goalType) {
            "STEPS" -> {
                iv_goal_type.setImageDrawable(mContext.getDrawable(R.drawable.shos))
                tv_goal_value.text = "${detailedTask.goalValue} steps"
                tv_steps_counter.visibility = View.VISIBLE
            }
            "LOCATION" -> {
                iv_goal_type.setImageDrawable(mContext.getDrawable(R.drawable.type_location))
                tv_goal_value.text = "Find place"
                tv_steps_counter.visibility = View.GONE
            }
            "SECRET_KEY" -> {
                iv_goal_type.setImageDrawable(mContext.getDrawable(R.drawable.type_key))
                tv_goal_value.text = "Find key"
                tv_steps_counter.visibility = View.GONE
            }
            "QR_CODE" -> {
                iv_goal_type.setImageDrawable(mContext.getDrawable(R.drawable.type_qr))
                tv_goal_value.text = "Find QR-code"
                tv_steps_counter.visibility = View.GONE
            }
        }

        tv_time_value.text = "${detailedTask.completionTime} min"


        if (detailedTask.photos.size > 0) {
            initDots(ll_dots_task, dotsTaskList, detailedTask.photos)
            photosTask = detailedTask.photos
            Log.d("DEBUG", "size: ${photosTask.size}")

            var adapter = ImagesAdapter(requireActivity(), "tasks")
            vp_images_task.adapter = adapter
            vp_images_task.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    for (x in dotsTaskList)
                        x.background = requireContext().getDrawable(R.drawable.item_inactive)

                    dotsTaskList.get(position).background = requireContext().getDrawable(R.drawable.item_active)

                }
            })
        } else {
            cv_photos_task_container.visibility = View.GONE
        }
    }

    fun getTaskDetails(task: Task) {

        currentTask = task

        val queue = Volley.newRequestQueue(mContext)

        val mStringRequest = object : StringRequest(
                Request.Method.GET,
                "http://wsk2019.mad.hakta.pro/api/tasks/${task.id}",
                Response.Listener { response ->
                    val o = JSONObject(response).getJSONObject("content")
                    val taskDetails: TaskDetails = Gson().fromJson(o.toString(), TaskDetails::class.java)

                    initTask(taskDetails)
                },
                Response.ErrorListener { error ->
                    Log.d("DEBUG", "error: ${error.message}")
                }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val m = HashMap<String, String>()
                m.put("Token", CacheHelper(mContext).getToken())
                return m
            }
        }
        queue.add(mStringRequest)
    }

    fun showDialog() {
        val r = JSONObject()
        val et = EditText(mContext)
        var key = ""

        val dialog = AlertDialog.Builder(mContext)
                .setView(et)
                .setTitle("Input your secret key")
                .setPositiveButton("SEND") { dialog, which ->
                    key = et.text.toString()
                    finishKeyTask(key)
                }
                .setNegativeButton("CANCEL") { dialog, which -> }
                .create()

        dialog.show()

    }

    fun finishKeyTask(key: String) {
        val queue = Volley.newRequestQueue(mContext)

        val mStringRequest = object : StringRequest(
                Request.Method.PUT,
                "http://wsk2019.mad.hakta.pro/api/tasks/${currentTask.id}/result",
                Response.Listener { response ->

                },
                Response.ErrorListener { error ->

                }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {

                val r = JSONObject()
                r.put("key", key)
                return r.toString().toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val m = HashMap<String, String>()
                m.put("Token", CacheHelper(mContext).getToken())
                return m
            }
        }
        queue.add(mStringRequest)
    }

    fun finishQrTask(key: String) {
        val queue = Volley.newRequestQueue(mContext)

        val mStringRequest = object : StringRequest(
                Request.Method.PUT,
                "http://wsk2019.mad.hakta.pro/api/tasks/${currentTask.id}/result",
                Response.Listener { response ->

                },
                Response.ErrorListener { error ->

                }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {

                val r = JSONObject()
                r.put("key", key)
                return r.toString().toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val m = HashMap<String, String>()
                m.put("Token", CacheHelper(mContext).getToken())
                return m
            }
        }
        queue.add(mStringRequest)
    }

    fun finishStepsTask(steps: String) {
        val queue = Volley.newRequestQueue(mContext)

        val mStringRequest = object : StringRequest(
                Request.Method.PUT,
                "http://wsk2019.mad.hakta.pro/api/tasks/${currentTask.id}/result",
                Response.Listener { response ->

                },
                Response.ErrorListener { error ->

                }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {

                val r = JSONObject()
                r.put("steps", steps)
                return r.toString().toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val m = HashMap<String, String>()
                m.put("Token", CacheHelper(mContext).getToken())
                return m
            }
        }
        queue.add(mStringRequest)
    }

    fun finishTask() {


        when (detailedTask.goalType) {
            "STEPS" -> {
                finishStepsTask(detailedTask.goalValue)
            }
            "SECRET_KEY" -> {
                showDialog()
            }
            "QR_CODE" -> {
                initCameraPerms()
            }
        }


    }

    fun initTags() {
        for (x in quest.tags) {
            val inflater = LayoutInflater.from(requireContext())
            val tag = inflater.inflate(R.layout.quest_tag, null, false)
            val tv_title = tag.findViewById<TextView>(R.id.tv_title)
            tv_title.text = x
            ll_tags.addView(tag)
        }
    }

    fun initRating() {
        var rating = quest.rating.toDouble().roundToInt()
        if (rating > 5) rating = 5
        for (x in 0 until rating) {
            stars.get(x).background = requireContext().getDrawable(R.drawable.star_filled)
        }
    }

    fun checkFitPerms() {
        if (mContext.checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 2)
        }   else {
            connectFitness()
        }
    }

    fun initCameraPerms() {
        if (mContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
        } else {
            initScanner()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initScanner()
            } else {
                Toast.makeText(mContext, "I need this permission!!!", LENGTH_LONG).show()
                initCameraPerms()
            }
        }   else {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                connectFitness()
            }   else {
                Toast.makeText(mContext, "I need this permission dude...", LENGTH_LONG).show()
            }
        }
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

    override fun onProviderEnabled(provider: String) {
        Log.d("DEBUG", "enabled")
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_quest_detail, container, false)

        checkFitPerms()

        initViews(view)
        listener = null
        TAG = "DEBUG"

        dotsList = mutableListOf()
        dotsTaskList = mutableListOf()

        fragment = this

        val q: QuestItem = requireArguments().getSerializable("quest") as QuestItem
        quest = q
        photos = q.photos

        initComments()
        initDots(ll_dots, dotsList, photos)
        initTags()
        initRating()
        setTasks()
        sendCommentSection()
        getProfile()
        initChat()

        mv_map.getMapAsync(this)
        mv_map.onCreate(savedInstanceState)
        mv_map.onResume()

        tv_steps_counter.setOnClickListener(View.OnClickListener {
            openStepsCounter()
        })

        tv_action_button.setOnClickListener(View.OnClickListener {
            when (currentTask.status) {
                "NOT_STARTED" -> {
                    startTask()
                }
                "IN_PROGRESS" -> {
                    finishTask()
                }
            }
        })

        iv_audio_toggle.setOnClickListener(View.OnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                iv_audio_toggle.setImageDrawable(mContext.getDrawable(R.drawable.play))
            } else {
                mediaPlayer.start()
                iv_audio_toggle.setImageDrawable(mContext.getDrawable(R.drawable.pause))
            }
        })

        if (q.tasks.size > 0)
            getTaskDetails(q.tasks.get(0))

        mv_map.onCreate(savedInstanceState)

        tv_title.text = q.name
        tv_difficulty.text = q.difficulty
        tv_author.text = q.authorName
        tv_category.text = q.category.name
        tv_date.text = q.creationDate
        tv_description.text = q.description

        var adapter = ImagesAdapter(requireActivity(), "quests")
        vp_images_quest.adapter = adapter
        vp_images_quest.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                for (x in dotsList)
                    x.background = requireContext().getDrawable(R.drawable.item_inactive)

                dotsList.get(position).background = requireContext().getDrawable(R.drawable.item_active)

            }
        })

        return view
    }


    fun startTask() {
        val mRequestQueue = Volley.newRequestQueue(mContext)
        val mStringRequest = object : StringRequest(Request.Method.PUT, "http://wsk2019.mad.hakta.pro/api/tasks/${currentTask.id}/start", Response.Listener { response ->

            if (response.equals("\"OK\"")) {
                getTaskDetails(currentTask)
            } else {
                Toast.makeText(mContext, "You already have task in progress status", LENGTH_LONG).show()
            }

        }, Response.ErrorListener { error ->

            Toast.makeText(mContext, "You already have task in progress status", LENGTH_LONG).show()

        }) {

            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map.put("Token", CacheHelper(mContext).getToken())
                Log.d("DEBUG", "request token: ${CacheHelper(mContext).getToken()}")
                return map
            }
        }

        mRequestQueue.add(mStringRequest)
    }

    fun showPhoto(url: String) {

        val layoutInflater = LayoutInflater.from(mContext)
        val view = layoutInflater.inflate(R.layout.photo_view, null, false)
        val iv_image = view.findViewById<ImageView>(R.id.iv_image)
        Picasso.get().load(url).into(iv_image)

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

        val tv_close = view.findViewById<TextView>(R.id.tv_close)
        val tv_save = view.findViewById<TextView>(R.id.tv_save)

        tv_close.setOnClickListener(View.OnClickListener {
            cl_container.removeView(view)
        })

        tv_save.setOnClickListener(View.OnClickListener {
            Handler(Looper.getMainLooper()).post {
                Runnable {
                    val b: Bitmap = Picasso.get().load(url).get()
                    saveMediaToStorage(b)
                }
            }
        })

        set.applyTo(cl_container)


        cl_container.addView(view)

    }

    fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "${System.currentTimeMillis()}.jpg"

        var fos: OutputStream? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context?.contentResolver?.also { resolver ->

                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val imageUri: Uri? =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(mContext, "Saved to Photos", LENGTH_LONG).show()
        }
    }

    fun setTasks() {
        val tasks: MutableList<Task> = quest.tasks
        if (tasks.size == 0) {
            ll_task_info.visibility = View.GONE
            ll_tasks.visibility = View.GONE
        } else {
            ll_task_info.visibility = View.VISIBLE
            ll_tasks.visibility = View.VISIBLE
        }
        val adapter: TasksAdapter = TasksAdapter(tasks, requireContext(), this)
        val layoutManager: LinearLayoutManager = LinearLayoutManager(context)

        rv_tasks.adapter = adapter
        rv_tasks.layoutManager = layoutManager
    }

    fun initChat() {
        ll_chat_toggle.setOnClickListener(View.OnClickListener {

            if (isChatToggled) {
                ll_chat.visibility = View.GONE
            } else {
                ll_chat.visibility = View.VISIBLE
            }

            isChatToggled = !isChatToggled
        })

        iv_send_message.setOnClickListener(View.OnClickListener {
            sendMessage(et_chat_text.text.toString())
        })
    }

    fun sendMessage(text: String) {
        Log.d("DEBUG", "text: $text")
        val queue = Volley.newRequestQueue(mContext)
        val request = object : StringRequest(
                Request.Method.POST,
                "http://wsk2019.mad.hakta.pro/api/tasks/${currentTask.id}/messages",
                Response.Listener { response ->
                    if (response.equals("\"Success\"")) {
                        refreshChat()
                        et_chat_text.text = null
                    }
                },
                Response.ErrorListener { error ->
                }) {
            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params.put("Token", CacheHelper(mContext).getToken())
                return params
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                val requestObject = JSONObject()
                requestObject.put("text", text)
                return requestObject.toString().toByteArray()
            }
        }

        queue.add(request)
    }

    fun openStepsCounter() {
        val layoutInflater = LayoutInflater.from(mContext)
        val view : View = layoutInflater.inflate(R.layout.steps_counter, null, false)

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

        val ll_progress : LinearLayout = view.findViewById(R.id.ll_progress)
        val ll_from : LinearLayout = view.findViewById(R.id.ll_from)
        val tv_goal : TextView = view.findViewById(R.id.tv_goal)
        val tv_current_steps : TextView = view.findViewById(R.id.tv_current_steps)
        val tv_send : TextView = view.findViewById(R.id.tv_send)
        val tv_close : TextView = view.findViewById(R.id.tv_close)

        tv_current_steps.text = "Current steps: " + CacheHelper(mContext).getSteps()
        tv_goal.text = detailedTask.goalValue

        val lParamsProgress : LinearLayout.LayoutParams = ll_progress.layoutParams as LinearLayout.LayoutParams
        val lParamsFrom : LinearLayout.LayoutParams = ll_from.layoutParams as LinearLayout.LayoutParams

        val progress = 1f
        val progressPercent = CacheHelper(mContext).getSteps() / detailedTask.goalValue.toDouble()
        val fromPercent = progress-progressPercent

        if(progressPercent <= 0.2) {
            ll_progress.setBackgroundColor(mContext.getColor(R.color.progress_step_1))
            ll_from.setBackgroundColor(mContext.getColor(R.color.bg_step_1))
        }

        if(progressPercent >= 0.21 && progressPercent <= 0.4) {
            ll_progress.setBackgroundColor(mContext.getColor(R.color.progress_step_2))
            ll_from.setBackgroundColor(mContext.getColor(R.color.bg_step_2))
        }

        if(progressPercent >= 0.41 && progressPercent <= 0.60) {
            ll_progress.setBackgroundColor(mContext.getColor(R.color.progress_step_3))
            ll_from.setBackgroundColor(mContext.getColor(R.color.bg_step_3))
        }

        if(progressPercent >= 0.61) {
            ll_progress.setBackgroundColor(mContext.getColor(R.color.aqua))
            ll_from.setBackgroundColor(mContext.getColor(R.color.darkAqua))
        }


        lParamsProgress.weight = fromPercent.toFloat()
        lParamsFrom.weight = progressPercent.toFloat()

        tv_send.setOnClickListener(View.OnClickListener {

        })

        tv_close.setOnClickListener(View.OnClickListener {
            cl_container.removeView(view)
        })

        cl_container.addView(view)
    }

    fun refreshChat() {
        val queue = Volley.newRequestQueue(mContext)
        val request = object : StringRequest(
                Request.Method.GET,
                "http://wsk2019.mad.hakta.pro/api/tasks/${currentTask.id}/messages",
                Response.Listener { response ->
                    val json = JSONObject(response).getJSONArray("content")
                    val list: MutableList<Message> = mutableListOf()

                    for (x in 0 until json.length()) {
                        val o = json.getJSONObject(x)
                        val message: Message = Gson().fromJson(o.toString(), Message::class.java)

                        if (message.author.id.equals(profile.id)) {
                            message.isOutgoing = true
                        }

                        list.add(message)
                    }

                    val linearLayoutManager = LinearLayoutManager(mContext)
                    val adapter = ChatAdapter(list, mContext)

                    rv_chat.adapter = adapter
                    rv_chat.layoutManager = linearLayoutManager

                    val lastItemPos = rv_chat.adapter!!.itemCount - 1
                    linearLayoutManager.scrollToPositionWithOffset(lastItemPos, 0)


                },
                Response.ErrorListener { error ->

                }) {
            override fun getHeaders(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params.put("Token", CacheHelper(mContext).getToken())
                return params
            }
        }

        queue.add(request)
    }

    lateinit var profile: com.grinvald.grinvaldmadventure.models.Profile
    fun getProfile() {
        val queue = Volley.newRequestQueue(mContext)
        val request = object : StringRequest(
                Request.Method.GET,
                "http://wsk2019.mad.hakta.pro/api/user/profile",
                Response.Listener { response ->
                    val json = JSONObject(response).getJSONObject("content").toString()

                    val profile: com.grinvald.grinvaldmadventure.models.Profile = Gson().fromJson(json, com.grinvald.grinvaldmadventure.models.Profile::class.java)

                    this.profile = profile
                    refreshChat()
                },
                Response.ErrorListener { error ->

                }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Token"] = CacheHelper(mContext).getToken()
                return headers
            }
        }

        queue.add(request)

    }

    private inner class ImagesAdapter(fa: FragmentActivity, type: String) : FragmentStateAdapter(fa) {

        val type = type

        override fun getItemCount(): Int {
            return photos.size
        }

        override fun createFragment(position: Int): Fragment {
            var photo = ""
            if (type.equals("tasks")) {
                Log.d("DEBUG", "pos: $position")
                var pos = position
                if (pos == photosTask.size) pos -= 1
                photo = photosTask.get(pos)
            } else photo = photos.get(position)

            return Photo(fragment).newInstance(photo)

        }

    }


    override fun onMapReady(p0: GoogleMap?) {
        Log.d("DEBUG", "map ready")
        if (p0 != null) {
            map = p0
        }

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

}