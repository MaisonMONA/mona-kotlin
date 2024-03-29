package com.maison.mona.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.maison.mona.viewmodels.BadgeViewModel
import com.maison.mona.viewmodels.OeuvreViewModel

class SplashActivity : AppCompatActivity() {

    //https://stackoverflow.com/questions/31473557/android-display-splash-screen-while-loading
    //
    //Instead of a regular splash screen we use a drawable layer list (https://developer.android.com/guide/topics/resources/drawable-resource)
    //to display to the user while the MainActivity sets up to load

    private lateinit var oeuvreViewModel: OeuvreViewModel
    private lateinit var badgeViewModel: BadgeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Set the internet Mode
        oeuvreViewModel = ViewModelProvider(this).get(OeuvreViewModel::class.java)
        badgeViewModel = ViewModelProvider(this).get(BadgeViewModel::class.java)

        startActivity(Intent(this, MainActivity::class.java))

        // Va chercher les oeuvres
        oeuvreViewModel.oeuvreList.observe(this) { oeuvreList ->
            if (oeuvreList.isNotEmpty()) {
                // Va chercher les badges
                badgeViewModel.badgesList.observe(this) {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }
    }
}
