package com.example.mona.activities

import ScreenSlidePageFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager

import com.example.mona.R
import com.example.mona.data.SaveSharedPreference
import com.google.android.material.tabs.TabLayout
import com.ogaclejapan.smarttablayout.SmartTabLayout
import kotlinx.android.synthetic.main.activity_onboarding.*

private const val NUM_PAGES = 5

class OnboardingActivity : AppCompatActivity() {
    private var pager: ViewPager? = null
    private var indicator: TabLayout? = null
    private var skip: Button? = null
    private var next: Button? = null
    private var previous: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding);
        Log.d("TUTORIAL", "Strart tutorial")
        pager = findViewById(R.id.pager);
        indicator = findViewById(R.id.indicator);
        skip = findViewById(R.id.skip);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);

        val pagerAdapter = ScreenSlidePagerAdapter(supportFragmentManager)
        pager?.adapter = pagerAdapter
        skip?.setOnClickListener { finishOnboarding() }
        next?.setOnClickListener {
            if(pager!!.currentItem < NUM_PAGES)
                pager!!.currentItem = pager!!.currentItem + 1}
        previous?.setOnClickListener {
            if(pager!!.currentItem > 0)
                pager!!.currentItem = pager!!.currentItem - 1}
        indicator?.setupWithViewPager(pager)

    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = NUM_PAGES

        override fun getItem(position: Int): Fragment{
            val fragment = ScreenSlidePageFragment()
            val bundle = Bundle()
            bundle.putInt("position",position)
            fragment.arguments = bundle
            return fragment
        }
    }

    private fun finishOnboarding() {
        // Launch the main Activity, called MainActivity
        //val main = Intent(applicationContext, MainActivity::class.java)
        //startActivity(main)
        SaveSharedPreference.setFirstTime(this,false)
        // Close the com.example.mona.activities.OnboardingActivity
        finish()
    }
}