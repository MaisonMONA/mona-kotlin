package com.example.mona.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import java.io.IOException
import java.net.Inet4Address
import java.net.NetworkInterface

private const val TAG = "ConnectivityBroadcastReceiver"

class ConnectivityBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult: PendingResult = goAsync()
        val asyncTask =
            Task(
                pendingResult
            )
        asyncTask.execute()
    }

    private class Task(
        private val pendingResult: PendingResult
    ) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params : Void): Boolean {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://google.com")
                .build()
            return try {
                val response = client.newCall(request).execute()
                return response.code() == 200

            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            // Must call finish() so the BroadcastReceiver can be recycled.
            pendingResult.finish()
        }
    }
}
