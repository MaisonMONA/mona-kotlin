package com.maison.mona.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.maison.mona.R
import com.maison.mona.databinding.FragmentTestBinding
import com.maison.mona.viewmodels.BadgeViewModel
import com.maison.mona.viewmodels.OeuvreViewModel

class BlankFragment : Fragment(R.layout.fragment_test) {
    private val oeuvreViewModel: OeuvreViewModel by viewModels()
    private val badgeViewModel: BadgeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTestBinding.inflate(inflater, container, false)
        context ?: return binding.root

        binding.apply {
            oeuvreViewModel.collectedList.observe(viewLifecycleOwner, { collected ->

                when (val count = collected.filter { it.state == 2 || it.state == 3 }.size) {
                    0 -> {
                        moreUserArtworks.text = "Aucune œuvre collectionnée"
                    }
                    1 -> {
                        moreUserArtworks.text = "1 œuvre collectionnée"
                    }
                    else -> {
                        val artworksString = getString(R.string.more_user_artworks_count, count)
                        moreUserArtworks.text = artworksString
                    }
                }
            })

            badgeViewModel.badgesList.observe(viewLifecycleOwner, { collected ->

                when (val count = collected.filter { it.isCollected }.size) {
                    0 -> {
                        moreUserBadges.text = "Aucun badge débloqué"
                    }
                    1 -> {
                        moreUserBadges.text = "1 badge débloqué"
                    }
                    else -> {
                        val badgesString = getString(R.string.more_user_badges_count, count)
                        moreUserBadges.text = badgesString
                    }
                }
            })
        }

        return binding.root
    }
}