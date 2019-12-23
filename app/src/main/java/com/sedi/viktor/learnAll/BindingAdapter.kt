package com.sedi.viktor.learnAll

import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter


@BindingAdapter("android:src")
fun setImageDrawable(view: ImageView, @DrawableRes drawableId: Int) {
    if (drawableId != 0) {
        view.setImageResource(drawableId)
    }
}
@BindingAdapter("android:background")
fun setBackground(view: View, @ColorRes colorID: Int) {
    view.setBackgroundColor(view.context.getColor(colorID))
}
