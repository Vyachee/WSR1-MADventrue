package com.grinvald.grinvaldmadventure.models

import java.io.Serializable

class Comment : Serializable {
    var id : String
    var text : String
    var rating : String
    var date : String
    var author: Author

    constructor(id: String, text: String, rating: String, date: String, author: Author) {
        this.id = id
        this.text = text
        this.rating = rating
        this.date = date
        this.author = author
    }
}