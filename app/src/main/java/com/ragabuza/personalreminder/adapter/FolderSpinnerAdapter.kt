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

class FolderSpinnerAdapter(context: Context, val groupId: Int, val options: MutableList<String>, val spinner: Spinner, val listener: FolderSpinnerCallback) : ArrayAdapter<String>(context, groupId, options) {

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val image = ImageView(context)
        image.setImageResource(R.drawable.ic_folder)

        return image
    }

    interface FolderSpinnerCallback{
        fun onClick(folder: String)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = inflater.inflate(groupId, parent, false)
        val text = itemView.findViewById<TextView>(R.id.text_view_folder)

        text.text = options[position]

        itemView.setOnClickListener {
            listener.onClick(options[position])
            hideDropDown()
        }

        return itemView
    }


    private fun hideDropDown() {
        val method = Spinner::class.java.getDeclaredMethod("onDetachedFromWindow")
        method.isAccessible = true
        method.invoke(spinner)
    }
}