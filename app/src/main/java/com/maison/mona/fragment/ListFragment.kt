package com.maison.mona.fragment

import `in`.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.maison.mona.R
import com.maison.mona.adapters.ListAdapter
import com.maison.mona.data.SaveSharedPreference
import com.maison.mona.databinding.FragmentListBinding
import com.maison.mona.entity.Oeuvre
import com.maison.mona.viewmodels.OeuvreViewModel
import org.osmdroid.util.GeoPoint
import java.text.Normalizer
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sqrt

class ListFragment : Fragment() {

    private val oeuvreViewModel: OeuvreViewModel by viewModels()

    private lateinit var adapter: ListAdapter
    private var layout: View? = null
    private var position: Parcelable? = null
    private lateinit var recyclerView: IndexFastScrollRecyclerView
    private var popupWindow: PopupWindow? = null

    //user location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation = GeoPoint(45.5044372, -73.578502)

    private var iconsStates = arrayListOf(
        arrayListOf(true, true, true),//Oeuvre
        arrayListOf(true, true, true), //Lieu
        arrayListOf(true, true, true) //patrimoine
    )

    private var iconsStatesBack = arrayListOf(
        arrayListOf(true, true, true),//Oeuvre
        arrayListOf(true, true, true), //Lieu
        arrayListOf(true, true, true)//patrimoine
    )

    private var categoryIndex: Int = 0
    private var filterIndex: Int = 0

    private var category: String = "Titres"
    private var filter: String = "A-Z"

