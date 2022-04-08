package com.example.mona

import android.os.AsyncTask
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class ArtworksTask() : AsyncTask<Void, Void, String>() {
    override fun doInBackground(vararg params: Void?): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://picasso.iro.umontreal.ca/~mona/api/artworks")
            .build()
        return try {
            val response = client.newCall(request).execute()
            response.body?.string()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}