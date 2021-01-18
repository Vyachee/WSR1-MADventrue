package com.grinvald.grinvaldmadventure

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.grinvald.grinvaldmadventure.common.CacheHelper

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler().postDelayed({

            val cacheHelper = CacheHelper(this)

            val defaultToken = cacheHelper.getToken()
            val facebookToken = cacheHelper.getFacebookToken()

            if(!defaultToken.equals("null") || !cacheHelper.getFacebookToken().equals("null")) {

                Log.d("DEBUG", "token: $defaultToken")
                Log.d("DEBUG", "facebook token: $facebookToken")

                val intent = Intent(this, MainScreen::class.java)
                startActivity(intent)
                finish()
            }   else {
                val intent = Intent(this, Tutorial::class.java)
                startActivity(intent)
                finish()
            }

        }, 2000)
    }
}