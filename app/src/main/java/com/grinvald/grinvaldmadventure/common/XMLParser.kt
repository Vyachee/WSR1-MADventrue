package com.grinvald.grinvaldmadventure.common

import android.util.Log
import com.grinvald.grinvaldmadventure.models.Weather
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

class XMLParser {
    fun parseWeather(xml: String) : Weather? {

        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val xpp = factory.newPullParser()

        xpp.setInput(StringReader(xml))
        var eventType = xpp.eventType

        val valuesList = mutableListOf<String>()

        while(eventType != XmlPullParser.END_DOCUMENT) {

            if(eventType == XmlPullParser.TEXT)
                valuesList.add(xpp.text)

            eventType = xpp.next()
        }

        val weatherList = mutableListOf<String>()
        for(x in valuesList) {
            if(valuesList.indexOf(x) % 2 != 0) {
                weatherList.add(x)
            }
        }

        val weather = Weather(
            weatherList[0],
            weatherList[1],
            weatherList[2],
            weatherList[3],
            weatherList[4]
        )

        return weather
    }
}