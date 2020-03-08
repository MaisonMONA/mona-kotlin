package com.example.mona.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mona.R
import kotlinx.android.synthetic.main.fragment_item.view.*


class ItemFragment () : Fragment() {

    val safeArgs : ItemFragmentArgs by navArgs()
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_item, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val oeuvre  = safeArgs.itemSelected

        //We setup the artowk item in accordance
        val title_and_year = oeuvre?.title + ", " + oeuvre?.produced_at?.substring(0,4)

        view.itemTitleYear.text = title_and_year

        var artistString : String? = ""

        for(artistIndex in 0 until oeuvre?.artists!!.size){
            artistString += oeuvre.artists!!.get(artistIndex).name
        }
        view.itemArtist.text = artistString
/*
        var dimensionString : String? = ""
        for (dimensionIndex in 0 until oeuvre.dimension!!.size){
            dimensionString += oeuvre.dimension!![dimensionIndex]
            dimensionString += " "
        }

        view.itemDimensions.text = dimensionString
*/
        view.itemCategory.text = oeuvre.category?.fr
        view.itemSubcategory.text = oeuvre.subcategory?.fr

        //the artwork state is collected
        //display displayed artwork
        if(oeuvre?.state == 2){
            val button_layout = view.findViewById<LinearLayout>(R.id.itemButtons)
            button_layout.layoutParams.height = 0

            val item_comment = view.findViewById<TextView>(R.id.itemComment)
            item_comment.text = oeuvre.comment
            item_comment.visibility = View.VISIBLE

            val item_rating = view.findViewById<RatingBar>(R.id.itemRating)
            item_rating.rating = oeuvre.rating!!.toFloat()
            item_rating.visibility = View.VISIBLE

            val itemDate = view.findViewById<TextView>(R.id.itemDate)
            itemDate.text = "Prise le "+oeuvre.date_photo


            var item_picture = view.findViewById<ImageView>(R.id.itemImage)
            var picture = Drawable.createFromPath(oeuvre.photo_path)
            item_picture.setImageDrawable(picture)
            item_picture.rotation = 90.0f

            //Reproportion the layout weight
            //val upperHalfView = view.findViewById<LinearLayout>(R.id.upper_half)
            //(upperHalfView.layoutParams as LinearLayout.LayoutParams).weight = 0.7f


        }else{

            //Button map
            view.findViewById<ImageButton>(R.id.button_map)?.setOnClickListener {
                val action = ItemFragmentDirections.openItemMap(oeuvre)
                findNavController().navigate(action)
            }

            view.findViewById<ImageButton>(R.id.button_cam)?.setOnClickListener {

                val action = ItemFragmentDirections.itemToRating(oeuvre)
                findNavController().navigate(action)
            }

        }



    }

    override fun onResume() {
        super.onResume()

    }


}
