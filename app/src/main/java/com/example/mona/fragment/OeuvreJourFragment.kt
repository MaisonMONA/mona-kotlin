package com.example.mona.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.example.mona.OeuvreViewModel
import com.example.mona.R
import com.example.mona.entity.Oeuvre
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_oeuvre_jour.view.*
import java.util.*

class OeuvreJourFragment : Fragment() {

    private val oeuvreViewModel : OeuvreViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        var rootView = inflater.inflate(R.layout.fragment_oeuvre_jour, container, false)
        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer{ oeuvrelist ->
            //Since the database takes a longer moment to load on the back thread
            if(oeuvrelist.size>0){

                val calendar = Calendar.getInstance()
                val dayOfYear = calendar[Calendar.DAY_OF_YEAR]

                val odj : Oeuvre? = oeuvrelist[dayOfYear]

                rootView.odj_title.text = odj?.title

                if(odj?.artists?.size == 0){
                    rootView.odj_artist_and_year.text = odj.produced_at?.substring(0,4)
                } else {
                    rootView.odj_artist_and_year.text = odj?.artists?.get(0)?.name + ", "+ odj?.produced_at?.substring(0,4)
                }

                var dimensionString : String? = ""
                for (dimensionIndex in 0 until odj?.dimension!!.size){
                    dimensionString += odj.dimension!![dimensionIndex]
                    dimensionString += " "
                }
                rootView.odj_dimensions.text = dimensionString

                rootView.odj_category.text = odj.category?.fr
                rootView.odj_subcategory.text = odj.subcategory?.fr

                rootView.findViewById<ImageButton>(R.id.button_map_odj)?.setOnClickListener {
                    val action = OeuvreJourFragmentDirections.openOdjMap(odj)
                    findNavController().navigate(action)
                }
            }

        })

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }
}
