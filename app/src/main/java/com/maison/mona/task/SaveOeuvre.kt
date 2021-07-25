package com.maison.mona.task

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.maison.mona.data.SaveSharedPreference
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

//Store the artwork in the database
//artwork_id|user_id|rating|comment|photo|created_at|updated_at
class SaveOeuvre(val context: Context) : AsyncTask<String, String, String>() {
    private val saveOeuvre = "Save Oeuvre"
    @RequiresApi(Build.VERSION_CODES.P)
    override fun doInBackground(vararg params: String): String? {
        val client = OkHttpClient()

        //We must check if the comment is empty
        val comment = params[2]

        val imageFile = File(context.cacheDir,"temp.jpg")
        // var testImage = File(params[3])
        val imageBitMap = BitmapFactory.decodeFile(params[3])
        val outStream = FileOutputStream(imageFile)
        imageBitMap.compress(Bitmap.CompressFormat.JPEG,30,outStream)

        val fileName = "artwork" + params[0] + ".jpg"

        if(!imageFile.exists()){
            Log.d(saveOeuvre, "File does not exits")
        }

        val mtjpeg: MediaType? = "image/jpeg".toMediaTypeOrNull()

        if (mtjpeg == null) {
            Log.d(saveOeuvre, "Probleme Media type")
        }

        if(!isNetworkConnected()){
            Log.d(saveOeuvre, "Probleme connexion")
        }

        var url = ""
        when(params[4]){
            "artwork"-> url = "https://picasso.iro.umontreal.ca/~mona/api/user/artworks"
            "place"-> url = "https://picasso.iro.umontreal.ca/~mona/api/user/places"
        }

        val formBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_token", SaveSharedPreference.getToken(context))//Get user token
                .addFormDataPart("id", params[0])
                .addFormDataPart("rating", params[1])
                .addFormDataPart("comment", comment)
                //.addFormDataPart("photo", fileName, imageFile.asRequestBody(mtjpeg))
                .build()
        val request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()

        return try {
            val response = client.newCall(request).execute()
            return response.body!!.string()
        } catch (e: IOException) {
            Log.d(saveOeuvre, "Erreur Save oeuvre: " + e.printStackTrace().toString())
            e.printStackTrace()
            null
        }
    }

    private fun isNetworkConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}