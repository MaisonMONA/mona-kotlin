package com.example.mona.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mona.OeuvreViewModel
import com.example.mona.R
import com.example.mona.entity.Oeuvre
import kotlinx.android.synthetic.main.fragment_oeuvre_jour.view.*
import java.util.*


class OeuvreJourFragment : Fragment() {

    private val oeuvreViewModel : OeuvreViewModel by viewModels()
    private lateinit var odj: Oeuvre

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //https://stackoverflow.com/questions/51043428/handling-back-button-in-android-navigation-component
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //do nothing
            }
        }
        callback.isEnabled = true
        requireActivity().onBackPressedDispatcher.addCallback(this@OeuvreJourFragment, callback)
    }



    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        var rootView = inflater.inflate(R.layout.fragment_oeuvre_jour, container, false)


        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer{ oeuvrelist ->
            val calendar = Calendar.getInstance()
            val dayOfYear = calendar[Calendar.DAY_OF_YEAR]

            odj = oeuvrelist[dayOfYear]

            rootView.odj_title.text = odj?.title

            if(odj?.artists?.size == 0){
                rootView.odj_artist_and_year.text = odj.produced_at?.substring(0,4)
            } else {
                rootView.odj_artist_and_year.text = odj?.artists?.get(0)?.name + ", "+ odj?.produced_at?.substring(0,4)
            }
            /*
            var dimensionString : String? = ""
            for (dimensionIndex in 0 until odj?.dimension!!.size){
                dimensionString += odj.dimension!![dimensionIndex]
                dimensionString += " "
            }


            rootView.odj_dimensions.text = dimensionString

            */

            rootView.odj_category.text = odj?.category?.fr
            rootView.odj_subcategory.text = odj?.subcategory?.fr

            rootView.findViewById<ImageButton>(R.id.button_map_odj)?.setOnClickListener {
                val action = OeuvreJourFragmentDirections.openOdjMap(odj)
                findNavController().navigate(action)

            }

            rootView.findViewById<ImageButton>(R.id.button_cam_odj)?.setOnClickListener {
                val action = OeuvreJourFragmentDirections.odjToRating(odj)
                findNavController().navigate(action)
            }


        })

        return rootView

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }


}
