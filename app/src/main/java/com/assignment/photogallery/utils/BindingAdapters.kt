package com.assignment.photogallery.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.assignment.photogallery.R
import com.bumptech.glide.Glide


/**
 * to load image url with Glide library
 */
@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, url: String?) {
    if (!url.isNullOrEmpty()) {
        var imgUrl = url
        Glide.with(view.context)
            .load(imgUrl)
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_broken_image)
            .into(view)
    }
}


