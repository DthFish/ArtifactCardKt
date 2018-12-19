package com.dthfish.artifact.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

/**
 * Description
 * Author DthFish
 * Date  2018/12/18.
 */
object ImageLoader {

    fun loadUrl(context: Context, url: String?, iv: ImageView) {
        Glide.with(context).load(url).into(iv)
    }

}