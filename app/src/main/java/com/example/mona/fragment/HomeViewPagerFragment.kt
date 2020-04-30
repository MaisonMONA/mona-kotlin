package com.example.mona.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mona.R
import com.example.mona.adapters.*
import com.example.mona.databinding.FragmentViewPagerBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeViewPagerFragment(): Fragment() {

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

        bottomNavigation.setOnNavigationItemSelectedListener(
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.odj_dest -> viewPager.currentItem = ODJ_PAGE_INDEX
                    R.id.map_dest -> viewPager.currentItem = MAP_PAGE_INDEX
                    R.id.list_dest -> viewPager.currentItem = LIST_PAGE_INDEX
                    R.id.collection_dest -> viewPager.currentItem = COLLECTION_PAGE_INDEX
                }
                true
            })

        //Remove tint and use custom selectors
        bottomNavigation.itemIconTintList = null

        return binding.root
    }


}