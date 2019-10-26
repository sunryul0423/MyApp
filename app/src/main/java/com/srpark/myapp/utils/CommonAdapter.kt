package com.srpark.myapp.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.srpark.myapp.R

/**
 * Glide 이미지 세팅
 * @param view xml의 ImageView
 * @param url 이미지 Url
 */
@BindingAdapter("imgUrl")
fun setImageUrl(view: ImageView, url: String?) {
    url?.let {
        GlideApp.with(view.context.applicationContext)
            .load(it)
            .override(view.width, view.height)
            .placeholder(R.color.gray)
            .fitCenter()
            .error(R.color.gray)
            .into(view)
    }
}

fun setCircleImageUrl(view: ImageView, url: String?) {
    url?.let {
        GlideApp.with(view.context.applicationContext)
            .load(it)
            .override(view.width, view.height)
            .error(R.drawable.img_user_placeholder)
            .circleCrop()
            .into(view)
    }
}