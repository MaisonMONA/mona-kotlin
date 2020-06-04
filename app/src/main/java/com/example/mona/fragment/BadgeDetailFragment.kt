package com.example.mona.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mona.databinding.FragmentBadgeItemBinding

class BadgeDetailFragment : Fragment(){

    private val safeArgs: BadgeDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBadgeItemBinding.inflate(inflater, container, false)
        context ?: return binding.root

        binding.apply {
            badge = safeArgs.badge
            executePendingBindings()

            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
        }

        return binding.root
    }
}