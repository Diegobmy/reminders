package com.ragabuza.personalreminder.adapter

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.view.LayoutInflater
import android.widget.*
import com.ragabuza.personalreminder.R
import android.view.View
import android.util.DisplayMetrics


/**
 * Created by diego.moyses on 1/2/2018.
 */
class InformationAdapter(
        val context: Activity,
        val text: String
) {

    companion object {
        val STARTFROMTOP = 0
        val STARTFROMBOT = 1

        val TOP = 1
        val BOT = 2
        val LEFT = 3
        val RIGHT = 4
        val CENTER = 5
        val BEFORE = 6
        val AFTER = 7
    }

    private var dismissListener: (() -> Unit)? = null
    fun setDismissListener(body: () -> Unit): InformationAdapter {
        dismissListener = body
        return this
    }

    private var skipListener: (() -> Unit)? = null
    fun setSkipListener(body: () -> Unit): InformationAdapter {
        skipListener = body
        return this
    }


    private var focusView: View? = null
    fun setfocusView(body: View?): InformationAdapter {
        focusView = body
        return this
    }

    private var expanded: Int? = null
    fun expandView(expand: Int?): InformationAdapter {
        expanded = expand
        return this
    }

    private var coordinates: List<Int>? = null
    fun setCoordinates(body: List<Int>): InformationAdapter {
        coordinates = body
        return this
    }

    private var next: InformationAdapter? = null
    fun setNext(body: InformationAdapter): InformationAdapter {
        next = body
        return this
    }

    private var requireMark: Boolean = false
    fun setRequireMark(): InformationAdapter {
        requireMark = true
        return this
    }

    private var skipHorizontal: Int = 0
    private var skipVertical: Int = 0
    fun setSkip(horizontal: Int, vertical: Int): InformationAdapter {
        skipHorizontal = horizontal
        skipVertical = vertical
        return this
    }

    private var textHorizontal: Int = 0
    private var textVertical: Int = 0
    fun setTextPosition(horizontal: Int, vertical: Int): InformationAdapter {
        textHorizontal = horizontal
        textVertical = vertical
        return this
    }

    private var disableBack: Boolean = true
    fun setDisableBack(status: Boolean): InformationAdapter {
        disableBack = status
        return this
    }

    private var delayTime: Long = 1
    fun setdelayTime(time: Long): InformationAdapter {
        delayTime = time
        return this
    }

    fun show() {
        var willSkip = false
        val dialog = Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.information_dialog, null)

        if (disableBack)
            dialog.setOnKeyListener { _, _, _ -> true }


        var markPositionAfter = 0
        var markPositionBefore = 0

        if (focusView != null || coordinates != null) {
            val displayMetrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val Parentheight = displayMetrics.heightPixels
            val ParentWidth = displayMetrics.widthPixels

            var rect = Rect()
            if (focusView != null) {
                focusView?.getGlobalVisibleRect(rect)
            } else if (coordinates != null) {
                rect = if (coordinates!![0] == STARTFROMTOP)
                    Rect(coordinates!![1], coordinates!![2], coordinates!![3], coordinates!![4])
                else
                    Rect(ParentWidth - coordinates!![3], Parentheight - coordinates!![4], ParentWidth - coordinates!![1], Parentheight - coordinates!![2])
            }

            if (expanded != null){
                rect = Rect(rect.left - expanded!!, rect.top - expanded!!, rect.right + expanded!!, rect.bottom + expanded!!)
            }

            val start = view.findViewById<ImageView>(R.id.ivImportantStart)
            val end = view.findViewById<ImageView>(R.id.ivImportantEnd)
            val top = view.findViewById<ImageView>(R.id.ivImportantTop)
            val bot = view.findViewById<ImageView>(R.id.ivImportantBot)

            start.visibility = View.VISIBLE
            end.visibility = View.VISIBLE
            top.visibility = View.VISIBLE
            bot.visibility = View.VISIBLE

            val layoutParamsStart = RelativeLayout.LayoutParams(rect.left, rect.height())
            val layoutParamsEnd = RelativeLayout.LayoutParams(ParentWidth - (rect.left + rect.width()), rect.height())
            val layoutParamsTop = RelativeLayout.LayoutParams(ParentWidth, rect.top)
            val layoutParamsBot = RelativeLayout.LayoutParams(ParentWidth, Parentheight - (rect.top + rect.height()))

            layoutParamsStart.setMargins(0, rect.top, 0, 0)
            layoutParamsEnd.setMargins(rect.right, rect.top, 0, 0)
            layoutParamsTop.setMargins(0, 0, 0, 0)
            layoutParamsBot.setMargins(0, rect.bottom, 0, 0)


            start.layoutParams = layoutParamsStart
            end.layoutParams = layoutParamsEnd
            top.layoutParams = layoutParamsTop
            bot.layoutParams = layoutParamsBot

            val mark = view.findViewById<View>(R.id.ivImportantBox)
            mark.visibility = View.VISIBLE
            mark.background = context.resources.getDrawable(if (requireMark)
                R.drawable.information_mark_required
            else
                R.drawable.information_mark
            )

            val layoutParamsMark = RelativeLayout.LayoutParams(rect.width(), rect.height())
            layoutParamsMark.setMargins(rect.left, rect.top, 0, 0)
            mark.layoutParams = layoutParamsMark

            markPositionBefore = Parentheight - rect.top + 16
            markPositionAfter = rect.bottom + 16

            delay(Runnable {
                mark.setOnClickListener {
                    beforeDismiss(willSkip)
                    delayedDismiss(Runnable {dialog.dismiss()})
                }
            })

        } else {

            val displayMetrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val parentHeight = displayMetrics.heightPixels
            val parentWidth = displayMetrics.widthPixels

            val start = view.findViewById<ImageView>(R.id.ivImportantStart)
            start.visibility = View.VISIBLE
            val layoutParamsStart = RelativeLayout.LayoutParams(parentWidth, parentHeight)
            start.layoutParams = layoutParamsStart
        }


        val information = view.findViewById<TextView>(R.id.tvInfo)
        val infoParams = information.layoutParams as RelativeLayout.LayoutParams
        when (textVertical) {
            TOP -> infoParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
            CENTER, 0 -> infoParams.addRule(RelativeLayout.CENTER_VERTICAL)
            BOT -> infoParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            BEFORE -> {
                infoParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                infoParams.bottomMargin = markPositionBefore
            }
            AFTER -> {
                infoParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                infoParams.topMargin = markPositionAfter
            }
        }
        when (textHorizontal) {
            LEFT -> infoParams.addRule(RelativeLayout.ALIGN_PARENT_START)
            CENTER, 0 -> infoParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            RIGHT -> infoParams.addRule(RelativeLayout.ALIGN_PARENT_END)
        }
        information.text = text


        if (skipHorizontal != 0) {
            val skipButton = view.findViewById<TextView>(R.id.btSkip)
            skipButton.visibility = View.VISIBLE

            val skipParams = skipButton.layoutParams as RelativeLayout.LayoutParams

            when (skipVertical) {
                TOP -> skipParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                CENTER -> skipParams.addRule(RelativeLayout.CENTER_VERTICAL)
                BOT -> skipParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            }
            when (skipHorizontal) {
                LEFT -> skipParams.addRule(RelativeLayout.ALIGN_PARENT_START)
                CENTER -> skipParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
                RIGHT -> skipParams.addRule(RelativeLayout.ALIGN_PARENT_END)
            }
            delay(Runnable {
                skipButton.setOnClickListener {
                    willSkip = true
                    beforeDismiss(willSkip)
                    delayedDismiss(Runnable {dialog.dismiss()})
                }
            })
        }

        if (!requireMark)
            delay(Runnable {
                view.setOnClickListener {
                    beforeDismiss(willSkip)
                    delayedDismiss(Runnable {dialog.dismiss()})
                }
            })

        dialog.setContentView(view)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()


    }

    private fun beforeDismiss(willSkip: Boolean) {
        if (!willSkip) {
            dismissListener?.invoke()
            if (next?.skipHorizontal == 0) {
                next?.setSkip(skipHorizontal, skipVertical)
            }
            if (next?.textHorizontal == 0) {
                next?.setTextPosition(textHorizontal, textVertical)
            }
            next?.skipListener = skipListener
            next?.show()
        } else {
            skipListener?.invoke()
        }
    }

    private fun delay(r: Runnable) {
        Handler().postDelayed(r, delayTime)
    }
    private fun delayedDismiss(r: Runnable) {
        Handler().postDelayed(r, 100)
    }

}
