package com.irempamukcu.picturestosketch

import android.graphics.Bitmap

var picList = mutableListOf<Pic>()

val PIC_EXTRA_ID = "piceExtra"

class Pic (
    var picture: Bitmap,
    var title : String,
    var description : String,
    val id : Int
)