package com.grinvald.grinvaldmadventure

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.grinvald.grinvaldmadventure.common.CacheHelper
import org.json.JSONObject

class SignIn : AppCompatActivity() {

    lateinit var et_email : EditText
    lateinit var et_password : EditText

    lateinit var tv_signup : TextView
    lateinit var tv_signin : TextView
    lateinit var ll_facebook_login : LinearLayout

    lateinit var callbackManager : CallbackManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        initViews()

        et_email.setText(CacheHelper(this).getEmail())

        tv_signup.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        })

        tv_signin.setOnClickListener(View.OnClickListener {
            val email = et_email.text.toString()
            val password = et_password.text.toString()
            login(email, password)
        })

        ll_facebook_login.setOnClickListener(View.OnClickListener {
            Log.d("DEBUG", "click")
            LoginManager.getInstance().logInWithReadPermissions(this, mutableListOf("public_profile"))
            callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    val token = result!!.accessToken
                    val sToken = token.token

                    if(token.isExpired()) {
                        CacheHelper(baseContext).writeFacebookToken(sToken)
                        val intent = Intent(baseContext, MainScreen::class.java)
                        startActivity(intent)
                    }

                    Log.d("DEBUG", "token: $sToken")
                }

                override fun onCancel() {
                    TODO("Not yet implemented")
                }

                override fun onError(error: FacebookException?) {
                    TODO("Not yet implemented")
                }

            })
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun login(email: String, password: String) {
        val queue = Volley.newRequestQueue(this)
        val request = object: StringRequest(Request.Method.POST, "http://wsk2019.mad.hakta.pro/api/user/login",
        Response.Listener { response ->

            val r = JSONObject(response)
            val token = r.getString("token")

            CacheHelper(this).writeToken(token)

            val intent = Intent(this, MainScreen::class.java)
            startActivity(intent)
            finish()

        }, Response.ErrorListener { error ->

            }) {
            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                val json = JSONObject()
                json.put("email", email)
                json.put("password", password)
                return json.toString().toByteArray()
            }
        }

        queue.add(request)
    }

    private fun initViews() {
        et_email = findViewById(R.id.et_email)
        et_password = findViewById(R.id.et_password)
        tv_signup = findViewById(R.id.tv_signup)
        tv_signin = findViewById(R.id.tv_signin)
        ll_facebook_login = findViewById(R.id.ll_facebook_login)
    }
}