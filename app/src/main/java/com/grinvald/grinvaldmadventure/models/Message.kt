package com.grinvald.grinvaldmadventure.models

import org.json.JSONObject

class Message {
    var id : String
    var text : String
    var date : String
    var author : Author
    var isOutgoing : Boolean = false

    constructor(id: String, text: String, date: String, author: Author) {
        this.id = id

        this.text = text

        this.date = date
        this.author = author
    }

}