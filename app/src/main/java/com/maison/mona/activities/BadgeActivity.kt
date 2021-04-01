package com.maison.mona.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import com.maison.mona.R

class BadgeActivity : AppCompatActivity() {

    private var mBackButton: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badge)

        mBackButton = findViewById(R.id.badge_toolbar)

        mBackButton?.setOnClickListener({
            finish()
        })
    }
}