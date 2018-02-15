package com.ragabuza.personalreminder.adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.*
import com.ragabuza.personalreminder.R
import android.view.View
import com.ragabuza.personalreminder.model.Favorite
import android.widget.Toast
import com.ebanx.swipebtn.OnStateChangeListener
import com.ebanx.swipebtn.SwipeButton


/**
 * Created by diego.moyses on 1/2/2018.
 */
class ConfirmAdapter(val context: Context, private val listener: ConfirmResult) {

    interface ConfirmResult {
        fun onConfirm()
    }


    fun show() {

        val dialog = Dialog(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.confirm_dialog, null)


        val enableButton = view.findViewById<SwipeButton>(R.id.swipe_btn)
        enableButton.setOnStateChangeListener { status ->
            if (!status){
                listener.onConfirm()
                dialog.dismiss()
            }
        }

        dialog.setContentView(view)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()


    }


}