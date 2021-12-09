package com.maison.mona.activities

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.maison.mona.data.SaveSharedPreference

class MyGlobals(var mContext: Context) {

    fun setOnlineMode():Boolean{
        Log.d("Save", "Set Online mode")
        val online = SaveSharedPreference.isOnline(mContext)
        if(online){
            SaveSharedPreference.setOnline(mContext,false)
        }else{
            if(!isNetworkConnected()){
                SaveSharedPreference.setOnline(mContext,false)
            }else{
                SaveSharedPreference.setOnline(mContext,true)
            }
        }
        return SaveSharedPreference.isOnline(mContext)
    }

    // There are no active networks.
    fun isNetworkConnected(): Boolean{
        val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val ni = cm.activeNetworkInfo
        return ni != null
    }
}