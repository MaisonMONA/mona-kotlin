package com.example.mona.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mona.R
import com.example.mona.entity.Oeuvre
import kotlinx.android.synthetic.main.fragment_item.view.*
import kotlinx.android.synthetic.main.recyclerview_item.view.*

class ItemFragment internal constructor(
    oeuvre: Oeuvre
) : Fragment() {

    private var oeuvre: Oeuvre = oeuvre

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_item, container, false)

        //We setup the artowk item in accordance

        rootView.itemTitle.text = oeuvre.title

        rootView.itemBorough.text = oeuvre.borough

        var artistString : String? = "par "

        for(artistIndex in 0 until oeuvre.artists!!.size){
            artistString += oeuvre.artists!!.get(artistIndex).name
        }
        rootView.itemArtist.text = artistString

        var materialString : String? = ""
        for (materialIndex in 0 until oeuvre.materials!!.size){
            materialString += oeuvre.materials!![materialIndex]
            materialString += " "
        }
        rootView.itemMaterial.text = materialString

        var dimensionString : String? = ""
        for (dimensionIndex in 0 until oeuvre.dimension!!.size){
            dimensionString += oeuvre.dimension!![dimensionIndex]
            dimensionString += " "
        }
        rootView.itemDimensions.text = dimensionString

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}
