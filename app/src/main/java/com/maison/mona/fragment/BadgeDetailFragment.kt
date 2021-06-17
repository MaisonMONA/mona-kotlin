package com.maison.mona.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.maison.mona.R
import com.maison.mona.entity.Badge
import com.maison.mona.viewmodels.OeuvreViewModel

class BadgeDetailFragment(badge: Badge?): Fragment(R.layout.badge_detail) {

    private lateinit var mContext: Context
    private var mBadge = badge

    private val oeuvreViewModel: OeuvreViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("BADGES", "TODO back from fragment")
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.badge_detail, container, false)

        //fonction pour compter la progression du badge, on passe a travers toutes les oeuvres pour compter celles qui matchent le badge
        oeuvreViewModel.collectedList.observe(viewLifecycleOwner, {collected ->
            var count: Int
            val args = mBadge?.optional_args!!

            Log.d("BADGES", collected[0].category?.fr.toString())
            Log.d("BADGES", args)

            for(oeuvre in collected){
                Log.d("BADGES", oeuvre.borough.toString())
            }

            if(args.contains("borough")) {
                count = collected.filter { args.contains(it.borough.toString()) }.size

                if(args.contains("Rivière-des-Prairies")){
                    count = collected.filter { it.borough?.contains("Rivière-des-Prairies")!! }.size
                }
            } else if(args.contains("category")){
                val a = collected.filter { args.contains(it.category?.en.toString())}.size
                val b = collected.filter { args.contains(it.category?.fr.toString())}.size
                count = if (a > b) a else b
            } else if(args.contains("Université de Montréal")){
                count = collected.filter { it.collection == "Université de Montréal"}.size
            } else {
                count = collected.size
            }

            val countString = getString(R.string.badge_detail_count, count, mBadge?.goal)
            view.findViewById<TextView>(R.id.badge_detail_goal).text = countString
        })

        //on assigne le texte du badge, ainsi que la description
        view.findViewById<TextView>(R.id.badge_detail_text).text = mBadge?.title_fr
        view.findViewById<TextView>(R.id.badge_detail_description).text = mBadge?.description_fr

        assignDrawable(mBadge, view)

        return view
    }

    private fun assignDrawable(badge: Badge?, view: View){
        when {
            badge?.optional_args!!.contains("borough") -> {
                val borough = badge.optional_args

                when {
                    borough.contains("Côte-des-Neiges") -> {
                        setDrawable(view, R.drawable.badge_icon_cdn_color, R.drawable.badge_icon_cdn_grey)
                    }
                    borough.contains("Ville-Marie") -> {
                        setDrawable(view, R.drawable.badge_icon_vm_color, R.drawable.badge_icon_vm_grey)
                    }
                    borough.contains("Rosemont") -> {
                        setDrawable(view, R.drawable.badge_icon_rosemont_color, R.drawable.badge_icon_rosemont_grey)
                    }
                    borough.contains("Le Plateau") -> {
                        setDrawable(view, R.drawable.badge_icon_pmr_color, R.drawable.badge_icon_pmr_grey)
                    }
                    borough.contains("Le Sud-Ouest") -> {
                        setDrawable(view, R.drawable.badge_icon_so_color, R.drawable.badge_icon_so_grey)
                    }
                    borough.contains("Mercier") -> {
                        setDrawable(view, R.drawable.badge_icon_hochelaga_color, R.drawable.badge_icon_hochelaga_grey)
                    }
                    borough.contains("Rivière-des-Prairies") -> {
                        setDrawable(view, R.drawable.badge_icon_riviere_des_prairies_color, R.drawable.badge_icon_riviere_des_prairies_grey)
                    }
                    borough.contains("Verdun") -> {
                        setDrawable(view, R.drawable.badge_icon_verdun_color, R.drawable.badge_icon_verdun_grey)
                    }
                    borough.contains("Villeray") -> {
                        setDrawable(view, R.drawable.badge_icon_villeray_color, R.drawable.badge_icon_villeray_grey)
                    }
                    borough.contains("Lachine") -> {
                        setDrawable(view, R.drawable.badge_icon_lachine_color, R.drawable.badge_icon_lachine_grey)
                    }
                    borough.contains("LaSalle") -> {
                        setDrawable(view, R.drawable.badge_icon_lasalle_color, R.drawable.badge_icon_lasalle_grey)
                    }
                    borough.contains("Ahuntsic") -> {
                        setDrawable(view, R.drawable.badge_icon_ac_color, R.drawable.badge_icon_ac_grey)
                    }
                    borough.contains("Outremont") -> {
                        setDrawable(view, R.drawable.badge_icon_outremont_color, R.drawable.badge_icon_outremont_grey)
                    }
                }
            }

            badge.optional_args.contains("category") -> {
                val category = badge.optional_args

                when {
                    category.contains("Decorative") -> {
                        setDrawable(view, R.drawable.badge_icon_art_decoratif_color, R.drawable.badge_icon_art_decoratif_grey)
                    }
                    category.contains("Beaux-Arts") -> {
                        setDrawable(view, R.drawable.badge_icon_beaux_arts_color, R.drawable.badge_icon_beaux_arts_grey)
                    }
                    category.contains("Murals") -> {
                        setDrawable(view, R.drawable.badge_icon_murales_color, R.drawable.badge_icon_murales_grey)
                    }
                }
            }

            badge.optional_args.length <= 3 -> {
                when(badge.goal){
                    1 -> { setDrawable(view, R.drawable.badge_icon_quantite_1_color, R.drawable.badge_icon_quantite_1_grey) }
                    3 -> { setDrawable(view, R.drawable.badge_icon_quantite_3_color, R.drawable.badge_icon_quantite_3_grey) }
                    5 -> { setDrawable(view, R.drawable.badge_icon_quantite_5_color, R.drawable.badge_icon_quantite_5_grey) }
                    8 -> { setDrawable(view, R.drawable.badge_icon_quantite_8_color, R.drawable.badge_icon_quantite_8_grey) }
                    10 -> { setDrawable(view, R.drawable.badge_icon_quantite_10_color, R.drawable.badge_icon_quantite_10_grey) }
                    15 -> { setDrawable(view, R.drawable.badge_icon_quantite_15_color, R.drawable.badge_icon_quantite_15_grey) }
                    20 -> { setDrawable(view, R.drawable.badge_icon_quantite_20_color, R.drawable.badge_icon_quantite_20_grey) }
                    25 -> { setDrawable(view, R.drawable.badge_icon_quantite_25_color, R.drawable.badge_icon_quantite_25_grey) }
                    30 -> { setDrawable(view, R.drawable.badge_icon_quantite_30_color, R.drawable.badge_icon_quantite_30_grey) }
                }
            }

            badge.optional_args.contains("collection") -> {
                setDrawable(view, R.drawable.badge_icon_udem_color, R.drawable.badge_icon_udem_grey)
            }
        }
    }

    fun setDrawable(view: View, imageCollected: Int, imageNotCollected: Int){
        if(mBadge?.isCollected == true){
            view.findViewById<ImageView>(R.id.badge_detail_image).setImageResource(imageCollected)
        } else {
            view.findViewById<ImageView>(R.id.badge_detail_image).setImageResource(imageNotCollected)
        }
    }
}