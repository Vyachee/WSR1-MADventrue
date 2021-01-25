package com.grinvald.grinvaldmadventure.models.test

class Task {
    var taskCompletionTime : Long
    var taskFinishDate : Long
    var taskStartDate : Long

    constructor(taskCompletionTime: Long, taskFinishDate: Long, taskStartDate: Long) {
        this.taskCompletionTime = taskCompletionTime
        this.taskFinishDate = taskFinishDate
        this.taskStartDate = taskStartDate
    }
}