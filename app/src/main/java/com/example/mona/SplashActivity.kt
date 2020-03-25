package com.example.mona
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class SplashActivity : AppCompatActivity() {

    //https://stackoverflow.com/questions/31473557/android-display-splash-screen-while-loading
    //
    //Instead of a regular splash screen we use a drawable layer list (https://developer.android.com/guide/topics/resources/drawable-resource)
    //to display to the user while the MainActivity sets up to load

    private lateinit var oeuvreViewModel: OeuvreViewModel
    private lateinit var lieuViewModel: LieuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //MainActivity does not start before the database has been loaded

        oeuvreViewModel = ViewModelProvider(this).get(OeuvreViewModel::class.java)
        lieuViewModel = ViewModelProvider(this).get(LieuViewModel::class.java)

        oeuvreViewModel.oeuvreList.observe(this, Observer{ oeuvrelist ->
            //Submit the list to the adapter
            if (!oeuvrelist.isEmpty()){

                lieuViewModel.lieuList.observe(this, Observer {lieuList ->
                    if(!lieuList.isEmpty()){
                        startActivity(Intent(this,MainActivity::class.java))
                    }
                })

            }
        })

    }
}
