package com.maison.mona.task

import android.os.AsyncTask
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class LoginUser : AsyncTask<String, String, String>() {

    override fun doInBackground(vararg params: String): String? {
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("username", params[0])
            .add("password", params[1])
            .build()
        val request = Request.Builder()

            .url("https://picasso.iro.umontreal.ca/~mona/api/login")
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