package com.maison.mona.fragment

import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.maison.mona.activities.BadgeActivity
import com.maison.mona.adapters.CollectionAdapter
import com.maison.mona.databinding.FragmentCollectionBinding
import com.maison.mona.viewmodels.OeuvreViewModel

class CollectionFragment : Fragment() {

    private val oeuvreViewModel : OeuvreViewModel by viewModels()

    private var badge_top: LinearLayout? = null
    private var badge_bottom: LinearLayout? = null
    private var badge_cardview: CardView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCollectionBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val recyclerView = binding.collectionRecyclerview

        val adapter = CollectionAdapter(
            context,
            findNavController()
        )

        recyclerView.adapter = adapter

        //on observe les oeuvres en state 2 (collectionnees) ou 3 (collectionnes mais hors ligne)
        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, { oeuvreList ->
            val sortedOeuvres = oeuvreList.filter { (it.state == 2 || it.state == 3) }
            adapter.submitList(sortedOeuvres)
        })

        //badge_button = binding.badgeButton
        badge_top = binding.collectionBadgeTop
        badge_bottom = binding.collectionBadgeBottom
        badge_cardview = binding.collectionCardviewBadge


        val transition = AutoTransition()
        transition.duration = 500

//        badge_top?.setOnClickListener { view ->
//            if(badge_bottom?.visibility == View.GONE){
//                TransitionManager.beginDelayedTransition(badge_cardview, transition)
//                badge_bottom?.visibility = View.VISIBLE
//            } else {
//                TransitionManager.beginDelayedTransition(badge_cardview, transition)
//                badge_bottom?.visibility = View.GONE
//            }
//        }

        badge_top?.setOnClickListener {
            val intent = Intent(context, BadgeActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }
}
