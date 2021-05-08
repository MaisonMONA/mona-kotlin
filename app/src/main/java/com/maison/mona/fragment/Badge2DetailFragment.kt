package com.maison.mona.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.maison.mona.R
import com.maison.mona.entity.Badge_2
import com.maison.mona.viewmodels.OeuvreViewModel

class Badge2DetailFragment(badge: Badge_2?): Fragment(R.layout.badge_detail) {

    private lateinit var mContext: Context
    private var mBadge = badge

    private val oeuvreViewModel: OeuvreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.badge_detail, container, false)

        //fonction pour compter la progression du badge, on passe a travers toutes les oeuvres pour compter celles qui matchent le badge
        oeuvreViewModel.collectedList.observe(viewLifecycleOwner, Observer {collected ->
            var count = 0

            if(mBadge?.optional_args!!.contains("borough")) {
                var borough = mBadge?.optional_args!!.substringAfter(":'").substringBefore("'}")
                count = collected.filter { it.borough == borough }.size
            } else {
                count = collected.size
            }

            val countString = getString(R.string.badge_detail_count, count, mBadge?.goal)
            view.findViewById<TextView>(R.id.badge_detail_goal).setText(countString)
        })

        //on assigne le texte du badge, ainsi que la description
        view.findViewById<TextView>(R.id.badge_detail_text).setText(mBadge?.title_fr)
        view.findViewById<TextView>(R.id.badge_detail_description).setText(mBadge?.description_fr)

        //si on a collecté le badge on met l'image adequate
        if(mBadge?.isCollected == true){
            view.findViewById<ImageView>(R.id.badge_detail_image).setImageResource(R.drawable.badge_icon_verdun_color)
        } else{
            view.findViewById<ImageView>(R.id.badge_detail_image).setImageResource(R.drawable.badge_icon_verdun_grey)
        }

        return view
    }
}