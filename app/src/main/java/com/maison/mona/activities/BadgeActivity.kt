package com.maison.mona.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maison.mona.R
import com.maison.mona.adapters.Badge_2Adapter
import com.maison.mona.viewmodels.BadgeViewModel
import java.util.*

class BadgeActivity : AppCompatActivity() {

    private val badgeViewModel: BadgeViewModel by viewModels()

    private var mBackButton: Toolbar? = null
    private var mResetSearchButton: Button? = null

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var adapter: Badge_2Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badge)

        mBackButton = findViewById(R.id.badge_toolbar)
        //mResetSearchButton = findViewById(R.id.badge_button_back)
        mRecyclerView = findViewById(R.id.badge_recyclerview)

        mBackButton?.setOnClickListener({
            finish()
        })

        adapter = Badge_2Adapter(applicationContext)

        mRecyclerView.adapter = adapter
        mRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        val test: List<String> = listOf("Quartiers", "Oeuvres", "Autres")
        adapter.submitList(test)

        badgeViewModel.badgesList.observe(this, Observer { badgeList ->
            adapter.giveAdapter(mBackButton, badgeList, this, supportFragmentManager)
        })
    }
}