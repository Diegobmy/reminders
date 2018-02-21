package com.ragabuza.personalreminder.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import com.ragabuza.personalreminder.R
import java.util.*


/**
 * Created by diego.moyses on 1/2/2018.
 */
class TimeShortDialogAdapter(val context: Context, val listener: TimeShortCallback) {


    interface TimeShortCallback{
        fun onTimePick(cal: Calendar?)
    }

    fun show() {

        val dialog = Dialog(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.time_shotcurts, null)

        val bt15 = view.findViewById<Button>(R.id.btTime15M)
        val bt30 = view.findViewById<Button>(R.id.btTime30M)
        val bt1h = view.findViewById<Button>(R.id.btTime1H)
        val bt1d = view.findViewById<Button>(R.id.btTime1D)
        val btMorning = view.findViewById<ImageButton>(R.id.btTimeMorning)
        val btAfternoon = view.findViewById<ImageButton>(R.id.btTimeAfternoon)
        val btNight = view.findViewById<ImageButton>(R.id.btTimeNight)
        val btAnother = view.findViewById<Button>(R.id.btTimeAnother)

        val cal = Calendar.getInstance()
        cal.set(Calendar.SECOND, 0)

        bt15.setOnClickListener {
            cal.add(Calendar.MINUTE, 15)
            listener.onTimePick(cal)
            dialog.dismiss()
        }
        bt30.setOnClickListener {
            cal.add(Calendar.MINUTE, 30)
            listener.onTimePick(cal)
            dialog.dismiss()
        }
        bt1h.setOnClickListener {
            cal.add(Calendar.HOUR, 1)
            listener.onTimePick(cal)
            dialog.dismiss()
        }
        bt1d.setOnClickListener {
            cal.add(Calendar.DAY_OF_YEAR, 1)
            listener.onTimePick(cal)
            dialog.dismiss()
        }
        btMorning.setOnClickListener {
            cal.set(Calendar.HOUR_OF_DAY, 9)
            cal.set(Calendar.MINUTE, 0)
            listener.onTimePick(cal)
            dialog.dismiss()
        }
        btAfternoon.setOnClickListener {
            cal.set(Calendar.HOUR_OF_DAY, 15)
            cal.set(Calendar.MINUTE, 0)
            listener.onTimePick(cal)
            dialog.dismiss()
        }
        btNight.setOnClickListener {
            cal.set(Calendar.HOUR_OF_DAY, 21)
            cal.set(Calendar.MINUTE, 0)
            listener.onTimePick(cal)
            dialog.dismiss()
        }
        btAnother.setOnClickListener {
            listener.onTimePick(null)
            dialog.dismiss()
        }

        dialog.setContentView(view)

        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        dialog.show()


    }


}