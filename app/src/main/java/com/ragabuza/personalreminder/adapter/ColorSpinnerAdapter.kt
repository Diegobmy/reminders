package com.ragabuza.personalreminder.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ragabuza.personalreminder.R
import java.lang.reflect.AccessibleObject.setAccessible
import android.widget.Spinner


/**
 * Created by diego.moyses on 2/7/2018.
 */

data class ThemeColor(val id: Int, val theme: Int, val darker: Int, val dark: Int, val normal: Int, val light: Int)

class ColorSpinnerAdapter(context: Context, private val groupId: Int, private val options: List<ThemeColor>) : ArrayAdapter<ThemeColor>(context, groupId, options) {

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = inflater.inflate(groupId, parent, false)
        val darker = itemView.findViewById<View>(R.id.vwColorDarker)
        val dark = itemView.findViewById<View>(R.id.vwColorDark)
        val normal = itemView.findViewById<View>(R.id.vwColorNormal)
        val light = itemView.findViewById<View>(R.id.vwColorLight)
//
        darker.setBackgroundColor(context.resources.getColor(options[position].darker))
        dark.setBackgroundColor(context.resources.getColor(options[position].dark))
        normal.setBackgroundColor(context.resources.getColor(options[position].normal))
        light.setBackgroundColor(context.resources.getColor(options[position].light))
        return itemView
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = inflater.inflate(groupId, parent, false)
        val darker = itemView.findViewById<View>(R.id.vwColorDarker)
        val dark = itemView.findViewById<View>(R.id.vwColorDark)
        val normal = itemView.findViewById<View>(R.id.vwColorNormal)
        val light = itemView.findViewById<View>(R.id.vwColorLight)

        darker.setBackgroundColor(context.resources.getColor(options[position].darker))
        dark.setBackgroundColor(context.resources.getColor(options[position].dark))
        normal.setBackgroundColor(context.resources.getColor(options[position].normal))
        light.setBackgroundColor(context.resources.getColor(options[position].light))


        return itemView
    }

}