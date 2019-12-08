package com.sedi.viktor.learnAll.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.sedi.viktor.learnAll.Color
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.convertColorIntToColor
import com.sedi.viktor.learnAll.extensions.invisible
import com.sedi.viktor.learnAll.extensions.visible
import com.sedi.viktor.learnAll.ui.dialogs.DialogColorChooser
import kotlinx.android.synthetic.main.item_selected_color.view.*


class CustomColorPickerItem(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    var color: Int

    init {
        LayoutInflater.from(context).inflate(R.layout.item_selected_color, this, true)
        color = resources.getColor(Color.DEFAULT.color, context.theme)
    }


    fun onClickListener(
        onClickItemCallback: DialogColorChooser.onClickItemCallback,
        position: Int
    ) {

        parent_root.setOnClickListener {

            if (iv_cheched.visibility == View.VISIBLE)
                iv_cheched.invisible()
            else iv_cheched.visible()

            Log.d("LearnAll", "Clicked ${convertColorIntToColor(color)}")

            onClickItemCallback.onClicked(
                iv_cheched.visibility,
                color,
                position
            )
        }


    }

    fun setChecked(isChecked: Boolean) {
        if (isChecked)
            iv_cheched.visible()
        else iv_cheched.invisible()
    }


    private fun getBackGroundColor(drawable: Drawable): Color {
        val gradientDrawable = drawable as GradientDrawable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return convertColorIntToColor(gradientDrawable.color!!.defaultColor)
        }
        return Color.DEFAULT
    }

    fun setBackgroundIconColor(color: Int) {
        val drawable = resources.getDrawable(R.drawable.ic_color_item, context.theme)
        DrawableCompat.setTint(iv_color_circle.drawable, ContextCompat.getColor(context, color))
    }

}