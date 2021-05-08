package com.maison.mona.activities

import ScreenSlidePageFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.maison.mona.R
import com.maison.mona.data.SaveSharedPreference
import com.google.android.material.tabs.TabLayout

private const val NUM_PAGES = 7

class OnboardingActivity : AppCompatActivity() {
    private var pager: ViewPager? = null
    private var indicator: TabLayout? = null
    private var next: Button? = null
    private var end:Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding);

        Log.d("TUTORIAL", "Start tutorial")

        pager = findViewById(R.id.pager);
        indicator = findViewById(R.id.indicator)
        end = findViewById(R.id.end)

        val pagerAdapter = ScreenSlidePagerAdapter(
            supportFragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        )
        pager?.adapter = pagerAdapter

        //The end button
        end?.visibility = View.INVISIBLE
        end?.setOnClickListener {
            Log.d("Tutoriel", "end")
            finishOnboarding() }

        //Detect changes when we swipe
        pager?.addOnPageChangeListener(object : OnPageChangeListener {
            // This method will be invoked when a new page becomes selected.
            override fun onPageSelected(position: Int) {
                if(position == 6){
                    end?.visibility = View.VISIBLE
                }else{
                    end?.visibility = View.INVISIBLE
                }
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
        /*
        skip?.setOnClickListener { finishOnboarding() }
        next?.setOnClickListener {
            if(pager!!.currentItem < NUM_PAGES)
                pager!!.currentItem = pager!!.currentItem + 1}
        previous?.setOnClickListener {
            if(pager!!.currentItem > 0)
                pager!!.currentItem = pager!!.currentItem - 1}
         */
        indicator?.setupWithViewPager(pager)
    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(
        fm,
        behavior
    ) {
        override fun getCount(): Int = NUM_PAGES

        override fun getItem(position: Int): Fragment{
            Log.d("Tutoriel", position.toString())
            val fragment = ScreenSlidePageFragment()
            val bundle = Bundle()
            bundle.putInt("position", position)
            fragment.arguments = bundle
            return fragment
        }
    }

    private fun finishOnboarding() {
        // Launch the main Activity, called MainActivity
        val main = Intent(applicationContext, MainActivity::class.java)
        startActivity(main)
        SaveSharedPreference.setFirstTime(this, false)
        // Close the com.example.mona.activities.OnboardingActivity
        finish()
    }
}