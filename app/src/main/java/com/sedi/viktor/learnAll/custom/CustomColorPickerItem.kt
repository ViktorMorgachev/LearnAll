package com.sedi.viktor.learnAll.custom

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.extensions.invisible
import com.sedi.viktor.learnAll.extensions.visible
import com.sedi.viktor.learnAll.ui.dialogs.DialogColorChooser
import kotlinx.android.synthetic.main.item_selected_color.view.*


class CustomColorPickerItem(context: Context?, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {


    init {
        LayoutInflater.from(context).inflate(R.layout.item_selected_color, this, true)
        parent_root.setOnClickListener { onClickListener() }
    }


    fun onClickListener(action: (() -> Unit) = {}): Int {
        if (iv_cheched.visibility == View.VISIBLE)
            iv_cheched.invisible()
        else iv_cheched.visible()

        return iv_cheched.visibility
    }

    fun onClickListener(
        onClickItemCallback: DialogColorChooser.onClickItemCallback,
        position: Int
    ) {
        if (iv_cheched.visibility == View.VISIBLE)
            iv_cheched.invisible()
        else iv_cheched.visible()

        onClickItemCallback.onClicked(iv_cheched.visibility, position)

    }


    fun setBackgroundIconColor(color: Int) {
        val shape = ResourcesCompat.getDrawable(resources, R.drawable.ic_color_item, context.theme)
        val gradientDrawable = shape as GradientDrawable
        gradientDrawable.setColor(ContextCompat.getColor(context, color))
    }

}