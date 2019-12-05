package com.sedi.viktor.learnAll.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import com.sedi.viktor.learnAll.R
import com.sedi.viktor.learnAll.custom.CustomColorPickerItem
import com.sedi.viktor.learnAll.ui.edit_word.listeners.ChangeColorListener
import kotlinx.android.synthetic.main.color_picker_dialog.view.*


class DialogColorChooser :
    AlertDialog.Builder, DialogInterface {


    private var changeColorListener: ChangeColorListener
    private val customColorPickerItems = ArrayList<CustomColorPickerItem>()

    companion object {
        var colors = listOf(
            R.color.black,
            R.color.blue,
            R.color.dark_green,
            R.color.orange,
            R.color.peace_color,
            R.color.light_green,
            R.color.pink,
            R.color.red,
            R.color.violet,
            R.color.white,
            R.color.yellow,
            R.color.color_200_1,
            R.color.color_200_2,
            R.color.color_200_4,
            R.color.color_200_5,
            R.color.color_200_6,
            R.color.color_200_7,
            R.color.color_200_8

        )
    }

    constructor(
        context: Context?,
        changeColorListener: ChangeColorListener
    ) : super(context) {

        this.changeColorListener = changeColorListener

        setView(getView(context, colors))

    }

    private lateinit var clickItemCallback: onClickItemCallback

    private fun getView(context: Context?, colors: List<Int>): View {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.color_picker_dialog, null)


        initListeners()


        val parser = view.resources.getXml(R.xml.item_selected_color)

        try {
            parser.next()
            parser.nextTag()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val attr = Xml.asAttributeSet(parser)
        val count = attr.attributeCount


        if (count < 0) return view


        for (i in colors.indices) {

            val customColorPickerItem = CustomColorPickerItem(context, attr)
            customColorPickerItem.setBackgroundIconColor(colors[i])

            // Вешаем слушателей
            customColorPickerItem.onClickListener(clickItemCallback, i)
            customColorPickerItems.add(customColorPickerItem)

            view.parent_root.addView(customColorPickerItem)
        }

        view.parent_root.invalidate()

        return view

    }

    private fun initListeners() {
        clickItemCallback = object : onClickItemCallback {
            override fun onClicked(visibility: Int, colorPosition: Int) {
                if (visibility == View.VISIBLE) {

                    changeColorListener.onColorChanged(colors[colorPosition])

                    for (i in 0 until customColorPickerItems.size) {
                        customColorPickerItems[i].setChecked(false)
                    }

                    customColorPickerItems[colorPosition].setChecked(true)

                }
            }

        }
    }

    override fun dismiss() {

    }

    override fun cancel() {

    }


    interface onClickItemCallback {
        fun onClicked(visibility: Int, colorPosition: Int)
    }

}