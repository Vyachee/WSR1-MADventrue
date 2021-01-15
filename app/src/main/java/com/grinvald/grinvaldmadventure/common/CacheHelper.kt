package com.grinvald.grinvaldmadventure.common

import android.content.Context
import android.content.Context.MODE_PRIVATE

class CacheHelper (context: Context){

    val context = context;
    val prefs = context.getSharedPreferences("authData", MODE_PRIVATE)
    val editor = prefs.edit()

    fun writeAuthData(email: String, password: String, phone: String) {
        editor.putString("email", email)
        editor.putString("password", password)
        editor.putString("phone", phone)
        editor.apply()
    }

    fun getEmail() : String {
        return prefs.getString("email", "").toString()
    }

}