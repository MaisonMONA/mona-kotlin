package com.maison.mona.activities

import ScreenSlidePageFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.maison.mona.R

private const val NUM_PAGES = 11


class Tutoriel : AppCompatActivity() {

    private var pager: ViewPager? = null
    private var indicator: TabLayout? = null
    private var next: Button? = null
    private var end: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutoriel)

        Log.d("TUTORIAL", "Start tutorial")

        pager = findViewById(R.id.pager)
        indicator = findViewById(R.id.indicator)
        end = findViewById(R.id.end)

        val pagerAdapter = ScreenSlidePagerAdapter(
            supportFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        pager?.adapter = pagerAdapter


        //Detect changes when we swipe
        pager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            // This method will be invoked when a new page becomes selected.
            override fun onPageSelected(position: Int) {

            }

            // This method will be invoked when the current page is scrolled
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            override fun onPageScrollStateChanged(state: Int) {
                // Code goes here
            }
        })
        indicator?.setupWithViewPager(pager)
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(
        fm,
        behavior
    ) {
        override fun getCount(): Int = NUM_PAGES

        override fun getItem(position: Int): Fragment {
            val fragment = ScreenSlidePageFragment()
            val bundle = Bundle()
            bundle.putInt("position", position)
            fragment.arguments = bundle
            return fragment
        }
    }
}