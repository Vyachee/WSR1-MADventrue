package com.grinvald.grinvaldmadventure.common

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.google.gson.Gson
import com.grinvald.grinvaldmadventure.models.QuestItem
import org.json.JSONArray
import org.json.JSONObject

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

    fun removeAuthData(){
        editor.remove("email")
        editor.remove("password")
        editor.remove("phone")
        editor.remove("token")
        editor.remove("facebook_token")
    }

    fun getEmail() : String {
        return prefs.getString("email", "").toString()
    }

    fun writeToken(token: String) {
        editor.putString("token", token)
        editor.apply()
    }

    fun getToken() : String {
        return prefs.getString("token", null).toString()
    }

    fun writeFacebookToken(token: String) {
        editor.putString("facebook_token", token)
        editor.apply()
    }

    fun getFacebookToken() : String {
        return prefs.getString("facebook_token", null).toString()
    }

    fun addToFavourites(quest: QuestItem) {

        val p = context.getSharedPreferences("favourites_quests", MODE_PRIVATE)

        val data = getFavouritesJSON()
        val editor = p.edit()

        var array : JSONArray
        if(data == null)
            array = JSONArray()
        else array = JSONArray(data)

        val o = JSONObject(Gson().toJson(quest, QuestItem::class.java))
        array.put(o)
        editor.putString("quests", array.toString())
        editor.apply()

    }

    fun getFavouritesJSON(): String? {
        val p = context.getSharedPreferences("favourites_quests", MODE_PRIVATE)
        return p.getString("quests", null)
    }

    fun getFavourites() : MutableList<QuestItem> {
        val json = getFavouritesJSON()
        val array = JSONArray(json)
        val list = mutableListOf<QuestItem>()

        for(x in 0 until array.length()) {
            val obj : QuestItem = Gson().fromJson(array.getJSONObject(x).toString(), QuestItem::class.java)
            list.add(obj)
        }

        return list
    }

    fun isInFavourites(quest: QuestItem) : Boolean {
        val json = getFavouritesJSON()
        if(json == null) return false
        val array = JSONArray(json)
        val list = mutableListOf<QuestItem>()

        for(x in 0 until array.length()) {

            val obj : QuestItem = Gson().fromJson(array.getJSONObject(x).toString(), QuestItem::class.java)

            if(obj.id.equals(quest.id))
                return true

        }

        return false
    }

    private fun saveFavouritesJson(list: String) {
        val p = context.getSharedPreferences("favourites_quests", MODE_PRIVATE)
        val editor = p.edit()
        editor.remove("quests")
        editor.putString("quests", list)
        Log.d("DEBUG", "saved $list")
        editor.apply()
    }

    fun removeFromFavourites(quest: QuestItem) {
        val list = getFavourites()
        for(x in 0 until list.size) {
            if(list.get(x).id.equals(quest.id)) {
                Log.d("DEBUG", "removed ${list.get(x).name}")
                list.removeAt(x)
            }
        }
        saveFavouritesJson(Gson().toJson(list))
    }



}