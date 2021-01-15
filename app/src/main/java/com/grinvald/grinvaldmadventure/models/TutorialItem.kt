package com.grinvald.grinvaldmadventure.models

import android.graphics.drawable.Drawable
import java.io.Serializable

class TutorialItem(img: Drawable, title: String, description: String) : Serializable {
    var img: Drawable = img
    var title: String = title
    var description: String = description
}