    private var fromButton = false

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
                popupDisplay()
                true
            }
            R.id.list_geo -> {
                getUserLocation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //Set the main list that will be displayed on screen
    fun setList(category: String, filter: String){
        var filteredList: List<Oeuvre>
        var sortedList: List<Any> = listOf()

        //Gets the location of the user
        userLocation = SaveSharedPreference.getGeoLoc(context)

        if((iconsStates[0][0] or iconsStates[0][1] or iconsStates[0][2])
            && (iconsStates[1][0] or iconsStates[1][1] or iconsStates[1][2])){//Oeuvre and Lieu
            oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, { oeuvrelist ->
                filteredList = filterStateList(oeuvrelist)
                sortedList = filterList(filteredList, category, filter)
                adapter.submitList(sortedList,category,userLocation)
            })
        }else if((iconsStates[0][0] or iconsStates[0][1] or iconsStates[0][2])){//Only oeuvre
            oeuvreViewModel.oeuvreTList.observe(viewLifecycleOwner, { oeuvrelist ->
                filteredList = filterStateList(oeuvrelist)
                sortedList = filterList(filteredList, category, filter)
                adapter.submitList(sortedList,category,userLocation)
            })
        } else if((iconsStates[1][0] or iconsStates[1][1] or iconsStates[1][2])) {//Only lieu
            oeuvreViewModel.lieuList.observe(viewLifecycleOwner, { lieulist ->
                filteredList = filterStateList(lieulist)
                sortedList = filterList(filteredList, category, filter)
                adapter.submitList(sortedList,category,userLocation)
            })
        } else{//Empty
            filteredList = listOf()
            adapter.submitList(sortedList,category,userLocation)
        }
    }

    private fun getUserLocation(){
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
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if(location != null){
                        Log.d("Liste", "optient location")

                        val geoP = GeoPoint(location.latitude, location.longitude)
                        userLocation = geoP
                        SaveSharedPreference.setGeoLoc(context, geoP)
                        Toast.makeText(requireActivity(), "Position recentrée !", Toast.LENGTH_LONG).show()

                        setList(this.category, this.filter)
                    }
                }
        }
    }

    private fun filterStateList(list: List<Oeuvre>): List<Oeuvre>{
        var filteredList = list

        if(!(iconsStates[0][0]))
            filteredList = filteredList.filter { (it.type != "artwork") || (it.state != null)}

        if(!(iconsStates[0][1]))
            filteredList = filteredList.filter { (it.type != "artwork") || (it.state != 1)}

        if(!(iconsStates[0][2]))
            filteredList = filteredList.filter { (it.type != "artwork") || (it.state != 2)}

        if(!(iconsStates[1][0]))
            filteredList = filteredList.filter { (it.type != "place") || (it.state != null)}

        if(!(iconsStates[1][1]))
            filteredList = filteredList.filter { (it.type != "place") || (it.state != 1)}

        if(!(iconsStates[1][2]))
            filteredList = filteredList.filter { (it.type != "place") || (it.state != 2)}

        return filteredList
    }

    //Filter the list of items
    private fun filterList(list: List<Oeuvre>, category: String, filter: String): List<Any>{
        var currentList = list
        var sortedList = listOf<Any>()
        setDistances(list)

        if( currentList.isEmpty()){
            return sortedList
        }

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
        Log.d("Popup", "Liste length: " + sortedList.size)
        return sortedList
    }

    private fun setDistances(list:List<Oeuvre>){
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
    private fun addDistanceHeaders(list: MutableList<Oeuvre>): List<Any>{
        //Creation of mutable list of Interval object where the item and
        // their distance from the user are stored
        val sortedList = list.sortedWith(compareBy(Oeuvre::distance))

        //adding the item to their respectable list sequentially
        val sortedOeuvres = mutableListOf<Any>()
        //Set the distance headers as items at a lesser distance than X
        //var distanceCounter = 0;//initial
        //var distJump = 1//Jump between each header

        var current = -1
        val max = 9//last header(all items after more than)
        for (oeuvre in sortedList) {
            val distKm = oeuvre.distance//Km

            if (distKm != null) {
                val roundDist = distKm.toInt()

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
        val normalList = list.filter{ it.title!!.first().isLetter()}
        val specialList = list.filter{ !(it.title!!.first().isLetter()) }

        val alphabetMap = normalList.groupBy { unaccent(it.title!!).first().toUpperCase()}
        val listAlphabet = mutableListOf<Any>()

        alphabetMap.forEach { (t, u) ->
           listAlphabet.add(t.toString())
           listAlphabet.addAll(u)
        }

        listAlphabet.add("*")
        listAlphabet.addAll(specialList)
        return listAlphabet
    }
    //Adds the header at the right position in the list
    private fun addArtistsHeaders(list: MutableList<Oeuvre>,filter: String): List<Any> {
        //Find the list of all categories(names, borought ...)
        val categoryMap: MutableMap<String,MutableList<Oeuvre>> = mutableMapOf()
        categoryMap["*"] = mutableListOf()

        for(item in list){
            addListArtistMap(item, categoryMap)
        }

        val sortedCategoryMap = categoryMap.toSortedMap()
        //Create the list of items
        val sortedList = mutableListOf<Any>()

        for((key,value) in sortedCategoryMap){
            createListArtistItem(key, value, sortedList)
        }

        if(sortedCategoryMap["*"]?.isNotEmpty()!!){
            sortedList.add("* Pas d'artiste *")
            sortedCategoryMap["*"]?.let { sortedList.addAll(it) }
        }
        return sortedList
    }
    private fun createListArtistItem(key: String, value: MutableList<Oeuvre>, sortedList:  MutableList<Any>){
        if(key != "*"){
            sortedList.add(key)

            val vSort: List<Oeuvre> = if(filter == "A-Z"){
                value.sortedWith(compareBy(Oeuvre::title))
            }else{
                value.sortedWith(compareBy(Oeuvre::distance))
            }
            sortedList.addAll(vSort)
        }
    }
    private fun addListArtistMap(item: Oeuvre, categoryMap: MutableMap<String,MutableList<Oeuvre>>){
        if(item.artists.isNullOrEmpty()){
            categoryMap["*"]!!.add(item)
        }else {
            for(artist in item.artists!!){
                //Because we might have a name == ""
                if(artist.name.isNotBlank()) {
                    if (!categoryMap.contains(artist.name)) {
                        categoryMap[artist.name] = mutableListOf()
                    }

                    categoryMap[artist.name]?.add(item)
                }
            }
        }
    }
    //Adds the header at the right position in the list
    private fun addBoroughtHeaders(list: MutableList<Oeuvre>,filter: String): List<Any>{
        //Find the list of all categories(names, borought ...)
        val categoryMap: MutableMap<String,MutableList<Oeuvre>> = mutableMapOf()
        categoryMap["*"] = mutableListOf()
        for(item in list){
            if(item.borough.isNullOrEmpty()){
                categoryMap["*"]!!.add(item)
            }else {
                if (!categoryMap.contains(item.borough)) {
                    categoryMap[item.borough!!] = mutableListOf()
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
                val vSort: List<Oeuvre> = if(filter == "A-Z"){
                    v.sortedWith(compareBy(Oeuvre::title))
                }else{
                    v.sortedWith(compareBy(Oeuvre::distance))
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
        val categoryMap: MutableMap<String,MutableList<Oeuvre>> = mutableMapOf()
        categoryMap["*"] = mutableListOf()
        for(item in list){
            addListCategoryMap(item, categoryMap)
        }
        val sortedCategoryMap = categoryMap.toSortedMap()
        //Create the list of items
        val sortedList = mutableListOf<Any>()
        for((key,value) in sortedCategoryMap){
            createListItem(key, value, sortedList)
        }
        if(sortedCategoryMap["*"]?.isNotEmpty()!!){
            sortedList.add("* Pas de catégorie *")
            sortedCategoryMap["*"]?.let { sortedList.addAll(it) }
        }
        return sortedList
    }

    private fun createListItem(key: String, value: MutableList<Oeuvre>, sortedList:  MutableList<Any>){
        if(key != "*"){
            sortedList.add(key)

            val vSort: List<Oeuvre> = if(filter == "A-Z"){
                value.sortedWith(compareBy(Oeuvre::title))
            }else{
                value.sortedWith(compareBy(Oeuvre::distance))
            }
            sortedList.addAll(vSort)
        }
    }
    private fun addListCategoryMap(item: Oeuvre, categoryMap: MutableMap<String,MutableList<Oeuvre>>){
        if(item.category == null || item.category!!.fr.isEmpty()){
            categoryMap["*"]!!.add(item)
        }else {
            if (!categoryMap.contains(item.category!!.fr)) {
                categoryMap[item.category!!.fr] = mutableListOf()
            }
            categoryMap[item.category!!.fr]?.add(item)
        }
    }

    private fun unaccent(src: String): String {
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
        val p = 0.017453292519943295    // Math.PI / 180
        val a = 0.5 - cos((toLat - fromLat) * p) /2 +
                cos(fromLat * p) * cos(toLat * p) *
                (1 - cos((toLon - fromLon) * p))/2

        return 12742 * asin(sqrt(a)) // 2 * R; R = 6371 km
    }

    //Manages the filter popup display
    private fun popupDisplay() {
        //Sets the popup the first time, or return the current one
        if(popupWindow == null){
            Log.d("Popup", "Create Popup")
            val listDrawer = layoutInflater.inflate(R.layout.list_menu_drawer, null)
            popupWindow = PopupWindow(
                listDrawer,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            popupWindow!!.isOutsideTouchable = true
            //popupWindow.showAsDropDown(view)
            val categorySpinner = listDrawer.findViewById<Spinner>(R.id.category_spinner)
            val listCategory = listOf(
                "Titres",
                "Artistes",
                "Categorie",
                "Arrondissements"
            )
            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                listCategory
            )
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = spinnerAdapter
            //Set the icons
            val icons = listOf(
                listOf<ImageView>(  listDrawer.findViewById(R.id.oeuvre_normal),
                                    listDrawer.findViewById(R.id.oeuvre_targetted),
                                    listDrawer.findViewById(R.id.oeuvre_collected)),
                listOf<ImageView>(  listDrawer.findViewById(R.id.lieu_normal),
                                    listDrawer.findViewById(R.id.lieu_targetted),
                                    listDrawer.findViewById(R.id.lieu_collected)),
                listOf<ImageView>(  listDrawer.findViewById(R.id.patrimoine_normal),
                                    listDrawer.findViewById(R.id.patrimoine_targetted),
                                    listDrawer.findViewById(R.id.patrimoine_collected))
            )
            icons.forEachIndexed{i,row ->
                row.forEachIndexed{ j, _ ->
                    setIconToggle(icons[i][j],i,j)
                }
            }
            //Send the information from the filters when we close the popup
            val filterButton = listDrawer.findViewById<Button>(R.id.filterButton)
            val radioGroup = listDrawer.findViewById<RadioGroup>(R.id.radio_group)
            radioGroup.check(R.id.radio_alphabet)
            this.filterIndex = R.id.radio_alphabet
            //When we click the filter button
            filterButton.setOnClickListener {
                //Get info from the radio buttons
                val idCurrent  = radioGroup.checkedRadioButtonId
                this.filterIndex = idCurrent
                var radioValue = "None"
                if(idCurrent != -1){//Not needed, just in case
                    val radioButton = radioGroup.findViewById<RadioButton>(idCurrent)
                    radioValue = radioButton.text.toString()
                    Log.d("Popup", radioValue)
                }
                //Get Spinner value
                val spinnerValue = categorySpinner.selectedItem.toString()
                this.categoryIndex = categorySpinner.selectedItemPosition

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
                    radioGroup.check(this.filterIndex)
                    categorySpinner.setSelection(this.categoryIndex)
                    this.iconsStates[0] = ArrayList(this.iconsStatesBack[0])
                    this.iconsStates[1] = ArrayList(this.iconsStatesBack[1])

                    icons.forEachIndexed { i, _ ->
                        icons.forEachIndexed { j, _ ->
                            setIcon(icons[i][j], i, j)
                        }
                    }

                }
                this.fromButton = false
            }
        }
        popupWindow!!.showAtLocation(layout, Gravity.TOP or Gravity.END, 0, 0)
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

    private fun setIcon(view: ImageView, i: Int, j: Int) {
        if (!iconsStates[i][j]) {
            view.setColorFilter(R.color.black)
        } else {
            view.colorFilter = null
        }
    }
}
