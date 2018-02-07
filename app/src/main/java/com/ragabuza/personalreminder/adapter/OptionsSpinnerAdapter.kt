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

data class SpinnerItem(val text: String, val image: Int)

class OptionsSpinnerAdapter(context: Context, private val groupId: Int, private val options: List<SpinnerItem>, private val firstElement: Int) : ArrayAdapter<SpinnerItem>(context, groupId, options) {

    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var listener: SpinnerItemClick? = null
    private var mainSpinner: Spinner? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = inflater.inflate(firstElement, parent, false)

        return itemView
    }

    interface SpinnerItemClick {
        fun onSpinnerClick(item: Int)
    }

    fun setItemClick(spinner: Spinner, inter: SpinnerItemClick) {
        listener = inter
        mainSpinner = spinner
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView = inflater.inflate(groupId, parent, false)
        val imageView = itemView.findViewById<ImageView>(R.id.img_spinner)
        val textView = itemView.findViewById<TextView>(R.id.text_spinner)

        imageView.setImageResource(options[position].image)
        textView.text = options[position].text

        itemView.setOnClickListener {
            listener?.onSpinnerClick(position)
            hideDropDown()
        }

        return itemView
    }

    private fun hideDropDown() {
        if (mainSpinner != null) {
            val method = Spinner::class.java.getDeclaredMethod("onDetachedFromWindow")
            method.isAccessible = true
            method.invoke(mainSpinner)
        }
    }
}