package com.example.mona.task

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.example.mona.data.SaveSharedPreference
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okio.BufferedSink
import okio.source
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


//Store the artwork in the database
//artwork_id|user_id|rating|comment|photo|created_at|updated_at
class SaveOeuvre(val context: Context) : AsyncTask<String, String, String>() {

    @RequiresApi(Build.VERSION_CODES.P)
    override fun doInBackground(vararg params: String): String? {
        val client = OkHttpClient()
        Log.d("Save", "Id: " + params[0])
        Log.d("Save", "Rating: " + params[1])
        Log.d("Save", "comment: " + params[2])
        Log.d("Save", "Path: " + params[3])

        var imageFile = File(context.cacheDir,"temp.jpg")
        var imageBitMap = BitmapFactory.decodeFile(params[3])
        var outStream = FileOutputStream(imageFile)
        imageBitMap.compress(Bitmap.CompressFormat.JPEG,30,outStream)

        val fileName = "artwork" + params[0] + ".jpg"

        if(imageFile.exists()){
            Log.d("Save", "File exists: " + imageFile.absolutePath)
           Log.d("Save", imageFile.name)
        }else{
            Log.d("Save", "File does not exits")
        }

        val mtjpeg: MediaType? = "image/jpeg".toMediaTypeOrNull()
        if (mtjpeg != null) {
            Log.d("Save", "Type de file: " + mtjpeg.subtype)
        }else{
            Log.d("Save", "Probleme Media type")
        }
        Log.d("Save", fileName)
        Log.d("Save", "Token: " + SaveSharedPreference.getToken(context))

        if(isNetworkConnected()){
            Log.d("Save", "Connection correcte");
        }else{
            Log.d("Save", "Probleme connexion");
        }
        Log.d("Save","Type: " + params[4])
        var url = "";
        when(params[4]){
            "artwork"-> url = "https://picasso.iro.umontreal.ca/~mona/api/user/artworks"
            "place"-> url = "https://picasso.iro.umontreal.ca/~mona/api/user/places"
        }

        val formBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_token", SaveSharedPreference.getToken(context))//Get user token
                .addFormDataPart("id", params[0])
                .addFormDataPart("rating", params[1])
                .addFormDataPart("comment", params[2])
                .addFormDataPart("photo", fileName, imageFile.asRequestBody(mtjpeg))
                .build()
        val request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()
        return try {
            val response = client.newCall(request).execute()
            Log.d("Save", "Save fini")
            val message = response.body!!.string()
            Log.d("Save", "Reponse $message")
            return message
        } catch (e: IOException) {
            Log.d("Save", "Erreur Save Oeuvre")
            Log.d("Save", "Erreur: " + e.printStackTrace().toString())
            e.printStackTrace()
            null
        }
    }
    private fun isNetworkConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        return isConnected
    }
}