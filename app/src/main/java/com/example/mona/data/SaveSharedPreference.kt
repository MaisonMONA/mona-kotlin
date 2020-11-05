package com.example.mona.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

//Based off: https://stackoverflow.com/questions/12744337/how-to-keep-android-applications-always-be-logged-in-state
//If you have a relatively small collection of key-values that you'd like to save, you should use the
// SharedPreferences APIs. A SharedPreferences object points to a file containing key-value pairs and
// provides simple methods to read and write them. Each SharedPreferences file is managed by the framework
// and can be private or shared.

object SaveSharedPreference {

    private const val PREF_USER_NAME:String = "username"
    private const val TOKEN:String = "token"
    private var ONLINE:String = "true"
    fun getSharedPreferences(ctx: Context?): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun getUsername(ctx: Context?): String {
        return getSharedPreferences(
            ctx
        )
            .getString(PREF_USER_NAME, "")
    }

    fun setUsername(ctx: Context?, username: String?) {
        val editor: SharedPreferences.Editor = getSharedPreferences(
            ctx
        ).edit()
        editor.putString(PREF_USER_NAME, username)
        editor.commit()
    }

    fun getToken(ctx: Context?): String {
        return getSharedPreferences(
            ctx
        )
            .getString(TOKEN, "")
    }

    fun setToken(ctx: Context?, token: String?) {
        val editor: SharedPreferences.Editor = getSharedPreferences(
            ctx
        ).edit()
        editor.putString(TOKEN, token)
        editor.commit()
    }
    fun isOnline(ctx: Context?): Boolean{
        val value = getSharedPreferences(ctx)
            .getString(ONLINE, "true")
        return value.toBoolean()}

    fun setOnline(ctx: Context?, value: Boolean){
        val editor: SharedPreferences.Editor = getSharedPreferences(
            ctx
        ).edit()
        var stringValue = value.toString()
        editor.putString(ONLINE, stringValue)
        editor.commit()
    }


}
