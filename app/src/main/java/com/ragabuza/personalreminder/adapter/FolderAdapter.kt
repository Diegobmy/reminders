package com.ragabuza.personalreminder.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.model.Favorite
import com.ragabuza.personalreminder.util.Constants.Other.Companion.EMPTY_FOLDER
import com.ragabuza.personalreminder.util.ReminderTranslation

/**
 * Created by diego.moyses on 2/8/2018.
 */
class FolderAdapter(val context: Context, private val folders: HashSet<String>, private val listener: FolderClickListener) : BaseAdapter() {

    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    interface FolderClickListener {
        fun delete(folder: String)
        fun edit(folder: String)
    }


    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        val folder = folders.elementAt(p0)

        val itemView = inflater.inflate(R.layout.folder_item, p2, false)

        val text = itemView.findViewById<TextView>(R.id.text_view_folder)
        val delete = itemView.findViewById<ImageView>(R.id.image_view_folder_delete)

        text.text = folder

        delete.setOnClickListener {
            listener.delete(folder)
        }
        itemView.setOnClickListener {
            listener.edit(folder)
        }
        return itemView

    }

    override fun getItem(p0: Int): Any {
        return folders.elementAt(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return folders.size
    }
}