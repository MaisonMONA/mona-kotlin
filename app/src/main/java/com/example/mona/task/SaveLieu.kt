package com.example.mona.task

import android.os.AsyncTask
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

//Store the Lieu in the database
//artwork_id|user_id|rating|comment|photo|created_at|updated_at
class SaveLieu : AsyncTask<String, String, String>() {
    override fun doInBackground(vararg params: String): String? {
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("artwork_id", params[0])
            .add("user_id", params[1])
            .add("rating", params[2])
            .add("comment", params[3])
            .add("photo", params[4])
            .add("created_at", params[5])
            .add("updated_at", params[6])
            .build()
        val request = Request.Builder()
            .url("https://picasso.iro.umontreal.ca/~mona/api/User/ArtworkController")//TODO a determiner
            //\User\PlaceController
            .post(formBody)
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