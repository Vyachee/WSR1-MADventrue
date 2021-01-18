package com.grinvald.grinvaldmadventure.models

class Achievement {
    var id : String
    var name : String
    var description : String
    var icon : String

    constructor(id: String, name: String, description: String, icon: String) {
        this.id = id
        this.name = name
        this.description = description
        this.icon = icon
    }
}