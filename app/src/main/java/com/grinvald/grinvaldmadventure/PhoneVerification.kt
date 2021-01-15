package com.grinvald.grinvaldmadventure

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import kotlin.concurrent.thread

class PhoneVerification : AppCompatActivity() {

    lateinit var et_d1 : EditText
    lateinit var et_d2 : EditText
    lateinit var et_d3 : EditText
    lateinit var et_d4 : EditText

    lateinit var tv_phone : TextView
    lateinit var tv_timer : TextView
    lateinit var tv_resend : TextView

    lateinit var extras : Bundle

    var seconds : Int = 300
    var stopped = false

    lateinit var code : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_verification)

        code = ""
        initViews()
        initFields()

        Thread(Runnable {

            while (!stopped) {
                seconds--
                tv_timer.text = "$seconds sec"
                if (seconds <= 0) {
                    stopped = true
                    tv_resend.setTextColor(getColor(R.color.aqua))
                }
                android.os.SystemClock.sleep(1000)
            }
        }).start()

        extras = intent!!.extras!!
        initPhoneNumber(
            extras.getString("phone").toString(),
            extras.getString("code").toString()
        )

        tv_resend.setOnClickListener(View.OnClickListener {
            if (seconds <= 0) {
                tv_resend.setTextColor(getColor(R.color.gray))
                seconds = 300
                stopped = false
            }
        })
    }

    private fun refreshCode() {
        code = ""
        code += et_d1.text.toString()
        code += et_d2.text.toString()
        code += et_d3.text.toString()
        code += et_d4.text.toString()

        if(code.length == 4) {
            sendCode()
        }
    }

    private fun sendCode() {
        val mRequestQueue = Volley.newRequestQueue(this)
        val mStringRequest = object: StringRequest(Request.Method.PUT, "http://wsk2019.mad.hakta.pro/api/user/activation", Response.Listener {response ->

            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
            finish()

        }, Response.ErrorListener { error ->

        }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }
            override fun getBody(): ByteArray {
                val params = HashMap<String, String>()
                params.put("code", code)
                return JSONObject(params as Map<*, *>).toString().toByteArray()
            }
        }

        mRequestQueue.add(mStringRequest)
    }

    private fun initFields() {
        val delay : Long = 50

        et_d4.setOnKeyListener(View.OnKeyListener { view: View, i: Int, keyEvent: KeyEvent ->
            Handler().postDelayed({
                if (keyEvent.keyCode == KeyEvent.KEYCODE_DEL && et_d4.text.toString().isEmpty())
                    et_d3.requestFocus()

            }, delay)
            refreshCode()
            false

        })

        et_d3.setOnKeyListener(View.OnKeyListener { view: View, i: Int, keyEvent: KeyEvent ->
            Handler().postDelayed({
                if (keyEvent.keyCode == KeyEvent.KEYCODE_DEL && et_d3.text.toString().isEmpty())
                    et_d2.requestFocus()
                else if(keyEvent.keyCode != KeyEvent.KEYCODE_DEL) et_d4.requestFocus()
            }, delay)
            refreshCode()
            false

        })

        et_d2.setOnKeyListener(View.OnKeyListener { view: View, i: Int, keyEvent: KeyEvent ->
            Handler().postDelayed({
                if (keyEvent.keyCode == KeyEvent.KEYCODE_DEL && et_d2.text.toString().isEmpty())
                    et_d1.requestFocus()
                else if(keyEvent.keyCode != KeyEvent.KEYCODE_DEL) et_d3.requestFocus()
            }, delay)
            refreshCode()
            false

        })

        et_d1.setOnKeyListener(View.OnKeyListener { view: View, i: Int, keyEvent: KeyEvent ->
            Handler().postDelayed({
                if (keyEvent.keyCode != KeyEvent.KEYCODE_DEL) {
                    et_d2.requestFocus()
                }
            }, delay)
            refreshCode()
            false

        })
    }

    private fun initPhoneNumber(phoneNumber: String, phoneCode: String) {
        val phone = phoneNumber
        val number = phone!!.replaceFirst("(\\d{3})(\\d{3})(\\d{2})(\\d{2})".toRegex(), "$1 $2 $3 $4")
        val code = phoneCode
        tv_phone.text = "$code $number"
    }

    private fun initViews() {
        et_d1 = findViewById(R.id.et_d1)
        et_d2 = findViewById(R.id.et_d2)
        et_d3 = findViewById(R.id.et_d3)
        et_d4 = findViewById(R.id.et_d4)
        tv_phone = findViewById(R.id.tv_phone)
        tv_timer = findViewById(R.id.tv_timer)
        tv_resend = findViewById(R.id.tv_resend)
    }
}