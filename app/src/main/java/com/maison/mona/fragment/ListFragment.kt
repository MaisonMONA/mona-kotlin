package com.maison .mona.fragment

//import com.example.mona.entity.Lieu
//import com.example.mona.viewmodels.LieuViewModel

import `in`.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.maison.mona.R
import com.maison.mona.adapters.ListAdapter
import com.maison.mona.databinding.FragmentListBinding
import com.maison.mona.entity.Oeuvre
import com.maison.mona.viewmodels.OeuvreViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.maison.mona.data.SaveSharedPreference
import kotlinx.android.synthetic.main.recyclerview_oeuvre.view.*
import org.osmdroid.util.GeoPoint
import java.text.Normalizer
import java.util.*
import kotlin.collections.ArrayList


class ListFragment : Fragment() {

    //view models
    private val oeuvreViewModel: OeuvreViewModel by viewModels()
    //adapter refference
    private lateinit var adapter: ListAdapter
    private var layout: View? = null
    private var position: Parcelable? = null
    private lateinit var recyclerView: IndexFastScrollRecyclerView
    var popupWindow: PopupWindow? = null
    //user location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation = GeoPoint(45.5044372, -73.578502)
    private var iconsStates = arrayListOf(
        arrayListOf(true, true, true),//Oeuvre
        arrayListOf(true, true, true) //Lieu
    )
    private var iconsStatesBack = arrayListOf(
        arrayListOf(true, true, true),//Oeuvre
        arrayListOf(true, true, true) //Lieu
    )
    private var category_index: Int = 0
    private var filter_index: Int = 0

    private var category: String = "Titres"
    private var filter: String = "A-Z"

    private var fromButton = false;

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
        Log.d("Liste", "On Create")
        if(this.layout == null){
            val binding = FragmentListBinding.inflate(inflater, container, false)
            context ?: return binding.root

            //Get a refference to the recyclerView and featured message
            recyclerView = binding.recyclerview
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
            setList(this.category,this.filter)
            return binding.root
        }else{
            return this.layout!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layout = view.findViewById(R.id.recyclerview_container)
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        setList(this.category,this.filter)
        recyclerView.layoutManager?.onRestoreInstanceState(position)
    }

    fun reloadPage() {

    }

