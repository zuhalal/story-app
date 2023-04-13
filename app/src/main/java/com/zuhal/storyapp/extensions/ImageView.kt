package com.example.githubusers.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

fun ImageView.loadImage(url: String?, width: Int? = 55, height: Int? = 55) {
    Glide.with(this.context)
        .load(url)
        .apply(RequestOptions().override(width ?: 55, height ?: 55))
        .into(this)
}

fun ImageView.loadImageCenterCrop(url: String?) {
    Glide.with(this.context)
        .load(url)
        .centerCrop()
        .into(this)
}