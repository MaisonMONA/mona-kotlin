package com.example.mona.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mona.activities.LoginActivity
import com.example.mona.activities.RegisterActivity
import com.example.mona.data.SaveSharedPreference
import com.example.mona.databinding.FragmentMoreBinding

class MoreFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMoreBinding.inflate(inflater, container, false)
        context ?: return binding.root

        binding?.apply {

            //Affecting the username
            username.text = SaveSharedPreference.getUsername(context)

            badgeButton.setOnClickListener {
                val action = HomeViewPagerFragmentDirections.homeToBadge()
                findNavController().navigate(action)

            }
            howItWorksButton.setOnClickListener{
                val action = HomeViewPagerFragmentDirections.homeToText("CommentCaMarche.md")
                findNavController().navigate(action)
            }

            aboutButton.setOnClickListener {
                val action = HomeViewPagerFragmentDirections.homeToText("AproposMONA.md")
                findNavController().navigate(action)
            }

            whoAreWeButton.setOnClickListener {
                val action = HomeViewPagerFragmentDirections.homeToText("QuiSommesNous.md")
                findNavController().navigate(action)
            }

            signOutButton.setOnClickListener {
                val myIntent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(myIntent)
            }
        }

        return binding.root
    }

}