package com.ragabuza.personalreminder.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.ragabuza.personalreminder.util.ReminderTranslation
import com.ragabuza.personalreminder.util.Shared
import android.view.ViewGroup
import android.view.View.MeasureSpec
import android.opengl.ETC1.getWidth
import android.view.View
import android.widget.ListView
import com.ragabuza.personalreminder.R


/**
 * Created by diego.moyses on 2/6/2018.
 */
@SuppressLint("Registered")
open class ActivityBase : AppCompatActivity(){
    lateinit var trans: ReminderTranslation
    lateinit var shared: Shared

    override fun onCreate(savedInstanceState: Bundle?) {
        shared = Shared(this)
        trans = ReminderTranslation(this)
        applyTheme()
        super.onCreate(savedInstanceState)
    }

    open fun applyTheme(){
        theme.applyStyle(shared.getTheme().theme, true)
    }

    fun ListView.reajustListView() {
        val listAdapter = this.adapter ?: return

        val desiredWidth = MeasureSpec.makeMeasureSpec(this.width, MeasureSpec.UNSPECIFIED)
        var totalHeight = 0
        var view: View? = null
        for (i in 0 until listAdapter.getCount()) {
            view = listAdapter.getView(i, view, this)
            if (i == 0)
                view!!.layoutParams = ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT)

            view!!.measure(desiredWidth, MeasureSpec.UNSPECIFIED)
            totalHeight += view.measuredHeight
        }
        val params = this.layoutParams
        params.height = totalHeight + this.dividerHeight * (listAdapter.count - 1)
        this.layoutParams = params
    }

}