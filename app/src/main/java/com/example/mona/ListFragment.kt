package com.example.mona

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

class ListFragment : Fragment() {

    private val oeuvreViewModel by activityViewModels<OeuvreViewModel>()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        oeuvreViewModel.oeuvreList?.observe(this, Observer<List<Oeuvre>>{ oeuvrelist ->
            if ( oeuvrelist.size > 0) {
                for(oeuvre in oeuvrelist){
                    println(oeuvre.title)
                }
            }
        })


    }
}
