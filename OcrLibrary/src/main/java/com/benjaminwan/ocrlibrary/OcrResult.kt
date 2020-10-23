package com.benjaminwan.ocrlibrary

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OcrResult(
    val textBoxes: ArrayList<TextBox>,
    var textBoxTime: Double,
    val angles: ArrayList<Angle>,
    val textLines: ArrayList<TextLine>,
    var boxImg: Bitmap,
    var fullTime: Double,
    var strRes: String
) : Parcelable

@Parcelize
data class Point(var x: Int, var y: Int) : Parcelable

@Parcelize
data class TextBox(val box: ArrayList<Point>, var score: Float) : Parcelable

@Parcelize
data class Angle(var index: Int, var scroe: Float, var time: Double) : Parcelable

@Parcelize
data class TextLine(var line: String, val scores: FloatArray, var time: Double) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextLine

        if (line != other.line) return false
        if (!scores.contentEquals(other.scores)) return false
        if (time != other.time) return false

        return true
    }

    override fun hashCode(): Int {
        var result = line.hashCode()
        result = 31 * result + scores.contentHashCode()
        result = 31 * result + time.hashCode()
        return result
    }
}
