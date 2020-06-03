package com.example.mona.fragment

import android.location.Location
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mona.R
import com.example.mona.adapters.ListAdapter
import com.example.mona.databinding.FragmentListBinding
import com.example.mona.entity.Interval
import com.example.mona.entity.Lieu
import com.example.mona.entity.Oeuvre
import com.example.mona.viewmodels.LieuViewModel
import com.example.mona.viewmodels.OeuvreViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*
import kotlin.random.Random

class ListFragment : Fragment() {

    //view models
    private val oeuvreViewModel: OeuvreViewModel by viewModels()
    private val lieuViewModel: LieuViewModel by viewModels()

    //adapter refference
    private lateinit var adapter: ListAdapter

    //user location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userLocation : Location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialize location agent
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

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

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.featured -> {
                itemsOfTheWeek()
                true
            }
            R.id.oeuvre_id -> {
                oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { oeuvrelist ->
                    oeuvrelist?.let { adapter.submitList(it) }
                })

                true
            }
            R.id.oeuvre_alphabetical -> {
                oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { oeuvrelist ->
                    val sortedList =
                        oeuvrelist.sortedWith(compareBy(Oeuvre::title, Oeuvre::borough))
                    sortedList?.let { adapter.submitList(it) }
                })
                true
            }
            R.id.oeuvre_borough -> {
                oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { oeuvrelist ->
                    val sortedList =
                        oeuvrelist.sortedWith(compareBy(Oeuvre::borough, Oeuvre::title))
                    sortedList?.let { adapter.submitList(it) }
                })
                true
            }
            R.id.oeuvre_distance -> {

                oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { oeuvreList ->

                    //Creation of mutable list of Interval object where the item and
                    // their distance from the user are stored
                    var distanceList = mutableListOf<Interval>()

                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location->
                            if (location != null) {
                                userLocation = location

                                for (oeuvre in oeuvreList) {
                                    val distance = distance(userLocation.latitude, userLocation.longitude, oeuvre.location!!.lat, oeuvre.location!!.lng)
                                    distanceList.add(Interval(distance, oeuvre))
                                }

                                //Sort objects depending on their distance attribute
                                val sortedList = distanceList.sortedWith(compareBy(Interval::distance))

                                //adding the item to their respectable list sequentially
                                var sortedOeuvres = mutableListOf<Oeuvre>()

                                for (data in sortedList) {
                                    sortedOeuvres.add(data.item as Oeuvre)
                                }

                                sortedOeuvres?.let { adapter.submitList(it) }
                            }

                        }
                })
                true
            }
            R.id.lieu_id -> {
                lieuViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieulist ->
                    lieulist?.let { adapter.submitList(it) }
                })
                true
            }
            R.id.lieu_alphabetical -> {
                lieuViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieuList ->
                    val sortedList = lieuList.sortedWith(compareBy(Lieu::title, Lieu::borough))
                    sortedList?.let { adapter.submitList(it) }
                })
                true
            }
            R.id.lieu_borough -> {
                lieuViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieuList ->
                    val sortedList = lieuList.sortedWith(compareBy(Lieu::borough, Lieu::title))
                    sortedList?.let { adapter.submitList(it) }
                })
                true
            }
            R.id.lieu_distance -> {


                lieuViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieuList ->
                    //Creation of mutable list of Interval object where the item and
                    // their distance from the user are stored
                    var distanceList = mutableListOf<Interval>()

                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location->
                            if (location != null) {
                                userLocation = location

                                for (lieu in lieuList) {
                                    val distance =
                                        distance(userLocation.latitude, userLocation.longitude, lieu.location!!.lat, lieu.location!!.lng)
                                    distanceList.add(Interval(distance, lieu))
                                }

                                //Sort objects depending on their distance attribute
                                val sortedList = distanceList.sortedWith(compareBy(Interval::distance))

                                //adding the item to their respectable list sequentially
                                var sortedLieux = mutableListOf<Lieu>()

                                for (data in sortedList) {
                                    sortedLieux.add(data.item as Lieu)
                                }

                                sortedLieux?.let { adapter.submitList(it) }
                            }

                        }

                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun itemsOfTheWeek() {

        val weeklyIndex = Calendar.WEEK_OF_YEAR

        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { oeuvreList ->
            var featured_oeuvre = emptyList<Oeuvre>()

            for (num in 1..20) {
                val index = weeklyIndex + 7 * num
                featured_oeuvre = featured_oeuvre + oeuvreList.get(index)
            }
            lieuViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieuList ->
                var featured_lieu = emptyList<Lieu>()

                for (num in 1..20) {
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


    fun distance(
        fromLat: Double,
        fromLon: Double,
        toLat: Double,
        toLon: Double
    ): Double {
        val radius = 6378137.0 // approximate Earth radius, *in meters*
        val deltaLat = toLat - fromLat
        val deltaLon = toLon - fromLon
        val angle = 2 * Math.asin(
            Math.sqrt(
                Math.pow(Math.sin(deltaLat / 2), 2.0) +
                        Math.cos(fromLat) * Math.cos(toLat) *
                        Math.pow(Math.sin(deltaLon / 2), 2.0)
            )
        )
        return radius * angle
    }

    fun getLastKnownLocation() {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                    userLocation = location
                }

            }

    }
}
