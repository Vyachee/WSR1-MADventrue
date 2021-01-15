package com.grinvald.grinvaldmadventure

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.grinvald.grinvaldmadventure.common.CacheHelper

class SignIn : AppCompatActivity() {

    lateinit var et_email : EditText
    lateinit var et_password : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        initViews()
        et_email.setText(CacheHelper(this).getEmail())

    }

    private fun initViews() {
        et_email = findViewById(R.id.et_email)
        et_password = findViewById(R.id.et_password)
    }
}