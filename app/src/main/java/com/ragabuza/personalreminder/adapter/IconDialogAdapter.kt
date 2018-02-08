package com.ragabuza.personalreminder.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.wifi.WifiManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.*
import com.ragabuza.personalreminder.R
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.view.View
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import com.ragabuza.personalreminder.model.Reminder
import java.util.*


/**
 * Created by diego.moyses on 1/2/2018.
 */
class IconDialogAdapter(val context: Context, private val listener: IconResult, private val tag: String?) {

    interface IconResult {
        fun onIconClick(iconTag: Int, icon: Int, iTag: String?)
    }

    fun show() {

        val dialog = Dialog(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.iconpicker_dialog, null)
        val btHome = view.findViewById<ImageButton>(R.id.ibIconHome)
        val btWork = view.findViewById<ImageButton>(R.id.ibIconWork)
        val btCar = view.findViewById<ImageButton>(R.id.ibIconCar)
        val btHeart = view.findViewById<ImageButton>(R.id.ibIconHeart)
        val btStar = view.findViewById<ImageButton>(R.id.ibIconStar)
        val btClose = view.findViewById<ImageButton>(R.id.ibClose)

        btClose.setOnClickListener {
            dialog.dismiss()
        }

        btHome.setOnClickListener {
            listener.onIconClick(R.drawable.ic_home_black, R.drawable.ic_home_white, tag)
            dialog.dismiss()
        }
        btWork.setOnClickListener {
            listener.onIconClick(R.drawable.ic_work_black, R.drawable.ic_work_white, tag)
            dialog.dismiss()
        }
        btCar.setOnClickListener {
            listener.onIconClick(R.drawable.ic_directions_car_black, R.drawable.ic_directions_car_white, tag)
            dialog.dismiss()
        }
        btHeart.setOnClickListener {
            listener.onIconClick(R.drawable.ic_favorite_black, R.drawable.ic_favorite_white, tag)
            dialog.dismiss()
        }
        btStar.setOnClickListener {
            listener.onIconClick(R.drawable.ic_star_black, R.drawable.ic_star_white, tag)
            dialog.dismiss()
        }



        dialog.setContentView(view)
        dialog.show()


    }


}