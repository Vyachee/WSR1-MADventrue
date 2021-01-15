package com.grinvald.grinvaldmadventure

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import com.airbnb.paris.extensions.style
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.grinvald.grinvaldmadventure.common.CacheHelper
import org.json.JSONObject

class SignUp : AppCompatActivity() {

    lateinit var et_code : EditText
    lateinit var et_email : EditText
    lateinit var et_nickname : EditText
    lateinit var et_phone : EditText
    lateinit var et_password : EditText
    lateinit var et_password_repeat : EditText

    lateinit var v_line : View

    lateinit var fl_container : FrameLayout

    lateinit var tv_email_error : TextView
    lateinit var tv_nickname_error : TextView
    lateinit var tv_phone_error : TextView
    lateinit var tv_password_error : TextView
    lateinit var tv_password_repeat_error : TextView
    lateinit var tv_signin : TextView
    lateinit var tv_signup : TextView

    lateinit var email : String
    lateinit var nickname : String
    lateinit var code : String
    lateinit var phone : String
    lateinit var password : String
    lateinit var password_repeat : String

    fun initViews() {
        et_code = findViewById(R.id.et_code)
        v_line = findViewById(R.id.v_line)
        et_email = findViewById(R.id.et_email)
        et_nickname = findViewById(R.id.et_nickname)
        et_phone = findViewById(R.id.et_phone)
        et_password = findViewById(R.id.et_password)
        et_password_repeat = findViewById(R.id.et_password_repeat)
        tv_signup = findViewById(R.id.tv_signup)
        fl_container = findViewById(R.id.fl_container)
        tv_signin = findViewById(R.id.tv_signin)
        tv_email_error = findViewById(R.id.tv_email_error)
        tv_nickname_error = findViewById(R.id.tv_nickname_error)
        tv_phone_error = findViewById(R.id.tv_phone_error)
        tv_password_error = findViewById(R.id.tv_password_error)
        tv_password_repeat_error = findViewById(R.id.tv_password_repeat_error)
    }

