package com.example.mona.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mona.*
import com.example.mona.adapters.*
import com.example.mona.databinding.*

import com.google.android.material.tabs.TabLayoutMediator


class HomeViewPagerFragment(): Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentViewPagerBinding.inflate(inflater, container, false)

        val tabLayout = binding.tabs
        
        //Colors on tab icons
        tabLayout.tabIconTint = null

        val viewPager = binding.viewPager

        viewPager.isUserInputEnabled = false

        viewPager.adapter = PagerAdapter(this)

        // Set the icon and text for each tab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(getTabIcon(position))
            tab.text = getTabTitle(position)
        }.attach()

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        return binding.root
    }

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            ODJ_PAGE_INDEX -> R.drawable.odj
            MAP_PAGE_INDEX -> R.drawable.map
            LIST_PAGE_INDEX -> R.drawable.list
            COLLECTION_PAGE_INDEX -> R.drawable.collection
            else -> throw IndexOutOfBoundsException()
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            ODJ_PAGE_INDEX -> getString(R.string.ODJ)
            MAP_PAGE_INDEX -> getString(R.string.Map)
            LIST_PAGE_INDEX -> getString(R.string.List)
            COLLECTION_PAGE_INDEX -> getString(R.string.Collection)
            else -> null
        }
    }

}