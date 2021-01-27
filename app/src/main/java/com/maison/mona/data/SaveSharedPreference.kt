package com.maison.mona.data

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
    private var ONLINE:String = "online"
    private var FIRSTTIME:String = "firsttime"
    private var LASTUPDATE:String = "00-00-0000"

    fun getSharedPreferences(ctx: Context?): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun getUsername(ctx: Context?): String {
        return getSharedPreferences(
            ctx
        )
            .getString(PREF_USER_NAME, "")!!
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
            .getString(TOKEN, "")!!
    }

    fun setToken(ctx: Context?, token: String?) {
        val editor: SharedPreferences.Editor = getSharedPreferences(
            ctx
        ).edit()
        editor.putString(TOKEN, token)
        editor.commit()
    }
    //Online informations
    //Check if the user is in online or offline mode
    fun isOnline(ctx: Context?): Boolean{
        val value = getSharedPreferences(ctx)
            .getString(ONLINE, "true")
        return value.toBoolean()}

    //Set the online mode to true or false
    fun setOnline(ctx: Context?, value: Boolean){
        val editor: SharedPreferences.Editor = getSharedPreferences(
            ctx
        ).edit()
        val stringValue = value.toString()
        editor.putString(ONLINE, stringValue)
        editor.apply()
    }

    //First time tutorial
    fun firstTime(ctx: Context?):Boolean{
        val value = getSharedPreferences(ctx)
            .getString(FIRSTTIME, "true")
        return value.toBoolean()
    }

    //Toggle off after the first time for the first time tutorial
    fun toggleFirstTime(ctx: Context?){
        val editor: SharedPreferences.Editor = getSharedPreferences(
            ctx
        ).edit()
        editor.putString(FIRSTTIME,"false")
        editor.apply()
    }
    //Set first time manually, mostly for tests can remove in the final version
    fun setFirstTime(ctx: Context?, status:Boolean){
        val editor: SharedPreferences.Editor = getSharedPreferences(
            ctx
        ).edit()
        editor.putString(FIRSTTIME,status.toString())
        editor.apply()
    }

    fun getLastUpdate(ctx: Context?): String {
        return getSharedPreferences(ctx)
            .getString(LASTUPDATE, "0000-00-00 12:00:00.000")!!
    }

    fun setLastUpdate(ctx: Context?, time: String) {
        val editor: SharedPreferences.Editor = getSharedPreferences(
            ctx
        ).edit()
        editor.putString(LASTUPDATE, time)
        editor.apply()
    }
}
