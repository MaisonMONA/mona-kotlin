package com.example.mona.fragment

//import com.example.mona.entity.Lieu
//import com.example.mona.viewmodels.LieuViewModel

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.location.Location
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mona.R
import com.example.mona.adapters.ListAdapter
import com.example.mona.databinding.FragmentListBinding
import com.example.mona.entity.Oeuvre
import com.example.mona.viewmodels.OeuvreViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.Normalizer
import java.util.*
import kotlin.random.Random


class ListFragment : Fragment() {

    //view models
    private val oeuvreViewModel: OeuvreViewModel by viewModels()
    //adapter refference
    private lateinit var adapter: ListAdapter
    private lateinit var layout: View
    var popupWindow: PopupWindow? = null
    //user location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userLocation : Location
    private var iconsStates = arrayListOf( arrayListOf(true,true,true),//Oeuvre
                                           arrayListOf(true,true,true))//Lieu
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
        recyclerView.setIndexBarVisibility(true)
        //Create adapter
        adapter = ListAdapter(
            context,
            findNavController()
        )
        //Set a adapter
        recyclerView.adapter = adapter
        //Set a layout manager
        recyclerView.layoutManager = LinearLayoutManager(context)
        //Create the lists for the headers and the sub lists
        masterList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layout = view.findViewById(R.id.recyclerview_container)
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("Popup", item.toString())
        popupDisplay(layout)
        return when (item.itemId) {
            /*
            R.id.featured -> {
                itemsOfTheWeek()
                true
            }

            R.id.oeuvre_id -> {
                oeuvreViewModel.oeuvreTList.observe(viewLifecycleOwner, Observer { oeuvrelist ->
                    oeuvrelist?.let { adapter.submitList(it) }
                })

                true
            }
            R.id.oeuvre_alphabetical -> {
                oeuvreViewModel.oeuvreTList.observe(viewLifecycleOwner, Observer { oeuvrelist ->
                    val sortedList =
                        oeuvrelist.sortedWith(compareBy(Oeuvre::title, Oeuvre::borough))
                    sortedList.let { adapter.submitList(it) }
                })
                true
            }
            R.id.oeuvre_borough -> {
                oeuvreViewModel.oeuvreTList.observe(viewLifecycleOwner, Observer { oeuvrelist ->
                    val sortedList =
                        oeuvrelist.sortedWith(compareBy(Oeuvre::borough, Oeuvre::title))
                    sortedList.let { adapter.submitList(it) }
                })
                true
            }
            R.id.lieu_id -> {
                oeuvreViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieulist ->
                    lieulist?.let { adapter.submitList(it) }
                })

                true
            }
            R.id.lieu_alphabetical -> {
                oeuvreViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieulist ->
                    val sortedList =
                        lieulist.sortedWith(compareBy(Oeuvre::title, Oeuvre::borough))
                    sortedList.let { adapter.submitList(it) }
                })
                true
            }
            R.id.lieu_borough -> {
                oeuvreViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieulist ->
                    val sortedList =
                        lieulist.sortedWith(compareBy(Oeuvre::borough, Oeuvre::title))
                    sortedList.let { adapter.submitList(it) }
                })
                true
            }

            R.id.oeuvre_distance -> {

                oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { oeuvreList ->

                    //Creation of mutable list of Interval object where the item and
                    // their distance from the user are stored
                    var distanceList = mutableListOf<Interval>()

                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location ->
                            if (location != null) {
                                userLocation = location

                                for (oeuvre in oeuvreList) {
                                    val distance = distance(
                                        userLocation.latitude,
                                        userLocation.longitude,
                                        oeuvre.location!!.lat,
                                        oeuvre.location!!.lng
                                    )
                                    distanceList.add(Interval(distance, oeuvre))
                                }

                                //Sort objects depending on their distance attribute
                                val sortedList =
                                    distanceList.sortedWith(compareBy(Interval::distance))

                                //adding the item to their respectable list sequentially
                                var sortedOeuvres = mutableListOf<Oeuvre>()

                                for (data in sortedList) {
                                    sortedOeuvres.add(data.item as Oeuvre)
                                }

                                sortedOeuvres.let { adapter.submitList(it) }
                            }

                        }
                })
                true
            }

             */
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

            var temp = featured_oeuvre

            var header = listOf("")

            var featured_list = header + temp.shuffled(Random(weeklyIndex))

            adapter.submitList(featured_list)

        })
    }
    fun masterList(){
        val rootList = listOf<String>("Oeuvres", "Lieux")

        val lists = mutableMapOf<String, List<Any>>(
            "Oeuvres" to listOf<String>(
                "Titres", "Artistes", "Categorie", "Arrondissements", "Materiaux", "Techiques"
            ),
            "Titres" to emptyList(),
            "Artistes" to emptyList(),
            "Categorie" to emptyList(),
            "Arrondissements" to emptyList(),
            "Materiaux" to emptyList(),
            "Techniques" to emptyList()
        )
        adapter.submitMasterList(lists)
        adapter.submitRootList(rootList)
        adapter.submitList(rootList)

        oeuvreViewModel.oeuvreTList.observe(viewLifecycleOwner, Observer { oeuvrelist ->
            var sortedList =
                oeuvrelist.sortedWith(compareBy(Oeuvre::title, Oeuvre::borough))
            var headerList = addAlphabeticHeaders(sortedList as MutableList<Oeuvre>)
            oeuvrelist?.let { adapter.submitSubList("Titres", headerList) }
        })

    }
    fun setList(category: String, filter: String){
        if((iconsStates[0][0] or iconsStates[0][1] or iconsStates[0][2])
            && (iconsStates[1][0] or iconsStates[1][1] or iconsStates[1][2])){//Oeuvre and Lieu
            oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner,Observer{oeuvrelist ->
                var sortedList = filterList(oeuvrelist,category,filter)
                adapter.submitList(sortedList)
            })
        }else if((iconsStates[0][0] or iconsStates[0][1] or iconsStates[0][2])){//Only oeuvre
            oeuvreViewModel.oeuvreTList.observe(viewLifecycleOwner,Observer{oeuvrelist ->
                var sortedList = filterList(oeuvrelist,category,filter)
                adapter.submitList(sortedList)
            })
        } else if((iconsStates[1][0] or iconsStates[1][1] or iconsStates[1][2])) {//Only lieu
            oeuvreViewModel.lieuList.observe(viewLifecycleOwner,Observer{lieulist ->
                var sortedList = filterList(lieulist,category,filter)
                adapter.submitList(sortedList)
            })
        }
    }
    fun filterList(list:List<Oeuvre>,category: String,filter: String): List<Any>{
        var currentList = list
        var sortedList = listOf<Any>()
        //Check for the category
        //Check for the filter
        if(filter == "A-Z"){
            currentList = list.sortedWith(compareBy(Oeuvre::title,Oeuvre::borough))
            sortedList = addAlphabeticHeaders(currentList as MutableList<Oeuvre>)
        }else if(filter == "Distance"){
            sortedList = list.sortedWith( compareBy(Oeuvre::borough))
        }else{
            sortedList = currentList
        }
        return sortedList
    }
    //Adds the header at the right position in the list
    fun addAlphabeticHeaders(list: MutableList<Oeuvre>): List<Any>{
        var normalList = list.filter{ it.title!!.first().isLetter()}
        var specialList = list.filter{ !(it.title!!.first().isLetter()) }

        var alphabetMap = normalList.groupBy { unaccent(it.title!!).first().toUpperCase()}
        var listAlphabet = mutableListOf<Any>()
       alphabetMap.forEach { (t, u) ->
           listAlphabet.add(t.toString())
           listAlphabet.addAll(u)
        }
        listAlphabet.add("*")
        listAlphabet.addAll(specialList)
        return listAlphabet
    }

    fun unaccent(src: String): String {
        return Normalizer
            .normalize(src, Normalizer.Form.NFD)
            .replace("[^\\p{ASCII}]".toRegex(), "")
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

    fun popupDisplay(view: View) {
        if(popupWindow == null){
            Log.d("Popup","Create Popup")
            val list_drawer = layoutInflater.inflate(R.layout.list_menu_drawer, null)
            popupWindow = PopupWindow(
                list_drawer,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            popupWindow!!.isOutsideTouchable = true
            //popupWindow.showAsDropDown(view)
            var category_spinner = list_drawer.findViewById<Spinner>(R.id.category_spinner)
            var listCategory = listOf(
                "Titres",
                "Artistes",
                "Categorie",
                "Arrondissements",
                "Materiaux",
                "Techiques"
            )
            var spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                listCategory
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            category_spinner.setAdapter(spinnerAdapter);
            //Set the icons
            setIconToggle(list_drawer.findViewById(R.id.oeuvre_normal),0,0)
            setIconToggle(list_drawer.findViewById(R.id.oeuvre_targetted),0,1)
            setIconToggle(list_drawer.findViewById(R.id.oeuvre_collected),0,2)
            setIconToggle(list_drawer.findViewById(R.id.lieu_normal),1,0)
            setIconToggle(list_drawer.findViewById(R.id.lieu_targetted),1,1)
            setIconToggle(list_drawer.findViewById(R.id.lieu_collected),1,2)
            //Send the information from the filters when we close the popup
            popupWindow!!.setOnDismissListener {
                //Get info from the radio buttons
                var radioGroup = list_drawer.findViewById<RadioGroup>(R.id.radio_group)
                var idCurrent  = radioGroup.checkedRadioButtonId
                var radioValue = "None"
                if(idCurrent != -1){
                    var radioButton = radioGroup.findViewById<RadioButton>(idCurrent)
                    radioValue = radioButton.getText().toString()
                    Log.d("Popup", radioValue)
                }
                //Get Spinner value
                var spinnerValue = category_spinner.selectedItem.toString()
                Log.d("Popup", spinnerValue)
                setList(spinnerValue,radioValue)
            }
        }
        popupWindow!!.showAtLocation(layout, Gravity.TOP or Gravity.END , 0, 0);
    }
    fun setIconToggle(view: ImageView,i:Int,j:Int) {
        view.setOnClickListener {
            if (iconsStates[i][j]) {
                view.setColorFilter(R.color.black)
                iconsStates[i][j] = false
            } else {
                view.colorFilter = null
                iconsStates[i][j] = true
            }
        }
    }
}
