package com.maison.mona.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.maison.mona.R
import com.maison.mona.databinding.PopupBadgeLayoutBinding
import com.maison.mona.entity.Badge_2
import org.w3c.dom.Text


class PopUpManagerFragment() : Fragment(){

    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    @SuppressLint("ClickableViewAccessibility")
    fun onButtonShowPopupWindowClick(view: View?, nav: NavController, badge: Badge_2) {
        Log.d("BADGES", view?.rootView?.alpha.toString())
        view?.rootView?.alpha = 0.2F

        // inflate the layout of the popup window
        val inflater = mContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val popupView: View? = inflater?.inflate(R.layout.popup_badge_layout, null)

        // create the popup window
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = false // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        val textPopUp = popupView?.findViewById<TextView>(R.id.popup_badge_titre_text)
        textPopUp?.setText(badge.title_fr)

        val imagePopUp = popupView?.findViewById<ImageView>(R.id.popup_image_badge)
        imagePopUp?.setImageResource(R.drawable.verdun_color)

        val descriptionPopUp = popupView?.findViewById<TextView>(R.id.popup_badge_description_text)
        descriptionPopUp?.setText(badge.description_fr)

//        binding.badge = badge

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        // dismiss the popup window when touched
        popupView?.setOnTouchListener { v, event ->
            popupWindow.dismiss()
            view?.rootView?.alpha = 1F
            nav.popBackStack(R.id.fragmentViewPager_dest,false)
            true
        }
    }
}