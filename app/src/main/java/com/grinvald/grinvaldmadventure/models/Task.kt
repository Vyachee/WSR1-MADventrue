package com.grinvald.grinvaldmadventure.models

import java.io.Serializable

class Task : Serializable {
    var name : String
    var id : String
    var status : String
    var taskCompletionTime : String
    var startDate : String
    var endDate : String

    constructor(
        name: String,
        id: String,
        status: String,
        taskCompletionTime: String,
        startDate: String,
        endDate: String
    ) {
        this.name = name
        this.id = id
        this.status = status
        this.taskCompletionTime = taskCompletionTime
        this.startDate = startDate
        this.endDate = endDate
    }
}