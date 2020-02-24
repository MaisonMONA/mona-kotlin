package com.example.mona.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mona.OeuvreListAdapter
import com.example.mona.entity.Oeuvre
import com.example.mona.OeuvreViewModel
import com.example.mona.R
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.fragment_list.view.*

class ListFragment : Fragment() {

    private val oeuvreViewModel : OeuvreViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

/*
        context?.let{
            val adapter = OeuvreListAdapter(it)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(it)

            oeuvreViewModel.oeuvreList.observe(this, Observer{ oeuvrelist ->
                oeuvrelist.let {
                    println(oeuvrelist.size)
                }
            })
        }
*/
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
