package com.grinvald.grinvaldmadventure.models

import java.io.Serializable

class Category : Serializable{
    var id : String
    var name : String
    var description : String
    var photo : String

    constructor(id: String, name: String, description: String, photo: String) {
        this.id = id
        this.name = name
        this.description = description
        this.photo = photo
    }
}