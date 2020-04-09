package com.example.mona.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mona.adapters.OeuvreListAdapter
import com.example.mona.viewmodels.OeuvreViewModel
import com.example.mona.R

class ListFragment : Fragment() {

    private val oeuvreViewModel : OeuvreViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val rootView = inflater.inflate(R.layout.fragment_list, container, false)

        //Get a refference to the recyclerView
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerview)

        //Create adapter
        val adapter = OeuvreListAdapter(context)

        //Set a adapter
        recyclerView.adapter = adapter

        //Give action to item click in adapter
        adapter.onItemClick = { oeuvre ->
            //Setup new fragment Oeuvre
            val action = HomeViewPagerFragmentDirections.homeToItem(oeuvre)
            findNavController().navigate(action)

            println(oeuvre.state)

        }

        //Set a layout manager
        recyclerView.layoutManager = LinearLayoutManager(context)

        //Open LiveData
        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer{ oeuvrelist ->
            //Submit the list to the adapter
            oeuvrelist?.let { adapter.submitList(it) }
        })



        return rootView
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}
