package com.maison.mona.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.maison.mona.R
import com.maison.mona.adapters.*
import com.maison.mona.data.BadgeDatabase
import com.maison.mona.data.BadgeRepository
import com.maison.mona.databinding.FragmentViewPagerBinding
import com.maison.mona.viewmodels.BadgeViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_view_pager.*
import kotlinx.android.synthetic.main.recyclerview_oeuvre.view.*


class HomeViewPagerFragment(): Fragment() {

    private val badgeViewModel: BadgeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Creation of callback to disable back button once home
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // do nothing
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentViewPagerBinding.inflate(inflater, container, false)

        val viewPager = binding.viewPager

        viewPager.isUserInputEnabled = false

        viewPager.adapter = PagerAdapter(this)

        //Save states of four fragments
        viewPager.offscreenPageLimit = 4
        
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        val bottomNavigation = binding.bottomNavView
        //bottomNavigation.background.setTint(resources.getColor(R.color.black))
        bottomNavigation.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.odj_dest -> viewPager.currentItem = ODJ_PAGE_INDEX
                R.id.map_dest -> viewPager.currentItem = MAP_PAGE_INDEX
                R.id.list_dest -> viewPager.currentItem = LIST_PAGE_INDEX
                R.id.collection_dest -> viewPager.currentItem = COLLECTION_PAGE_INDEX
                R.id.more_dest -> viewPager.currentItem = MORE_PAGE_INDEX
            }
            true
        }

        //Remove tint and use custom selectors
        bottomNavigation.itemIconTintList = null

        badgeDatabseInit()

        return binding.root
    }

    fun badgeDatabseInit(){
        val repository: BadgeRepository
        val badgeDAO = BadgeDatabase.getDatabase(
            requireContext(),
            lifecycleScope
        ).badgesDAO()
        repository = BadgeRepository.getInstance(badgeDAO)

        badgeViewModel.badgesList.observe(viewLifecycleOwner, Observer { badgesList ->
            Log.d("SAVE", "HomeViewPager : badgeDatabase initialised : " + badgesList.toString())

            for(badge in badgesList){
                //Log.d("SAVE", "HomeviewPager : " + badge.required_args?.substringAfter(':')?.substringBeforeLast('}'))
                badge.goal = badge.required_args?.substringAfter(':')?.substringBeforeLast('}')?.toInt()
                Log.d("SAVE", badge.goal.toString())
            }
        })
    }
}