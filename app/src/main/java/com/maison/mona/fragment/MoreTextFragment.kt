package com.maison.mona.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.maison.mona.databinding.FragmentMoreTextBinding

class MoreTextFragment : Fragment(){
    val safeArgs: MoreTextFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMoreTextBinding.inflate(inflater, container, false)

        binding.apply {
            markdownView.loadMarkdownFromAssets(safeArgs.fileName)
        }

        return binding.root
    }
}