    override fun onPause() {
        super.onPause()
       position = recyclerView.layoutManager?.onSaveInstanceState()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.list_sort -> {
                Log.d("Popup", item.toString())
                popupDisplay(layout)
                true
            }
            R.id.list_geo -> {
                //TO-DO
                getUserLocation()
                setList("Titres", "A-Z")
                true
            }
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
        //Gets the location of the user
        userLocation = SaveSharedPreference.getGeoLoc(context)
        if((iconsStates[0][0] or iconsStates[0][1] or iconsStates[0][2])
            && (iconsStates[1][0] or iconsStates[1][1] or iconsStates[1][2])){//Oeuvre and Lieu
            oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { oeuvrelist ->
                filteredList = filterStateList(oeuvrelist)
                sortedList = filterList(filteredList, category, filter)
                adapter.submitList(sortedList,category,userLocation)
            })
        }else if((iconsStates[0][0] or iconsStates[0][1] or iconsStates[0][2])){//Only oeuvre
            oeuvreViewModel.oeuvreTList.observe(viewLifecycleOwner, Observer { oeuvrelist ->
                filteredList = filterStateList(oeuvrelist)
                sortedList = filterList(filteredList, category, filter)
                adapter.submitList(sortedList,category,userLocation)
            })
        } else if((iconsStates[1][0] or iconsStates[1][1] or iconsStates[1][2])) {//Only lieu
            oeuvreViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieulist ->
                filteredList = filterStateList(lieulist)
                sortedList = filterList(filteredList, category, filter)
                adapter.submitList(sortedList,category,userLocation)
            })
        } else{//Empty
            filteredList = listOf()
            adapter.submitList(sortedList,category,userLocation)
        }
    }

    fun getUserLocation(){
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

                    Log.d("Liste", location.toString())
                    if(location != null){
                        Log.d("Liste", "optient location")

                        val geoP = GeoPoint(location.latitude, location.longitude)
                        userLocation = geoP
                        SaveSharedPreference.setGeoLoc(context, geoP)
                    }
                }
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
        setDistances(list)
        //Check for the category
        when(category){
            "Titres" ->{
                if(filter == "A-Z"){
                    currentList = currentList.sortedWith(compareBy(Oeuvre::title))
                    sortedList = addAlphabeticHeaders(currentList as MutableList<Oeuvre>)
                }else{
                    sortedList = addDistanceHeaders(currentList as MutableList<Oeuvre>)
                }
            }
            "Artistes" ->{
                sortedList = addArtistsHeaders(currentList as MutableList<Oeuvre>,filter)
            }
            "Categorie" ->{
                sortedList = addCategoriesHeaders(currentList as MutableList<Oeuvre>,filter)
            }
            "Arrondissements" ->{
                sortedList = addBoroughtHeaders(currentList as MutableList<Oeuvre>,filter)
            }
            else -> {
                sortedList = currentList
            }
        }
        Log.d("Popup", "Liste lenght: " + sortedList.size)
        return sortedList
    }

    fun setDistances(list:List<Oeuvre>){
        userLocation = SaveSharedPreference.getGeoLoc(context)
        for(oeuvre in list) {
            val distance = distance(
                userLocation.latitude,
                userLocation.longitude,
                oeuvre.location!!.lat,
                oeuvre.location!!.lng
            )
            oeuvre.distance = distance
        }
    }

    //Adds the header at the right position in the list
    fun addDistanceHeaders(list: MutableList<Oeuvre>): List<Any>{
        //Creation of mutable list of Interval object where the item and
        // their distance from the user are stored
        Log.d("Liste",list[0].distance.toString())
        val sortedList = list.sortedWith(compareBy(Oeuvre::distance))
        //adding the item to their respectable list sequentially
        var sortedOeuvres = mutableListOf<Any>()
        //Set the distance headers as items at a lesser distance than X
        //var distanceCounter = 0;//initial
        //var distJump = 1//Jump between each header
        var current = -1
        var min = 0
        var max = 9//last header(all items after more than)
        for (oeuvre in sortedList) {
            var distKm = oeuvre.distance//Km
            if (distKm != null) {
                var roundDist = distKm.toInt()
                if(roundDist in (current + 1)..max){
                    current = roundDist
                    sortedOeuvres.add(current.toString() + "Km")
                }
            }
            sortedOeuvres.add(oeuvre)
        }
        return sortedOeuvres
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
    fun addArtistsHeaders(list: MutableList<Oeuvre>,filter: String): List<Any> {
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
                var vSort = listOf<Oeuvre>()
                if(filter == "A-Z"){
                    vSort = v.sortedWith(compareBy(Oeuvre::title))
                }else{
                    vSort = v.sortedWith(compareBy(Oeuvre::distance))
                }
                sortedList.addAll(vSort)
            }
        }
        if(sortedCategoryMap["*"]?.isNotEmpty()!!){
            sortedList.add("* Pas d'artiste *")
            sortedCategoryMap["*"]?.let { sortedList.addAll(it) }
        }
        return sortedList
    }

    //Adds the header at the right position in the list
    fun addBoroughtHeaders(list: MutableList<Oeuvre>,filter: String): List<Any>{
        //Find the list of all categories(names, borought ...)
        var categoryMap: MutableMap<String,MutableList<Oeuvre>> = mutableMapOf();
        categoryMap.put("*", mutableListOf())
        for(item in list){
            if(item.borough.isNullOrEmpty()){
                categoryMap["*"]!!.add(item)
            }else {
                if (!categoryMap.contains(item.borough)) {
                    categoryMap.put(item.borough!!, mutableListOf())
                }
                categoryMap[item.borough]?.add(item)
            }

            }
        val sortedCategoryMap = categoryMap.toSortedMap()
        //Create the list of items
        val sortedList = mutableListOf<Any>()
        for((k,v) in sortedCategoryMap){
            if(k != "*"){
                sortedList.add(k)
                var vSort = listOf<Oeuvre>()
                if(filter == "A-Z"){
                    vSort = v.sortedWith(compareBy(Oeuvre::title))
                }else{
                    vSort = v.sortedWith(compareBy(Oeuvre::distance))
                }
                sortedList.addAll(vSort)
            }
        }
        if(sortedCategoryMap["*"]?.isNotEmpty()!!){
            sortedList.add("* Pas d'arrondissement *")
            sortedCategoryMap["*"]?.let { sortedList.addAll(it) }
        }
        return sortedList
    }

    //Adds the header at the right position in the list
    fun addCategoriesHeaders(list: MutableList<Oeuvre>,filter: String): List<Any>{
        //Find the list of all categories(names, borought ...)
        var categoryMap: MutableMap<String,MutableList<Oeuvre>> = mutableMapOf();
        categoryMap.put("*", mutableListOf())
        for(item in list){
            if(item.category == null || item.category!!.fr.isNullOrEmpty()){
                categoryMap["*"]!!.add(item)
            }else {
                if (!categoryMap.contains(item.category!!.fr)) {
                    categoryMap.put(item.category!!.fr, mutableListOf())
                }
                categoryMap[item.category!!.fr]?.add(item)
            }

        }
        val sortedCategoryMap = categoryMap.toSortedMap()
        //Create the list of items
        val sortedList = mutableListOf<Any>()
        for((k,v) in sortedCategoryMap){
            if(k != "*"){
                sortedList.add(k)
                var vSort = listOf<Oeuvre>()
                if(filter == "A-Z"){
                    vSort = v.sortedWith(compareBy(Oeuvre::title))
                }else{
                    vSort = v.sortedWith(compareBy(Oeuvre::distance))
                }
                sortedList.addAll(vSort)
            }
        }
        if(sortedCategoryMap["*"]?.isNotEmpty()!!){
            sortedList.add("* Pas de catégorie *")
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

    //Manages the filter popup display
    fun popupDisplay(view: View?) {
        //Sets the popup the first time, or return the current one
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
                "Arrondissements"
            )
            var spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                listCategory
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            category_spinner.setAdapter(spinnerAdapter);
            //Set the icons
            var icons = listOf(
                listOf<ImageView>(  list_drawer.findViewById(R.id.oeuvre_normal),
                                    list_drawer.findViewById(R.id.oeuvre_targetted),
                                    list_drawer.findViewById(R.id.oeuvre_collected)),
                listOf<ImageView>(  list_drawer.findViewById(R.id.lieu_normal),
                                    list_drawer.findViewById(R.id.lieu_targetted),
                                    list_drawer.findViewById(R.id.lieu_collected))
            )
            icons.forEachIndexed{i,row ->
                row.forEachIndexed{j,icon ->
                    setIconToggle(icons[i][j],i,j)
                }
            }
            //Send the information from the filters when we close the popup
            var filterButton = list_drawer.findViewById<Button>(R.id.filterButton)
            var radioGroup = list_drawer.findViewById<RadioGroup>(R.id.radio_group)
            radioGroup.check(R.id.radio_alphabet)
            this.filter_index = R.id.radio_alphabet
            //When we click the filter button
            filterButton.setOnClickListener {
                //Get info from the radio buttons
                var idCurrent  = radioGroup.checkedRadioButtonId
                this.filter_index = idCurrent
                var radioValue = "None"
                if(idCurrent != -1){//Not needed, just in case
                    var radioButton = radioGroup.findViewById<RadioButton>(idCurrent)
                    radioValue = radioButton.getText().toString()
                    Log.d("Popup", radioValue)
                }
                //Get Spinner value
                var spinnerValue = category_spinner.selectedItem.toString()
                this.category_index = category_spinner.selectedItemPosition

                this.filter = radioValue
                this.category = spinnerValue
                setList(this.category, this.filter)

                this.fromButton = true
                 popupWindow!!.dismiss()
                this.iconsStatesBack[0] = ArrayList(this.iconsStates[0])
                this.iconsStatesBack[1] = ArrayList(this.iconsStates[1])
            }
            //When the popup closes
            popupWindow!!.setOnDismissListener {
                //If the user clicked outside, reset the values
                if (!this.fromButton) {
                    radioGroup.check(this.filter_index)
                    category_spinner.setSelection(this.category_index)
                    this.iconsStates[0] = ArrayList(this.iconsStatesBack[0])
                    this.iconsStates[1] = ArrayList(this.iconsStatesBack[1])

                    icons.forEachIndexed { i, row ->
                        icons.forEachIndexed { j, icon ->
                            setIcon(icons[i][j], i, j)
                        }
                    }

                }
                this.fromButton = false
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

    fun setIcon(view: ImageView, i: Int, j: Int) {
        if (!iconsStates[i][j]) {
            view.setColorFilter(R.color.black)
        } else {
            view.colorFilter = null
        }
    }
}
