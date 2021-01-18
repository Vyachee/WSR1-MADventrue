package com.grinvald.grinvaldmadventure.models

import java.io.Serializable

class Profile : Serializable{
    var id : String
    var email : String
    var firstName : String
    var lastName : String
    var nickName : String
    var avatar : String
    var city : String
    var createdQuests : MutableList<QuestItem>
    var completedQuests : MutableList<QuestItem>
    var authorRating : String
    var userRating : String
    var achievements : MutableList<Achievement>

    constructor(id: String, email: String, firstName: String, lastName: String, nickName: String, avatar: String, city: String, createdQuests: MutableList<QuestItem>, completedQuests: MutableList<QuestItem>, authorRating: String, userRating: String, achievements: MutableList<Achievement>) {
        this.id = id
        this.email = email
        this.firstName = firstName
        this.lastName = lastName
        this.nickName = nickName
        this.avatar = avatar
        this.city = city
        this.createdQuests = createdQuests
        this.completedQuests = completedQuests
        this.authorRating = authorRating
        this.userRating = userRating
        this.achievements = achievements
    }
}