package com.maison.mona.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.maison.mona.adapters.CollectionAdapter
import com.maison.mona.viewmodels.OeuvreViewModel
import com.maison.mona.databinding.FragmentCollectionBinding
//import com.example.mona.viewmodels.LieuViewModel
import kotlinx.android.synthetic.main.fragment_collection.view.*

//TODO implementation of cardview
//https://developer.android.com/guide/topics/ui/layout/cardview

class CollectionFragment : Fragment() {

    private val oeuvreViewModel : OeuvreViewModel by viewModels()
    //private val lieuViewModel : LieuViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding = FragmentCollectionBinding.inflate(inflater, container, false)
        context ?: return binding.root

        val recyclerView = binding.collectionRecyclerview

        val adapter = CollectionAdapter(
            context,
            findNavController()
        )

        recyclerView.adapter = adapter

        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { oeuvreList->
            val sortedOeuvres = oeuvreList.filter { (it.state == 2 || it.state == 3) }

            adapter.submitList(sortedOeuvres)


        })
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
