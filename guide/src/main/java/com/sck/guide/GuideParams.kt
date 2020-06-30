package com.sck.guide

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import java.io.ByteArrayOutputStream
import java.io.Serializable

class GuideParams(
    @Transient
    val highlightView:View,
    val guideLayoutResId:Int,
    val showTag:String,
    val radius:Float=0F,
    val padding:IntArray= IntArray(4),
    val gravity:Int=Gravity.BOTTOM
):Serializable{

    val topOffset:Int
    val leftOffset:Int
    val highlightImage:ByteArray

    init {
        var viewBitmap= view2Bitmap(highlightView)
        var byteArrayOutputStream= ByteArrayOutputStream()
        viewBitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream)
        var highlightImage=byteArrayOutputStream.toByteArray()
        byteArrayOutputStream.close()
        var location=IntArray(2)
        highlightView.getLocationOnScreen(location)
        this.topOffset=location[1]
        this.leftOffset=location[0]
        this.highlightImage=highlightImage
    }
}


/**
 * 可见view生成bitmap
 * @param view
 * @return
 */
private fun view2Bitmap(view: View): Bitmap {
    var view = view
    if (view is ScrollView) {
        view = view.getChildAt(0)
    } else if (view is NestedScrollView) {
        view = view.getChildAt(0)
    }
    val bmp =
        Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    canvas.drawColor(if(view.background==null) Color.WHITE else Color.TRANSPARENT)
    view.draw(canvas)
    return bmp
}


