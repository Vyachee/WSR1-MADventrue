package com.grinvald.grinvaldmadventure.models

import android.graphics.drawable.Drawable
import java.io.Serializable

class QuestItem : Serializable {
    constructor(
        id: String,
        name: String,
        description: String,
        photos: MutableList<String>,
        creationDate: String,
        startDate: String,
        endDate: String,
        mainPhoto: String,
        difficulty: String,
        category: Category,
        tags: MutableList<String>,
        authorName: String,
        rating: String,
        tasks: MutableList<Task>
    ) {
        this.id = id
        this.name = name
        this.description = description
        this.photos = photos
        this.creationDate = creationDate
        this.startDate = startDate
        this.endDate = endDate
        this.mainPhoto = mainPhoto
        this.difficulty = difficulty
        this.category = category
        this.tags = tags
        this.authorName = authorName
        this.rating = rating
        this.tasks = tasks
    }

    var id : String
    var name : String
    var description : String
    var photos : MutableList<String>
    var creationDate : String
    var startDate : String
    var endDate : String
    var mainPhoto : String
    var difficulty : String
    var category: Category
    var tags : MutableList<String>
    var authorName : String
    var rating : String
    var tasks : MutableList<Task>




}