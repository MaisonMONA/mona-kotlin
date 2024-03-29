package com.maison.mona.activities

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.maison.mona.data.SaveSharedPreference

class MyGlobals(var mContext: Context) {

    fun setOnlineMode():Boolean{
        Log.d("Save", "Set Online mode")
        //online = isNetworkConnected()
        val online = SaveSharedPreference.isOnline(mContext)
        if(online){
            SaveSharedPreference.setOnline(mContext,false)
            //Toast.makeText(activity, R.string.offline_message, Toast.LENGTH_LONG).show()
        }else{
            if(!isNetworkConnected()){
                //Toast.makeText(activity, R.string.online_requirement_message, Toast.LENGTH_LONG).show()
                SaveSharedPreference.setOnline(mContext,false)
            }else{
                //Toast.makeText(activity, R.string.online_message, Toast.LENGTH_LONG).show()
                SaveSharedPreference.setOnline(mContext,true)
            }
        }
        return SaveSharedPreference.isOnline(mContext)
    }

    // There are no active networks.
    fun isNetworkConnected(): Boolean {
        val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = cm.activeNetworkInfo
        return ni != null
    }
}