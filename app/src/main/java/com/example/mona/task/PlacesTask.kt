package com.example.mona.task

import android.os.AsyncTask
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import java.io.IOException

class PlacesTask() : AsyncTask<Void, Void, String>() {
    override fun doInBackground(vararg params: Void?): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://picasso.iro.umontreal.ca/~mona/api/places")
            .build()
        return try {
            val response = client.newCall(request).execute()
            response.body().string()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}