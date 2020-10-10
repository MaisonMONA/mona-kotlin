package com.example.mona.task

import android.os.AsyncTask
import android.util.Log
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

//Store the artwork in the database
//artwork_id|user_id|rating|comment|photo|created_at|updated_at
class SaveOeuvre : AsyncTask<String, String, String>() {
    override fun doInBackground(vararg params: String): String? {
        Log.d("Save", "Id: " + params[0])
        Log.d("Save", "rating: " + params[1])
        Log.d("Save", "Comment: " + params[2])
        Log.d("Save", "Photo: " + params[3])

        val client = OkHttpClient()
        val formBody = FormBody.Builder()
                .add("id", params[0])
                .add("rating", params[1])
                .add("comment", params[2])
                //.add("photo", params[3])
                .build()
        val request = Request.Builder()
                .url("https://picasso.iro.umontreal.ca/~mona/api/user/artworks")//TODO a determiner
                .post(formBody)
                .build()
        return try {
            val response = client.newCall(request).execute()
            Log.d("Save","Save fini")
            response.body!!.string()
        } catch (e: IOException) {
            Log.d("Save","Erreur save")
            e.printStackTrace()
            null
        }
    }

}