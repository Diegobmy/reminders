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
class IconDialogAdapter(val context: Context, private val listener: IconResult, private val tag: String?, val hideList: List<Favorite>) {

    interface IconResult {
        fun onIconClick(iconTag: Int, icon: Int, iTag: String?)
    }

    fun show() {

        val dialog = Dialog(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.iconpicker_dialog, null)

        val btClose = view.findViewById<ImageButton>(R.id.ibClose)

        val btHome = view.findViewById<ImageButton>(R.id.ibIconHome)
        val btWork = view.findViewById<ImageButton>(R.id.ibIconWork)
        val btCar = view.findViewById<ImageButton>(R.id.ibIconCar)
        val btHeart = view.findViewById<ImageButton>(R.id.ibIconHeart)
        val btStar = view.findViewById<ImageButton>(R.id.ibIconStar)

        val btPhone = view.findViewById<ImageButton>(R.id.ibIconPhone)
        val btMoney = view.findViewById<ImageButton>(R.id.ibIconMoney)
        val btRestaurant = view.findViewById<ImageButton>(R.id.ibIconRestaurant)
        val btFlight = view.findViewById<ImageButton>(R.id.ibIconFlight)
        val btStore = view.findViewById<ImageButton>(R.id.ibIconStore)

        val btTrain = view.findViewById<ImageButton>(R.id.ibIconTrain)
        val btAudio = view.findViewById<ImageButton>(R.id.ibIconAudio)
        val btHeadset = view.findViewById<ImageButton>(R.id.ibIconHeadset)
        val btMusic = view.findViewById<ImageButton>(R.id.ibIconMusic)
        val btCamera = view.findViewById<ImageButton>(R.id.ibIconCamera)

        val btWifi = view.findViewById<ImageButton>(R.id.ibIconWifi)
        val btBluetooth = view.findViewById<ImageButton>(R.id.ibIconBluetooth)
        val btLocation = view.findViewById<ImageButton>(R.id.ibIconLocation)
        val btComputer = view.findViewById<ImageButton>(R.id.ibIconComputer)
        val btApp = view.findViewById<ImageButton>(R.id.ibIconApp)

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
        btPhone.setOnClickListener {
            listener.onIconClick(R.drawable.ic_phone_android_black, R.drawable.ic_phone_android_white, tag)
            dialog.dismiss()
        }
        btMoney.setOnClickListener {
            listener.onIconClick(R.drawable.ic_attach_money_black, R.drawable.ic_attach_money_white, tag)
            dialog.dismiss()
        }
        btRestaurant.setOnClickListener {
            listener.onIconClick(R.drawable.ic_restaurant_black, R.drawable.ic_restaurant_white, tag)
            dialog.dismiss()
        }
        btFlight.setOnClickListener {
            listener.onIconClick(R.drawable.ic_flight_black, R.drawable.ic_flight_white, tag)
            dialog.dismiss()
        }
        btStore.setOnClickListener {
            listener.onIconClick(R.drawable.ic_store_black, R.drawable.ic_store_white, tag)
            dialog.dismiss()
        }
        btTrain.setOnClickListener {
            listener.onIconClick(R.drawable.ic_train_black, R.drawable.ic_train_white, tag)
            dialog.dismiss()
        }
        btAudio.setOnClickListener {
            listener.onIconClick(R.drawable.ic_surround_sound_black, R.drawable.ic_surround_sound_white, tag)
            dialog.dismiss()
        }
        btHeadset.setOnClickListener {
            listener.onIconClick(R.drawable.ic_headset_black, R.drawable.ic_headset_white, tag)
            dialog.dismiss()
        }
        btMusic.setOnClickListener {
            listener.onIconClick(R.drawable.ic_music_note_black, R.drawable.ic_music_note_white, tag)
            dialog.dismiss()
        }
        btCamera.setOnClickListener {
            listener.onIconClick(R.drawable.ic_camera_alt_black, R.drawable.ic_camera_alt_white, tag)
            dialog.dismiss()
        }
        btWifi.setOnClickListener {
            listener.onIconClick(R.drawable.ic_wifi_black, R.drawable.ic_wifi_white, tag)
            dialog.dismiss()
        }
        btBluetooth.setOnClickListener {
            listener.onIconClick(R.drawable.ic_bluetooth_black, R.drawable.ic_bluetooth_white, tag)
            dialog.dismiss()
        }
        btLocation.setOnClickListener {
            listener.onIconClick(R.drawable.ic_location_black, R.drawable.ic_location_white, tag)
            dialog.dismiss()
        }
        btComputer.setOnClickListener {
            listener.onIconClick(R.drawable.ic_computer_black, R.drawable.ic_computer_white, tag)
            dialog.dismiss()
        }
        btApp.setOnClickListener {
            listener.onIconClick(R.drawable.ic_simple_selected, R.drawable.ic_simple_selected, tag)
            dialog.dismiss()
        }

        hideList.forEach {
            when (it.tag) {
                R.drawable.ic_home_black -> btHome.visibility = View.GONE
                R.drawable.ic_work_black -> btWork.visibility = View.GONE
                R.drawable.ic_directions_car_black -> btCar.visibility = View.GONE
                R.drawable.ic_favorite_black -> btHeart.visibility = View.GONE
                R.drawable.ic_star_black -> btStar.visibility = View.GONE
                R.drawable.ic_phone_android_black -> btPhone.visibility = View.GONE
                R.drawable.ic_attach_money_black -> btMoney.visibility = View.GONE
                R.drawable.ic_restaurant_black -> btRestaurant.visibility = View.GONE
                R.drawable.ic_flight_black -> btFlight.visibility = View.GONE
                R.drawable.ic_store_black -> btStore.visibility = View.GONE
                R.drawable.ic_train_black -> btTrain.visibility = View.GONE
                R.drawable.ic_surround_sound_black -> btAudio.visibility = View.GONE
                R.drawable.ic_headset_black -> btHeadset.visibility = View.GONE
                R.drawable.ic_music_note_black -> btMusic.visibility = View.GONE
                R.drawable.ic_camera_alt_black -> btCamera.visibility = View.GONE
                R.drawable.ic_wifi_black -> btWifi.visibility = View.GONE
                R.drawable.ic_bluetooth_black -> btBluetooth.visibility = View.GONE
                R.drawable.ic_location_black -> btLocation.visibility = View.GONE
                R.drawable.ic_computer_black -> btComputer.visibility = View.GONE
                R.drawable.ic_simple_selected -> btApp.visibility = View.GONE
            }
        }

        dialog.setContentView(view)
        dialog.show()


    }


}