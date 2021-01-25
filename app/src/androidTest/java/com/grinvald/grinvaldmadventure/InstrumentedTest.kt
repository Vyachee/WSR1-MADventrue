package com.grinvald.grinvaldmadventure

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.grinvald.grinvaldmadventure.common.ProfileHelper
import com.grinvald.grinvaldmadventure.models.test.Quest
import com.grinvald.grinvaldmadventure.models.test.Task

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class InstrumentedTest {

    @Test
    fun calculateLevelWithRightData() {

        val list : MutableList<Quest> = mutableListOf()

        list.add(Quest(1, mutableListOf(
            Task(30, 1611560618, 1611558818),
            Task(45, 1611560618, 1611568818)
        )))

        list.add(Quest(4, mutableListOf(
            Task(80, 1611560618, 1611558818),
            Task(115, 1611560618, 1611568818)
        )))

        list.add(Quest(9, mutableListOf(
            Task(345, 1611560618, 1611558818),
            Task(294, 1611560618, 1611568818)
        )))

        val level = ProfileHelper(list).calcLevel()
        assertEquals(level, 4)

    }

    @Test
    fun calculateLevelWithIncorrectData() {

        val list : MutableList<Quest> = mutableListOf()

        list.add(Quest(1, mutableListOf(
            Task(30, 1611560618, 1611558818),
            Task(45, 1611568818, 1611560618)
        )))

        list.add(Quest(4, mutableListOf(
            Task(80, 1611558818,1611560618),
            Task(0, 1611560618, 1611568818)
        )))

        list.add(Quest(9, mutableListOf(
            Task(-10,  1611558818, 1611560618),
            Task(294, 1611560618, 1611568818)
        )))

        val level = ProfileHelper(list).calcLevel()
        assertEquals(level, 0)

    }

    @Test
    fun calculateLevelWithIncorrectData2() {

        val list : MutableList<Quest> = mutableListOf()

        list.add(Quest(1, mutableListOf(
            Task(30, 1611558818, 1611558818),
            Task(45, 1611568818, 1611560618)
        )))

        list.add(Quest(4, mutableListOf(
            Task(80, 1611558818,1611560618),
            Task(0, 1611560618, 1611568818)
        )))

        list.add(Quest(9, mutableListOf(
            Task(-10,  1611558818, 1611560618),
            Task(294, 1611560618, 1611568818)
        )))

        val level = ProfileHelper(list).calcLevel()
        assertEquals(level, 0)

    }
}