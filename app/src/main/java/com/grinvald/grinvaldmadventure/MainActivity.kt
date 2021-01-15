package com.grinvald.grinvaldmadventure

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        val intent = Intent(this, PhoneVerification::class.java)
//        intent.putExtra("phone", "9998884433")
//        intent.putExtra("code", "+7")
//        startActivity(intent)
//        finish()

        Handler().postDelayed({
            val intent = Intent(this, Tutorial::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}