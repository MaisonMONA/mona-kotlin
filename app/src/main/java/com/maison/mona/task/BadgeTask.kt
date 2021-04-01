package com.maison.mona.task

import android.os.AsyncTask
import okhttp3.*
import org.json.JSONArray
import java.io.IOException


class BadgeTask(time: String) : AsyncTask<Void, Void, String>(){

    private val time: String?

    //@Suppress("IllegalIdentifier")
    //private var json_test: String = `{"data":[{"id":1,"title":"Outremont: badge en bronze","description":"Collectionnez 5 oeuvres dans le quartier d'Outremont pour obtenir ce badge.","image":null,"action":"general_collection","required_args":"{'nb_artworks':5}","optional_args":"{'borough':'Outremont'}","produced_at":"2021-03-06 10:40:14","updated_at":"2021-03-06 10:40:14"},{"id":2,"title":"Outremont: badge en argent","description":"Collectionnez 15 oeuvres dans le quartier d'Outremont pour obtenir ce badge.","image":null,"action":"general_collection","required_args":"{'nb_artworks':15}","optional_args":"{'borough':'Outremont'}","produced_at":"2021-03-06 10:41:21","updated_at":"2021-03-06 10:41:21"},{"id":3,"title":"Dur Critique","description":"Attribuez 1 etoile a 10 oeuvres pour obtenir ce badge.","image":null,"action":"rating","required_args":"{'nb_ratings':10}","optional_args":"{'rating_value':1}","produced_at":"2021-03-06 10:44:36","updated_at":"2021-03-06 10:44:36"},{"id":4,"title":"Feministe","description":"Collectionnez 10 oeuvresfaites par des femmes pour obtenir ce badge.","image":null,"action":"general_collection","required_args":"{'nb_artworks':10}","optional_args":"{'tag':'artiste_femme'}","produced_at":"2021-03-06 10:46:48","updated_at":"2021-03-06 10:46:48"},{"id":5,"title":"Oeuvre speciale","description":"Collectionnez la premiere oeuvre de notre base de donnee pour obtenir ce badge.","image":null,"action":"specific_collection","required_args":"{'artworks_ids':[1]}","optional_args":"{}","produced_at":"2021-03-06 10:50:54","updated_at":"2021-03-06 10:50:54"}]}`

    init {
        this.time= time
    }

    override fun doInBackground(vararg params: Void?): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://picasso.iro.umontreal.ca/~mona/api/lastUpdatedArtworks?date=" + this.time)
            .build()

        val json: JSONArray? = null

        return try {
            val response = client.newCall(request).execute()
            //response.body().string()
            json.toString()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}