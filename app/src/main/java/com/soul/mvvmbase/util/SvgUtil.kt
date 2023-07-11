package com.soul.mvvmbase.util

import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.widget.ImageView
import com.caverock.androidsvg.SVG
import java.io.IOException
import java.io.InputStream

object SvgUtil {
    fun updateIcon(context: Context, img:String,imageView :ImageView){
        try {
            // 从Assets目录下读取SVG文件
            val inputStream: InputStream = context.assets.open("${img}.svg")
            // 使用AndroidSVG库解析SVG文件
            val svg = SVG.getFromInputStream(inputStream)
            // 创建一个PictureDrawable对象
            val pictureDrawable = PictureDrawable(svg.renderToPicture())
            // 将PictureDrawable对象设置给ImageView
            imageView.setImageDrawable(pictureDrawable)
        } catch (e: IOException) {
            e.printStackTrace()
        }


    }
}