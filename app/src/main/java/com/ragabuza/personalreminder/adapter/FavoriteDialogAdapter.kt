package com.ragabuza.personalreminder.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.*
import com.ragabuza.personalreminder.R
import android.view.View
import com.ragabuza.personalreminder.model.Favorite


/**
 * Created by diego.moyses on 1/2/2018.
 */
class FavoriteDialogAdapter(val context: Context, private val favorites: List<Favorite>, val listener: FavoriteSelectCallback) {


    interface FavoriteSelectCallback{
        fun favoriteCall(favorite: Favorite)
    }

    fun show() {

        val dialog = Dialog(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.list_item_dialog, null)

        val btClose = view.findViewById<ImageButton>(R.id.ibClose)
        val lvFavorites = view.findViewById<ListView>(R.id.lv)

        view.findViewById<TextView>(R.id.tvTitle).text = context.getString(R.string.favorites)

        val adapter = FavoriteAdapter(context, favorites, true)
        adapter.setOnFavoriteClickListener(object : FavoriteAdapter.favoriteClickListener{
            override fun delete(favorite: Favorite) {}
            override fun edit(favorite: Favorite) {
                listener.favoriteCall(favorite)
                dialog.dismiss()
            }

        })

        lvFavorites.adapter = adapter

        btClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()


    }


}