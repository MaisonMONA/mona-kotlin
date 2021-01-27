package com.maison.mona.task

import android.os.AsyncTask
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import java.io.IOException

class ArtworksPageTask() : AsyncTask<String, Void, String>() {
    override fun doInBackground(vararg params: String?): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(params[0])
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