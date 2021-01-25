package com.grinvald.grinvaldmadventure.common

import android.content.Context
import com.grinvald.grinvaldmadventure.models.Profile
import com.grinvald.grinvaldmadventure.models.QuestItem
import com.grinvald.grinvaldmadventure.models.test.Quest
import com.grinvald.grinvaldmadventure.models.test.Task
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ln
import kotlin.math.round

class ProfileHelper(list: MutableList<Quest>) {

    val list = list


    fun calcLevel() : Int{

        val level = round(ln((calcPoints() / 5) + 1) + 1).toInt()
        return level

    }

    fun calcPoints() : Double {

        var points = 0.00

        for(x in 0 until list.size) {
            val quest = list.get(x)
            points += calcQuestPoint(x, quest)
        }

        return points
    }

    fun calcQuestPoint(i: Int, quest: Quest) : Double {

        var questPoint : Double = quest.questDifficulty.toDouble()

        var sum = 0.00
        for(x in quest.tasks) {
            val format = SimpleDateFormat("yyyy-MM-dd")
            val date1: Date = Date(x.taskStartDate.toLong())
            val date2: Date = Date(x.taskFinishDate.toLong())

            var diff: Long = date2.time - date1.time
            if(diff <= 0) {
                diff = 1
            }
            val seconds = diff

            val minutes = seconds / 60
            val needleMinutes = x.taskCompletionTime.toDouble()

            val result = needleMinutes / minutes
            sum += result
        }

        questPoint *= sum

        return questPoint
    }
}