package com.maison.mona.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.os.Handler
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
import com.maison.mona.entity.Badge

class PopUpManagerFragment : Fragment(){

    private lateinit var mContext: Context
    private var mBadges = mutableListOf<Badge>()
    private lateinit var mNav: NavController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    fun createPopUpsBadges(view: View?, nav: NavController, badges: List<Badge>){
        mBadges = badges as MutableList<Badge>
        mNav = nav

        view?.rootView?.alpha = 0.2F

        // inflate the layout of the popup window
        val inflater = mContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val popupView: View? = inflater?.inflate(R.layout.popup_badge_layout, null)

        // create the popup window
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val popupWindow = PopupWindow(popupView, 800, height, false)

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        handleBadges(view, popupView, popupWindow, mBadges[0])
    }

    @SuppressLint("ClickableViewAccessibility")
    fun handleBadges(view: View?, popupView: View?, popupWindow: PopupWindow, badge: Badge){
        setInfoBadge(popupView, badge)

        val mHandler = Handler()
        mHandler.postDelayed({
            popupView?.setOnTouchListener { _, _ ->
                if(badge == mBadges.last() || mBadges.isEmpty()){
                    popupWindow.dismiss()
                    view?.rootView?.alpha = 1F
                    mNav.popBackStack(R.id.fragmentViewPager_dest,false)
                } else {
                    mBadges.remove(badge)
                    handleBadges(view, popupView, popupWindow, mBadges[0])
                }

                true
            }
        }, 50L)
    }

    fun setInfoBadge(popupView: View?, badge: Badge){
        val textPopUp = popupView?.findViewById<TextView>(R.id.popup_badge_titre_text)
        textPopUp?.text = badge.title_fr

        val descriptionPopUp = popupView?.findViewById<TextView>(R.id.popup_badge_description_text)
        descriptionPopUp?.text = badge.description_fr

        val imagePopUp = popupView?.findViewById<ImageView>(R.id.popup_image_badge)
        imagePopUp?.setImageResource(assignDrawable(badge))
    }

    private fun assignDrawable(badge: Badge): Int {
        val args = badge.optional_args!!
        val goal = badge.goal

        return when {
            args.contains("Côte-des-Neiges") -> { R.drawable.badge_icon_cdn_color }
            args.contains("Ville-Marie") -> { R.drawable.badge_icon_vm_color }
            args.contains("Rosemont") -> { R.drawable.badge_icon_rosemont_color }
            args.contains("Le Plateau") -> { R.drawable.badge_icon_pmr_color }
            args.contains("Le Sud-Ouest") -> { R.drawable.badge_icon_so_color }
            args.contains("Mercier") -> { R.drawable.badge_icon_hochelaga_color }
            args.contains("Rivière-des-Prairies") -> { R.drawable.badge_icon_riviere_des_prairies_color }
            args.contains("Verdun") -> { R.drawable.badge_icon_verdun_color }
            args.contains("Villeray") -> { R.drawable.badge_icon_villeray_color }
            args.contains("Lachine") -> { R.drawable.badge_icon_lachine_color }
            args.contains("LaSalle") -> { R.drawable.badge_icon_lasalle_color }
            args.contains("Ahuntsic") -> { R.drawable.badge_icon_ac_color }
            args.contains("Outremont") -> { R.drawable.badge_icon_outremont_color }

            args.contains("Decorative") -> { R.drawable.badge_icon_art_decoratif_color }
            args.contains("Beaux-Arts") -> { R.drawable.badge_icon_beaux_arts_color }
            args.contains("Murals") -> { R.drawable.badge_icon_murales_color }

            goal == 1 -> { R.drawable.badge_icon_quantite_1_color }
            goal == 3 -> { R.drawable.badge_icon_quantite_3_color }
            goal == 5 -> { R.drawable.badge_icon_quantite_5_color }
            goal == 8 -> { R.drawable.badge_icon_quantite_8_color }
            goal == 10 -> { R.drawable.badge_icon_quantite_10_color }
            goal == 15 -> { R.drawable.badge_icon_quantite_15_color }
            goal == 20 -> { R.drawable.badge_icon_quantite_20_color }
            goal == 25 -> { R.drawable.badge_icon_quantite_25_color }
            goal == 30 -> { R.drawable.badge_icon_quantite_30_color }

            args.contains("collection") -> { R.drawable.badge_icon_udem_color }

            else -> R.drawable.badge_icon_verdun_color
        }
    }
}