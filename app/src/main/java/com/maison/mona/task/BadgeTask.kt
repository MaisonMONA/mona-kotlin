package com.maison.mona.task

import android.os.AsyncTask
import okhttp3.*
import java.io.IOException

class BadgeTask(time: String) : AsyncTask<Void, Void, String>(){

    private val time: String?

    init {
        this.time= time
    }

    //https://picasso.iro.umontreal.ca/~mona/api/badges

    override fun doInBackground(vararg params: Void?): String? {
        val client = OkHttpClient()
        val request = Request.Builder()

            .url("https://picasso.iro.umontreal.ca/~mona/api/badges")
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.body?.string()
            //json.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}