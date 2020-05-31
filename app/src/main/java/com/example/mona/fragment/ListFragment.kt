package com.example.mona.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mona.R
import com.example.mona.adapters.ListAdapter
import com.example.mona.databinding.FragmentListBinding
import com.example.mona.entity.Lieu
import com.example.mona.entity.Oeuvre
import com.example.mona.viewmodels.LieuViewModel
import com.example.mona.viewmodels.OeuvreViewModel
import java.util.*
import kotlin.random.Random

class ListFragment : Fragment() {

    //view models
    private val oeuvreViewModel : OeuvreViewModel by viewModels()
    private val lieuViewModel : LieuViewModel by viewModels()

    //adapter refference
    private lateinit var adapter: ListAdapter

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding = FragmentListBinding.inflate(inflater, container, false)
        context ?: return binding.root

        //Get a refference to the recyclerView and featured message
        val recyclerView = binding.recyclerview

        //Create adapter
        adapter = ListAdapter(
            context,
            findNavController()
        )

        //Set a adapter
        recyclerView.adapter = adapter

        //Set a layout manager
        recyclerView.layoutManager = LinearLayoutManager(context)

        //Featured items of the week
        itemsOfTheWeek()

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.featured ->{
                itemsOfTheWeek()
                true
            }
            R.id.oeuvre_only -> {
                oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer{ oeuvrelist ->
                    oeuvrelist?.let { adapter.submitList(it) }
                })

                true
            }
            R.id.oeuvre_alphabetical ->{
                oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer{ oeuvrelist ->
                    val sortedList = oeuvrelist.sortedWith(compareBy(Oeuvre::title, Oeuvre::borough))
                    sortedList?.let { adapter.submitList(it) }
                })
                true
            }
            R.id.oeuvre_borough -> {
                oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer{ oeuvrelist ->
                    val sortedList = oeuvrelist.sortedWith(compareBy(Oeuvre::borough, Oeuvre::title))
                    sortedList?.let { adapter.submitList(it) }
                })
                true
            }
            R.id.lieu_only -> {
                lieuViewModel.lieuList.observe(viewLifecycleOwner, Observer{ lieulist ->
                    lieulist?.let { adapter.submitList(it) }
                })
                true
            }
            R.id.lieu_alphabetical -> {
                lieuViewModel.lieuList.observe(viewLifecycleOwner, Observer{ lieuList ->
                    val sortedList = lieuList.sortedWith(compareBy(Lieu::title, Lieu::borough))
                    sortedList?.let { adapter.submitList(it) }
                })
                true
            }
            R.id.lieu_borough ->{
                lieuViewModel.lieuList.observe(viewLifecycleOwner, Observer{ lieuList ->
                    val sortedList = lieuList.sortedWith(compareBy(Lieu::borough, Lieu::title))
                    sortedList?.let { adapter.submitList(it) }
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun itemsOfTheWeek(){

        val weeklyIndex = Calendar.WEEK_OF_YEAR

        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { oeuvreList ->
            var featured_oeuvre = emptyList<Oeuvre>()

            for (num in 1..20){
                val index = weeklyIndex + 7 * num
                featured_oeuvre = featured_oeuvre + oeuvreList.get(index)
            }
            lieuViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieuList ->
                var featured_lieu = emptyList<Lieu>()

                for (num in 1..20){
                    val index = weeklyIndex + 7 * num
                    featured_lieu = featured_lieu + lieuList.get(index)
                }

                var temp = featured_lieu + featured_oeuvre

                var header = listOf("")

                var featured_list = header + temp.shuffled(Random(weeklyIndex))

                adapter.submitList(featured_list)

            })
        })

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    fun getEmojiByUnicode(unicode: Int): String? {
        return String(Character.toChars(unicode))
    }
}
