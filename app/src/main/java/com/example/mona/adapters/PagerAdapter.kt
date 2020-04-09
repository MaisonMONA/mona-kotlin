package com.example.mona.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mona.fragment.CollectionFragment
import com.example.mona.fragment.ListFragment
import com.example.mona.fragment.MapFragment
import com.example.mona.fragment.OeuvreJourFragment


const val ODJ_PAGE_INDEX = 0
const val MAP_PAGE_INDEX = 1
const val LIST_PAGE_INDEX = 2
const val COLLECTION_PAGE_INDEX = 3

class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        ODJ_PAGE_INDEX to { OeuvreJourFragment() },
        MAP_PAGE_INDEX to { MapFragment() },
        LIST_PAGE_INDEX to { ListFragment() },
        COLLECTION_PAGE_INDEX to { CollectionFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

}