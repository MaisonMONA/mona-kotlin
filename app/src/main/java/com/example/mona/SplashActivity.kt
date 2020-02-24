package com.example.mona
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.ViewModelProvider

class SplashActivity : AppCompatActivity() {

    //https://stackoverflow.com/questions/31473557/android-display-splash-screen-while-loading
    //
    //Instead of a regular splash screen we use a drawable layer list (https://developer.android.com/guide/topics/resources/drawable-resource)
    //to display to the user while the MainActivity sets up to load

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this,MainActivity::class.java))


    }
}
