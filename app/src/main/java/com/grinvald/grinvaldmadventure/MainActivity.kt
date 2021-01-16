package com.grinvald.grinvaldmadventure

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.viewpager2.widget.ViewPager2
import com.grinvald.grinvaldmadventure.common.CacheHelper

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed({

            val cacheHelper = CacheHelper(this)
            if(cacheHelper.getToken() != null || cacheHelper.getFacebookToken() != null) {
                val intent = Intent(this, Tutorial::class.java)
                startActivity(intent)
                finish()
            }

        }, 2000)
    }
}