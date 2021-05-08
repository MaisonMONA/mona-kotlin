package com.maison.mona.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.maison.mona.activities.BadgeActivity
import com.maison.mona.adapters.CollectionAdapter
import com.maison.mona.databinding.FragmentCollectionBinding
import com.maison.mona.viewmodels.OeuvreViewModel


class CollectionFragment : Fragment() {

    private val oeuvreViewModel : OeuvreViewModel by viewModels()

    private var badge_button : Button? = null;

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
        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { oeuvreList ->
            val sortedOeuvres = oeuvreList.filter { (it.state == 2 || it.state == 3) }
            adapter.submitList(sortedOeuvres)
        })

        badge_button = binding.badgeButton

        //en appuyant sur le bouton de badge on lance l'activity
        badge_button?.setOnClickListener { view ->
            val intent = Intent(context, BadgeActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
