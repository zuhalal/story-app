package com.zuhal.storyapp.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImageCenterCrop(url: String?) {
    Glide.with(this.context)
        .load(url)
        .centerCrop()
        .into(this)
}