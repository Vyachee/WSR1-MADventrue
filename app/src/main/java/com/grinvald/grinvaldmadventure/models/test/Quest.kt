package com.grinvald.grinvaldmadventure.models.test

class Quest {
    val questDifficulty : Int
    val tasks : List<Task>

    constructor(questDifficulty: Int, tasks: List<Task>) {
        this.questDifficulty = questDifficulty
        this.tasks = tasks
    }
}