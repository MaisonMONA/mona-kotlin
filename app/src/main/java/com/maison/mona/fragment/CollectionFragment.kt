package com.maison.mona.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.Nullable
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.maison.mona.R
import com.maison.mona.activities.BadgeActivity
import com.maison.mona.adapters.CollectionAdapter
import com.maison.mona.databinding.FragmentCollectionBinding
import com.maison.mona.viewmodels.OeuvreViewModel
import kotlinx.android.synthetic.main.fragment_collection.*
import kotlinx.android.synthetic.main.fragment_oeuvre_item.*

class CollectionFragment : Fragment() {

    private val oeuvreViewModel : OeuvreViewModel by viewModels()

    private var badgeTop: LinearLayout? = null
    private var badgeBottom: LinearLayout? = null
    private var badgeCardview: CardView? = null

    @SuppressLint("StringFormatInvalid")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCollectionBinding.inflate(inflater, container, false)
        context ?: return binding.root

        binding.apply {
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

            oeuvreViewModel.collectedList.observe(viewLifecycleOwner, { collected ->

                when (val count = collected.filter { it.state == 2 || it.state == 3 }.size) {
                    0 -> {
                        collectionCount.text = "0"
                    }
                    1 -> {
                        collectionCount.text = "1"
                        collectionMessage.text = "Votre collection"
                    }
                    else -> {
                        collectionCount.text = count.toString()
                        collectionMessage.text = "Votre collection"
                    }
                }
            })

            //badge_button = binding.badgeButton
            badgeTop = binding.collectionBadgeTop
            //badgeBottom = binding.collectionBadgeBottom
            badgeCardview = binding.collectionCardviewBadge


            val transition = AutoTransition()
            transition.duration = 500

            badgeTop?.setOnClickListener {
                val intent = Intent(context, BadgeActivity::class.java)
                startActivity(intent)
            }

        }


        return binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {

        collection_cardview_badge.getLayoutTransition().setAnimateParentHierarchy(false);
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}
