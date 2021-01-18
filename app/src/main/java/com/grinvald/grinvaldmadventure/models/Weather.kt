package com.grinvald.grinvaldmadventure.models

class Weather {

    var temperature : String
    var pressure : String
    var humidity : String
    var windSpeed : String
    var windDegree : String

    constructor(
        temperature: String,
        pressure: String,
        humidity: String,
        windSpeed: String,
        windDegree: String
    ) {
        this.temperature = temperature
        this.pressure = pressure
        this.humidity = humidity
        this.windSpeed = windSpeed
        this.windDegree = windDegree
    }
}