package com.maison.mona.fragment

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import com.maison.mona.R

class PopUpManagerFragment() : Fragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun onButtonShowPopupWindowClick(view: View?) {
        // inflate the layout of the popup window
        val inflater = activity?.layoutInflater
        //val inflater = context?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val popupView: View? = inflater?.inflate(R.layout.popup_badge_layout, null)

        Log.d("BADGES", inflater.toString())

        // create the popup window
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        // dismiss the popup window when touched
//        popupView?.setOnTouchListener { v, event ->
//            popupWindow.dismiss()
//            true
//        }
    }

//    override fun requireContext(): Context {
//        return this.context
//            ?: throw IllegalStateException("Fragment $this not attached to a context.")
//    }
}