    fun validateFields() : Boolean {

        val email = et_email.text.toString()
        val nickname = et_nickname.text.toString()
        val code = et_code.text.toString()
        var phone = et_phone.text.toString()
        val password = et_password.text.toString()
        val password_repeat = et_password_repeat.text.toString()

        var success : Boolean = true

        if(!email.contains("@")) {
            tv_email_error.text = "Email must contain \"@\""
            tv_email_error.visibility = VISIBLE
            et_email.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_red))
            success = false
        }
        if(email.isEmpty()) {
            tv_email_error.text = "Email must not be empty"
            tv_email_error.visibility = VISIBLE;
            et_email.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_red))
            success = false
        }

        if(nickname.isEmpty()) {
            tv_nickname_error.text = "Nickname must not be empty"
            tv_nickname_error.visibility = VISIBLE
            et_nickname.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_red))
            success = false
        }

        if(code.isEmpty()) {
            tv_phone_error.text = "Code must not be empty"
            tv_phone_error.visibility = VISIBLE
            v_line.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_red))
            success = false
        }


        if(phone.length != 10){
            tv_phone_error.text = "Phone contain 10 digits"
            tv_phone_error.visibility = VISIBLE
            et_phone.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_red))
            success = false
        }
        if(phone.isEmpty()) {
            tv_phone_error.text = "Phone must not be empty"
            tv_phone_error.visibility = VISIBLE
            et_phone.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_red))
            success = false
        }

        if(password.isEmpty()) {
            tv_password_error.text = "Password must not be empty"
            tv_password_error.visibility = VISIBLE
            et_password.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_red))
            success = false
        }

        if(password_repeat.isEmpty()) {
            tv_password_repeat_error.text = "Password repeat must not be empty"
            tv_password_repeat_error.visibility = VISIBLE
            et_password_repeat.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_red))
            success = false
        }

        if(!password.equals(password_repeat)) {
            tv_password_error.text = "Passwords must be equals"
            tv_password_error.visibility = VISIBLE
            tv_password_repeat_error.text = "Passwords must be equals"
            tv_password_repeat_error.visibility = VISIBLE
            et_password.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_red))
            et_password_repeat.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.light_red))
            success = false
        }

        return success
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initViews()
        initFields()

        tv_signup.setOnClickListener(View.OnClickListener {

            email = et_email.text.toString()
            nickname = et_nickname.text.toString()
            code = et_code.text.toString()
            phone = et_phone.text.toString()
            password = et_password.text.toString()
            password_repeat = et_password_repeat.text.toString()

            if(validateFields())
                register(email, nickname, password, phone)



        })
    }

    fun initFields() {

        et_email.setOnFocusChangeListener(View.OnFocusChangeListener { view: View, b: Boolean ->
            if(b)
                et_email.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.aqua))
            else et_email.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.gray))

            tv_email_error.visibility = INVISIBLE;

        })

        et_nickname.setOnFocusChangeListener(View.OnFocusChangeListener { view: View, b: Boolean ->
            if(b)
                et_nickname.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.aqua))
            else et_nickname.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.gray))

            tv_nickname_error.visibility = INVISIBLE
        })

        et_code.setOnFocusChangeListener(View.OnFocusChangeListener { view: View, b: Boolean ->
            if(b)
                v_line.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.aqua))
            else v_line.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.gray))
            tv_phone_error.visibility = INVISIBLE
        })

        et_phone.setOnFocusChangeListener(View.OnFocusChangeListener { view: View, b: Boolean ->
            if(b)
                et_phone.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.aqua))
            else et_phone.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.gray))
            tv_phone_error.visibility = INVISIBLE
        })

        et_password.setOnFocusChangeListener(View.OnFocusChangeListener { view: View, b: Boolean ->
            if(b)
                et_password.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.aqua))
            else et_password.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.gray))

            tv_password_error.visibility = INVISIBLE
        })

        et_password_repeat.setOnFocusChangeListener(View.OnFocusChangeListener { view: View, b: Boolean ->
            if(b)
                et_password_repeat.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.aqua))
            else et_password_repeat.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.gray))

            tv_password_repeat_error.visibility = INVISIBLE;
        })

    }

    fun register(email: String, nickname: String, password: String, phone: String) {

        val mRequestQueue = Volley.newRequestQueue(this)
        val mStringRequest = object : StringRequest(Request.Method.POST, "http://wsk2019.mad.hakta.pro/api/users", Response.Listener { response ->
            if(response.equals("\"Success\"")) {


                CacheHelper(this)
                        .writeAuthData(email, password, phone)

                val intent = Intent(this, PhoneVerification::class.java)
                intent.putExtra("phone", et_phone.text.toString())
                intent.putExtra("code", "+" + et_code.text.toString())
                startActivity(intent)
                finish()
            }   else {
                val layoutInflater = LayoutInflater.from(this)
                val view = layoutInflater.inflate(R.layout.dialog_error, null, false)

                val tv_ok = view.findViewById<TextView>(R.id.tv_ok)
                val tv_description = view.findViewById<TextView>(R.id.tv_description)
                tv_description.text = "User with such email already exists"

                tv_ok!!.setOnClickListener(View.OnClickListener {
                    fl_container.removeView(view)
                })

                fl_container.addView(view)

            }
        }, Response.ErrorListener { error ->
            val layoutInflater = LayoutInflater.from(this)
            val view = layoutInflater.inflate(R.layout.dialog_error, null, false)

            val tv_ok = view.findViewById<TextView>(R.id.tv_ok)

            val tv_description = view.findViewById<TextView>(R.id.tv_description)
            tv_description.text = "Something terrible happened"

            tv_ok!!.setOnClickListener(View.OnClickListener {
                fl_container.removeView(view)
            })

            fl_container.addView(view)
        }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                val params2 = HashMap<String, String>()
                params2.put("email", email)
                params2.put("nickName", nickname)
                params2.put("password", password)
                params2.put("phone", phone)
                return JSONObject(params2 as Map<*, *>).toString().toByteArray()
            }

        }
        mRequestQueue!!.add(mStringRequest!!)
    }

}