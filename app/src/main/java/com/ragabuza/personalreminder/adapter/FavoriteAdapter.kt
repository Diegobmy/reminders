package com.ragabuza.personalreminder.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ragabuza.personalreminder.R
import com.ragabuza.personalreminder.model.Favorite
import com.ragabuza.personalreminder.util.ReminderTranslation

/**
 * Created by diego.moyses on 2/8/2018.
 */
class FavoriteAdapter(val context: Context, private val favorites: List<Favorite>) : BaseAdapter() {

    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    var listener: favoriteClickListener? = null

    interface favoriteClickListener {
        fun delete(favorite: Favorite)
        fun edit(favorite: Favorite)
    }

    fun setOnFavoriteClickListener(element: favoriteClickListener) {
        listener = element
    }


    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        val favorite = favorites[p0]

        val trans = ReminderTranslation(context)

        val itemView = inflater.inflate(R.layout.favorite_item, p2, false)
        val image = itemView.findViewById<ImageView>(R.id.image_view_favorite)
        val text = itemView.findViewById<TextView>(R.id.text_view_favorite)
        val delete = itemView.findViewById<ImageView>(R.id.image_view_favorite_delete)

        text.text = "${trans.reminderType(favorite.type)}:\n${
        if (!favorite.location.isNullOrEmpty())
            favorite.location
        else
            favorite.condition
        }"
        image.setImageResource(favorite.tag)

        delete.setOnClickListener {
            listener?.delete(favorite)
        }
        itemView.setOnClickListener {
            listener?.edit(favorite)
        }


        return itemView
    }

    override fun getItem(p0: Int): Any {
        return favorites[p0]
    }

    override fun getItemId(p0: Int): Long {
        return favorites[p0].id
    }

    override fun getCount(): Int {
        return favorites.size
    }
}