package com.example.mona.activities
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mona.data.SaveSharedPreference
import com.example.mona.viewmodels.OeuvreViewModel

class SplashActivity : AppCompatActivity() {

    //https://stackoverflow.com/questions/31473557/android-display-splash-screen-while-loading
    //
    //Instead of a regular splash screen we use a drawable layer list (https://developer.android.com/guide/topics/resources/drawable-resource)
    //to display to the user while the MainActivity sets up to load

    private lateinit var oeuvreViewModel: OeuvreViewModel
    //private lateinit var lieuViewModel: LieuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Set the internet Mode
        oeuvreViewModel = ViewModelProvider(this).get(OeuvreViewModel::class.java)
        //lieuViewModel = ViewModelProvider(this).get(LieuViewModel::class.java)

        oeuvreViewModel.oeuvreList.observe(this, Observer { oeuvreList ->
            if(!oeuvreList.isEmpty()){
                oeuvreViewModel.oeuvreList.observe(this, Observer {oeuvreList ->
                    startActivity(Intent(this,
                        MainActivity::class.java))
                })
            }
        })
    }

}
