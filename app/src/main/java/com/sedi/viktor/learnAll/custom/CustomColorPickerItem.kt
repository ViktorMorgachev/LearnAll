package com.sedi.viktor.learnAll.custom

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.sedi.viktor.learnAll.Color
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.convertColorIntToColor
import com.sedi.viktor.learnAll.extensions.invisible
import com.sedi.viktor.learnAll.extensions.visible
import com.sedi.viktor.learnAll.ui.dialogs.DialogColorChooser
import kotlinx.android.synthetic.main.item_selected_color.view.*


class CustomColorPickerItem(context: Context?, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {


    init {
        LayoutInflater.from(context).inflate(R.layout.item_selected_color, this, true)
    }


    fun onClickListener(
        onClickItemCallback: DialogColorChooser.onClickItemCallback,
        position: Int
    ) {

        parent_root.setOnClickListener {

            if (iv_cheched.visibility == View.VISIBLE)
                iv_cheched.invisible()
            else iv_cheched.visible()

            onClickItemCallback.onClicked(
                iv_cheched.visibility,
                getBackGroundColor(iv_color_circle.drawable),
                position
            )
        }


    }

    fun setChecked(isChecked: Boolean) {
        if (isChecked)
            iv_cheched.visible()
        else iv_cheched.invisible()
    }


    fun setBackgroundIconColor(color: Int) {
        val shape = ResourcesCompat.getDrawable(resources, R.drawable.ic_color_item, context.theme)
        val gradientDrawable = shape as GradientDrawable
        gradientDrawable.setColor(ContextCompat.getColor(context, color))
    }

    private fun getBackGroundColor(drawable: Drawable): Color {
        val gradientDrawable = drawable as GradientDrawable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return convertColorIntToColor(gradientDrawable.color!!.defaultColor)
        }
        return Color.DEFAULT
    }

}