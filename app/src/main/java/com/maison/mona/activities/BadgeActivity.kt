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

    //toolbar en haut de l'ecran
    private var mBackButton: Toolbar? = null

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var adapter: Badge_2Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badge)

        //on recupere la toolbar
        mBackButton = findViewById(R.id.badge_toolbar)
        mRecyclerView = findViewById(R.id.badge_recyclerview)

        //si on clique sur la toolbar on stop l'activité
        mBackButton?.setOnClickListener({
            finish()
        })

        //on crée l'adapter pour les badges
        adapter = Badge_2Adapter(applicationContext)

        //on le donne au recyclerview
        mRecyclerView.adapter = adapter

        //on definit un linearLayout pour le recyclerview (= liste verticale)
        mRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        val test: List<String> = listOf("Quartiers", "Oeuvres", "Autres")

        //on donne a l'adapter une liste a afficher
        adapter.submitList(test)

        //on "observe" la liste des badges de notre database et on la donne a l'adapter
        badgeViewModel.badgesList.observe(this, Observer { badgeList ->
            adapter.giveAdapter(mBackButton, badgeList, this, supportFragmentManager)
        })
    }
}