package com.example.mona.service

import android.app.IntentService
import android.content.Intent

class ConnectivityService : IntentService(ConnectivityService::class.simpleName){

    override fun onHandleIntent(workIntent: Intent) {
        // Gets data from the incoming Intent
        val dataString = workIntent.dataString

        // Do work here, based on the contents of dataString

    }
}

