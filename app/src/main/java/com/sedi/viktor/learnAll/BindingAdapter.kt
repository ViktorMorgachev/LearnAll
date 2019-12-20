package com.sedi.viktor.learnAll

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter


@BindingAdapter("android:src")
fun setImageDrawable(view: ImageView, @DrawableRes drawableId: Int) {
    if (drawableId != 0) {
        view.setImageResource(drawableId)
    }
}
