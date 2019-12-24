package com.sedi.viktor.learnAll

import android.view.View
import android.widget.ImageView
import android.widget.TextView
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

@BindingAdapter("android:background")
fun setBackGroundColor(view: View, color: String) {
    var color = convertColorStringToInt(color, Color.GRAY.color)
    view.setBackgroundColor(view.context.getColor(color))
}

@BindingAdapter("android:textColor")
fun setTextColor(view: TextView, color: String) {
    var color = convertColorStringToInt(color, Color.WHITE.color)
    view.setTextColor(view.context.getColor(color))
}


