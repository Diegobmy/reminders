package com.ragabuza.personalreminder.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView


/**
 * Created by diego.moyses on 2/1/2018.
 */
class DownloadImage(internal var bmImage: ImageView) : AsyncTask<String, Void, Bitmap>() {

    override fun doInBackground(vararg urls: String): Bitmap? {
        val urldisplay = urls[0]
        var mIcon11: Bitmap? = null
        try {
            val `in` = java.net.URL(urldisplay).openStream()
            mIcon11 = BitmapFactory.decodeStream(`in`)
        } catch (e: Exception) {
            Log.e("Error", e.message)
            e.printStackTrace()
        }

        return mIcon11
    }

    override fun onPostExecute(result: Bitmap) {
        bmImage.setImageBitmap(result)
    }
}