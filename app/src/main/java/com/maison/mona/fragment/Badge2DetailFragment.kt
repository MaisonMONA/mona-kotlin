package com.maison.mona.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.maison.mona.R
import com.maison.mona.entity.Badge_2

class Badge2DetailFragment(badge: Badge_2?): Fragment(R.layout.badge_detail) {

    private lateinit var mContext: Context
    private var mBadge = badge

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.badge_detail, container, false)

        view.findViewById<TextView>(R.id.badge_detail_text).setText(mBadge?.title_fr)
        view.findViewById<TextView>(R.id.badge_detail_description).setText(mBadge?.description_fr)

        if(mBadge?.isCollected == true){
            view.findViewById<ImageView>(R.id.badge_detail_image).setImageResource(R.drawable.verdun_color)
        } else{
            view.findViewById<ImageView>(R.id.badge_detail_image).setImageResource(R.drawable.verdun_grey)
        }

        return view
    }

}