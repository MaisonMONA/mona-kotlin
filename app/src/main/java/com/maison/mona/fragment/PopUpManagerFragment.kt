package com.maison.mona.fragment

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import com.maison.mona.R

class PopUpManagerFragment() : Fragment(){

    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("BADGES","onAttach " + context.toString())
        mContext = context
    }

    fun onButtonShowPopupWindowClick(view: View?) {

        // inflate the layout of the popup window
        val inflater = mContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val popupView: View? = inflater?.inflate(R.layout.popup_badge_layout, null)

        // create the popup window
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        // dismiss the popup window when touched
        popupView?.setOnTouchListener { v, event ->
            popupWindow.dismiss()
            true
        }
    }
}