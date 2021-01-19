package com.grinvald.grinvaldmadventure.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.constraintlayout.widget.ConstraintLayout
import com.grinvald.grinvaldmadventure.models.Category
import com.grinvald.grinvaldmadventure.models.QuestItem

class InternetHelper(context: Context) {
    val context = context

    fun checkConnection(): Boolean? {
        val cm: ConnectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.getActiveNetworkInfo()
        if (activeNetwork != null) {
            if (activeNetwork.getType() === ConnectivityManager.TYPE_WIFI) {
                return true
            } else if (activeNetwork.getType() === ConnectivityManager.TYPE_MOBILE) {
                return true
            }
        } else {
            return false
        }
        return false
    }

    fun getCategories(questList: MutableList<QuestItem>) : MutableList<Category> {

        val list : MutableList<Category> = mutableListOf()

        for(x in questList) {
            val category = x.category
            list.add(category)
        }

        return list

    }

}