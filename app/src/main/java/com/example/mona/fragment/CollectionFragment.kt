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
import com.example.mona.CollectionAdapter
import com.example.mona.OeuvreViewModel
import com.example.mona.R
import com.example.mona.entity.Oeuvre

//TODO implementation of cardview
//https://developer.android.com/guide/topics/ui/layout/cardview

class CollectionFragment : Fragment() {

    private val oeuvreViewModel : OeuvreViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_collection, container, false)

        val recyclerView = rootView.findViewById<RecyclerView>(R.id.collection_recyclerview)

        val adapter = CollectionAdapter(context)

        recyclerView.adapter = adapter

        adapter.onItemClick = {oeuvre ->
        }

        recyclerView.layoutManager = LinearLayoutManager(context)

        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer {
            val sortedList = it.filter { oeuvre -> oeuvre.state == 2 }
            adapter.submitList(sortedList)
        })
        
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
