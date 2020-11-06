package com.example.mona.activities

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import com.example.mona.R
import com.example.mona.data.SaveSharedPreference


class MyGlobals(var mContext: Context) {

    fun setOnlineMode():Boolean{
        Log.d("Save", "Set Online mode")
        //online = isNetworkConnected()
        var ss = SaveSharedPreference
        var online = ss.isOnline(mContext)
        if(online){
            ss.setOnline(mContext,false)
            //Toast.makeText(activity, R.string.offline_message, Toast.LENGTH_LONG).show()
        }else{
            if(!isNetworkConnected()){
                //Toast.makeText(activity, R.string.online_requirement_message, Toast.LENGTH_LONG).show()
                ss.setOnline(mContext,false)
            }else{
                //Toast.makeText(activity, R.string.online_message, Toast.LENGTH_LONG).show()
                ss.setOnline(mContext,true)
            }
        }
        return ss.isOnline(mContext)
    }

    // There are no active networks.
    fun isNetworkConnected(): Boolean{
        val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = cm.activeNetworkInfo
        return ni != null
    }
}