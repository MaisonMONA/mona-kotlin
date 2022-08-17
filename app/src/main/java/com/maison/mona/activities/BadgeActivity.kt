package com.maison.mona.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maison.mona.R
import com.maison.mona.adapters.BadgeAdapter
import com.maison.mona.viewmodels.BadgeViewModel

class BadgeActivity : AppCompatActivity() {

    private val badgeViewModel: BadgeViewModel by viewModels()

    //toolbar en haut de l'ecran
    private var mBackButton: Toolbar? = null
    private var mBackButtonText: TextView? = null
    private var badgesCount: TextView? = null

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var adapter: BadgeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badge)

        badgesCount = findViewById(R.id.badges_count)
        //on recupere la toolbar
        mBackButton = findViewById(R.id.badge_toolbar)
//        mBackButtonText = findViewById(R.id.badge_detail_text)
        mRecyclerView = findViewById(R.id.badge_recyclerview)

        //si on clique sur la toolbar on stop l'activité
        mBackButton?.setOnClickListener {
            finish()
        }

        //on crée l'adapter pour les badges
        adapter = BadgeAdapter(applicationContext)

        //on le donne au recyclerview
        mRecyclerView.adapter = adapter

        //on definit un linearLayout pour le recyclerview (= liste verticale)
        mRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        val listTypes: List<String> = listOf(
            getString(R.string.badges_type_borough),
            getString(R.string.badges_type_artworks),
            getString(R.string.badges_type_category),
            getString(R.string.badges_type_other))

        //on donne a l'adapter une liste a afficher
        adapter.submitList(listTypes)

        mBackButton?.title = getString(R.string.badge_toolbar_title_main)


        badgeViewModel.badgesList.observe(this) { collected ->

            when (val count = collected.filter { it.isCollected }.size) {
                0 -> {
                    badgesCount?.setText("0")
                }
                1 -> {
                    badgesCount?.setText("1")
                }
                else -> {

                    badgesCount?.text=count.toString()
                }
            }
        }
        //on "observe" la liste des badges de notre database et on la donne a l'adapter
        badgeViewModel.badgesList.observe(this) { badgeList ->
            adapter.giveAdapter(mBackButton, badgeList, this, supportFragmentManager, mRecyclerView)
        }
    }
}