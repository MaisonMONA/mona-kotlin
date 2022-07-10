package com.maison.mona.task

import android.os.AsyncTask
import okhttp3.*
import java.io.IOException

class PlacesTask(time: String) : AsyncTask<Void, Void, String>() {

    private val time: String?

    init {
        this.time = time

    }

    override fun doInBackground(vararg params: Void?): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
                //momo
            .url("https://picasso.iro.umontreal.ca/~mona/api/places?paginate=100&page=1&date"+time)


            //.url("https://picasso.iro.umontreal.ca/~mona/api/lastUpdatedPlaces?date="+this.time)
            //.url("https://picasso.iro.umontreal.ca/~mona/api/places?page="+time)
            .build()
        return try {
            val response = client.newCall(request).execute()
            response.body!!.string()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}