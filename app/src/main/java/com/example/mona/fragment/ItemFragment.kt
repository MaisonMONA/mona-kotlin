package com.example.mona.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mona.R
import com.example.mona.entity.Oeuvre
import kotlinx.android.synthetic.main.fragment_item.view.*
import kotlinx.android.synthetic.main.recyclerview_item.view.*

class ItemFragment () : Fragment() {

    val safeArgs : ItemFragmentArgs by navArgs()


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

        var dimensionString : String? = ""
        for (dimensionIndex in 0 until oeuvre.dimension!!.size){
            dimensionString += oeuvre.dimension!![dimensionIndex]
            dimensionString += " "
        }
        view.itemDimensions.text = dimensionString

        view.itemCategory.text = oeuvre.category?.fr
        view.itemSubcategory.text = oeuvre.subcategory?.fr

        //Button map
        view.findViewById<ImageButton>(R.id.button_map)?.setOnClickListener {
            val action = ItemFragmentDirections.openItemMap(oeuvre)
            findNavController().navigate(action)
        }


    }

}
