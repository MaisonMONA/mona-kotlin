package com.example.mona.fragment

import android.content.ClipData
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mona.OeuvreListAdapter
import com.example.mona.OeuvreViewModel
import com.example.mona.R

class ListFragment : Fragment() {

    private val oeuvreViewModel : OeuvreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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

            // parentFragmentManager is the same as activity?.supportFragmentManager
            parentFragmentManager.beginTransaction()
                .replace(R.id.recyclerview_container, ItemFragment(oeuvre))
                .addToBackStack(null) //Makes it possible to comeback to recyclerview
                .commit()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}
