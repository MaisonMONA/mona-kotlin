package com.maison.mona.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.maison.mona.fragment.*

//SET THE ORDER OF THE FRAGMENT
const val ODJ_PAGE_INDEX = 0
const val MAP_PAGE_INDEX = 2
const val LIST_PAGE_INDEX = 1
const val COLLECTION_PAGE_INDEX = 3
const val MORE_PAGE_INDEX = 4

class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        ODJ_PAGE_INDEX to { OeuvreJourFragment() },
        MAP_PAGE_INDEX to { MapFragment() },
        LIST_PAGE_INDEX to { ListFragment() },
        //LIST_PAGE_INDEX to { ExpListFragment() },
        COLLECTION_PAGE_INDEX to { CollectionFragment() },
        MORE_PAGE_INDEX to { MoreFragment() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

}