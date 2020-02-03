package com.example.mona

import android.os.AsyncTask
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import java.io.IOException

class ApiArtworks() : AsyncTask<Void, Void, String>() {
    override fun doInBackground(vararg params: Void?): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://picasso.iro.umontreal.ca/~mona/api/artworks")
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