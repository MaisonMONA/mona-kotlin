package com.example .mona.fragment

//import com.example.mona.entity.Lieu
//import com.example.mona.viewmodels.LieuViewModel

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mona.R
import com.example.mona.adapters.ListAdapter
import com.example.mona.databinding.FragmentListBinding
import com.example.mona.entity.Interval
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
    private var iconsStates = arrayListOf(
        arrayListOf(true, true, true),//Oeuvre
        arrayListOf(true, true, true)
    )//Lieu
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
        setList("Titres","A-Z");
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
    //Creates a list of items in the spotlight if we want to implement this feature
    /*
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
    }*/
    //Creates menus with sub menues, if we wan to implement this feature
    /*
    fun masterList() {
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
     */
    //Set the main list that will be displayed on screen
    fun setList(category: String, filter: String){
        var filteredList = listOf<Oeuvre>()
        var sortedList: List<Any> = listOf()
        if((iconsStates[0][0] or iconsStates[0][1] or iconsStates[0][2])
            && (iconsStates[1][0] or iconsStates[1][1] or iconsStates[1][2])){//Oeuvre and Lieu
            oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { oeuvrelist ->
                filteredList = filterStateList(oeuvrelist)
                sortedList = filterList(filteredList, category, filter)
                adapter.submitList(sortedList,category)
            })
        }else if((iconsStates[0][0] or iconsStates[0][1] or iconsStates[0][2])){//Only oeuvre
            oeuvreViewModel.oeuvreTList.observe(viewLifecycleOwner, Observer { oeuvrelist ->
                filteredList = filterStateList(oeuvrelist)
                sortedList = filterList(filteredList, category, filter)
                adapter.submitList(sortedList,category)
            })
        } else if((iconsStates[1][0] or iconsStates[1][1] or iconsStates[1][2])) {//Only lieu
            oeuvreViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieulist ->
                filteredList = filterStateList(lieulist)
                sortedList = filterList(filteredList, category, filter)
                adapter.submitList(sortedList,category)
            })
        } else{//Empty
            filteredList = listOf()
            adapter.submitList(sortedList,category)
        }
    }

    fun filterStateList(list: List<Oeuvre>): List<Oeuvre>{
        var filteredList = list
        if(!(iconsStates[0][0]) && !(iconsStates[1][0])){//if we remove the non collected/non targete
            filteredList = filteredList.filter { (it.state != null)}
        }
        if(!(iconsStates[0][1]) && !(iconsStates[1][1])){//if we remove the targeted items
            filteredList = filteredList.filter{it.state != 1}
        }
        if(!(iconsStates[0][2]) && !(iconsStates[1][2])){//if we remove the collected items
            filteredList = filteredList.filter{it.state != 2}
        }
        return filteredList
    }

    //Filter the list of items
    fun filterList(list: List<Oeuvre>, category: String, filter: String): List<Any>{
        var currentList = list
        var sortedList = listOf<Any>()
        //Check for the category
        when(category){
            "Titres" -> {
                currentList = currentList.sortedWith(compareBy(Oeuvre::title))
            }
            "Artistes" -> {
                currentList = currentList.sortedWith(compareBy {
                    if (it.artists != null && it.artists?.size!! > 0) {
                        it.artists!!.first().name
                    } else {
                        ""
                    }
                })
            }
            "Categorie" -> {
                currentList = currentList.sortedWith(compareBy {
                    if (it.category != null && it.category?.fr?.length ?: 0 > 0) {
                        it.category!!.fr
                    } else {
                        ""
                    }
                })
            }
            "Arrondissements" -> {
                currentList = currentList.sortedWith(compareBy(Oeuvre::borough))
            }
            "Materiaux" -> {
                currentList = currentList.sortedWith(compareBy(Oeuvre::title))
            }
            "Techniques" -> {
                currentList = currentList.sortedWith(compareBy(Oeuvre::title))
            }
            else ->{currentList = currentList.sortedWith(compareBy(Oeuvre::title))}
        }
        //Check for the filter
        if(filter == "A-Z"){
            when(category){
                "Titres" -> {
                    sortedList = addAlphabeticHeaders(currentList as MutableList<Oeuvre>)
                }
                "Artistes" -> {
                    sortedList = addArtistsHeaders(currentList as MutableList<Oeuvre>)
                }
                else ->{}
            }
        }else if(filter == "Distance"){
            addDistanceHeaders(currentList as MutableList<Oeuvre>,adapter)
        }else{//Not needed, but keeping it just in case
            sortedList = currentList
        }
        Log.d("Popup", "Liste lenght: " + currentList.size)
        return sortedList
    }

    //Adds the header at the right position in the list
    fun addDistanceHeaders(list: MutableList<Oeuvre>,adapter:ListAdapter){
        //Creation of mutable list of Interval object where the item and
        // their distance from the user are stored
        var distanceList = mutableListOf<Interval>()
        Log.d("Liste", "Wait for location")
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //Do not have permission
        }else{
             // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = location
                    Log.d("Liste", userLocation.latitude.toString() + " " + userLocation.longitude)
                    for (oeuvre in list) {
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
                    var sortedOeuvres = mutableListOf<Any>()
                    //Set the distance headers as items at a lesser distance than X
                    var distanceCounter = 0;//initial
                    var distJump = 1//Jump between each header
                    var max = 10//last header(all items after more than)
                    for (data in sortedList) {
                        var item = data.item as Oeuvre
                        var distKm = data.distance//Km to meters
                        if (distKm != null) {
                            if(distKm <= max && distKm >= distanceCounter){
                                sortedOeuvres.add(distanceCounter.toString() + " km")
                                distanceCounter += distJump
                            }
                        }
                        sortedOeuvres.add(item)
                    }
                    adapter.submitList(sortedOeuvres,"distance")
                }
            }
        }
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
    //Adds the header at the right position in the list
    fun addArtistsHeaders(list: MutableList<Oeuvre>): List<Any> {
        //Find the list of all categories(names, borought ...)
        var categoryMap: MutableMap<String,MutableList<Oeuvre>> = mutableMapOf();
        categoryMap.put("*", mutableListOf())
        for(item in list){
            if(item.artists.isNullOrEmpty()){
                categoryMap["*"]!!.add(item)
            }else {
                for(artist in item.artists!!){
                    //Because we might have a name == ""
                    if(!artist.name.isBlank()) {
                        if (!categoryMap.contains(artist.name)) {
                            categoryMap.put(artist.name, mutableListOf())
                        }
                        categoryMap[artist.name]?.add(item)
                    }
                }
            }
        }
       // val sortedCategoryMap = categoryMap.toSortedMap(compareBy<String>{unaccent(it).first().toUpperCase()});
        val sortedCategoryMap = categoryMap.toSortedMap()
        //Create the list of items
        val sortedList = mutableListOf<Any>()
        for((k,v) in sortedCategoryMap){
            if(k != "*"){
                sortedList.add(k)
                sortedList.addAll(v)
            }
        }
        if(sortedCategoryMap["*"]?.isNotEmpty()!!){
            sortedList.add("* Pas d'artiste *")
            sortedCategoryMap["*"]?.let { sortedList.addAll(it) }
        }
        return sortedList
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
        var p = 0.017453292519943295;    // Math.PI / 180
        var a = 0.5 - Math.cos((toLat - fromLat) * p)/2 +
                Math.cos(fromLat * p) * Math.cos(toLat * p) *
                (1 - Math.cos((toLon - fromLon) * p))/2;

        return 12742 * Math.asin(Math.sqrt(a)); // 2 * R; R = 6371 km
    }

    fun getLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location->
                if (location != null) {
                    this.userLocation = location
                }

            }

    }
    //Manages the filter popup display
    fun popupDisplay(view: View) {
        if(popupWindow == null){
            Log.d("Popup", "Create Popup")
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
            setIconToggle(list_drawer.findViewById(R.id.oeuvre_normal), 0, 0)
            setIconToggle(list_drawer.findViewById(R.id.oeuvre_targetted), 0, 1)
            setIconToggle(list_drawer.findViewById(R.id.oeuvre_collected), 0, 2)
            setIconToggle(list_drawer.findViewById(R.id.lieu_normal), 1, 0)
            setIconToggle(list_drawer.findViewById(R.id.lieu_targetted), 1, 1)
            setIconToggle(list_drawer.findViewById(R.id.lieu_collected), 1, 2)
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
                setList(spinnerValue, radioValue)
            }
        }
        popupWindow!!.showAtLocation(layout, Gravity.TOP or Gravity.END, 0, 0);
    }
    fun setIconToggle(view: ImageView, i: Int, j: Int) {
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
