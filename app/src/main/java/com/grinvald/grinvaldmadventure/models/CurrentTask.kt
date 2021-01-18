package com.grinvald.grinvaldmadventure.models

class CurrentTask {
    var id : String
    var name : String
    var description : String
    var completionTime : String
    var status : String
    var goalType : String
    var goalValue : String
    var location : Location
    var photos : MutableList<String>
    var videos : MutableList<String>
    var audios : MutableList<String>
    var quest : QuestItem
    var startDate : String
    var finishDate : String
    var startDateConstraint : String
    var finishDateConstraint : String

    constructor(id: String, name: String, description: String, completionTime: String, status: String, goalType: String, goalValue: String, location: Location, photos: MutableList<String>, videos: MutableList<String>, audios: MutableList<String>, quest: QuestItem, startDate: String, finishDate: String, startDateConstraint: String, finishDateConstraint: String) {
        this.id = id
        this.name = name
        this.description = description
        this.completionTime = completionTime
        this.status = status
        this.goalType = goalType
        this.goalValue = goalValue
        this.location = location
        this.photos = photos
        this.videos = videos
        this.audios = audios
        this.quest = quest
        this.startDate = startDate
        this.finishDate = finishDate
        this.startDateConstraint = startDateConstraint
        this.finishDateConstraint = finishDateConstraint
    }
}