package com.example.mona.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mona.R
import com.example.mona.databinding.FragmentMapBinding
//import com.example.mona.viewmodels.LieuViewModel
import com.example.mona.viewmodels.OeuvreViewModel
import com.google.android.gms.location.*
import org.osmdroid.api.IGeoPoint
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener
import org.osmdroid.views.overlay.OverlayItem


// Instances of this class are fragments representing a single
// object in our collection.
class MapFragment : Fragment() {

    //Map attribute
    private lateinit var map: MapView
    private lateinit var mapController: IMapController
    private val ZOOM_LEVEL = 17.0


    private lateinit var userOverlay: OverlayItem
    private lateinit var userObject: ItemizedIconOverlay<OverlayItem>
    private var first = true

    //view models
    private val oeuvreViewModel: OeuvreViewModel by viewModels()
    //private val lieuViewModel: LieuViewModel by viewModels()

    //Location tools
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx: Context? = context
        org.osmdroid.config.Configuration.getInstance()
            .load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        //initialization of location agent
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLocationUpdates()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentMapBinding.inflate(inflater, container, false)
        context ?: return binding.root

        map = binding.mainMap
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map.setMultiTouchControls(true)
        map.setBuiltInZoomControls(false)

        mapController = map.controller
        mapController.setZoom(ZOOM_LEVEL)

        //Updates his or her location
        startLocationUpdates()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addOeuvre(null)
        addOeuvre(1)
        addOeuvre(2)

        //addLieu(null, R.drawable.pin_lieu_normal)
        //addLieu(1, R.drawable.pin_lieu_target)
        //addLieu(2, R.drawable.pin_lieu_collected)

    }

    override fun onResume() {
        super.onResume()

        setHasOptionsMenu(true)

        startLocationUpdates()

        map.onResume()
    }

    override fun onPause() {
        super.onPause()

        stopLocationUpdates()

        map.onPause()
    }

    private fun resize(image: Drawable?, dimension: Int): Drawable {
        val b = (image as BitmapDrawable).bitmap
        val bitmapResized = Bitmap.createScaledBitmap(b, dimension, dimension, false)
        return BitmapDrawable(resources, bitmapResized)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_menu, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.oeuvre_noncollected -> {
                map.overlays.clear()
                addOeuvre(null)
                true
            }
            R.id.oeuvre_targetted -> {
                map.overlays.clear()
                addOeuvre(1)
                true
            }
            R.id.oeuvre_collected -> {
                map.overlays.clear()
                addOeuvre(2)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getDrawable(state: Int?,type: String?): Int{
        if(type == "artwork"){
            return when(state){
                null -> R.drawable.pin_oeuvre_normal
                   1 -> R.drawable.pin_oeuvre_target
                   2 -> R.drawable.pin_oeuvre_collected
                else -> R.drawable.pin_oeuvre_normal
            }
        }else if(type == "place"){
            return when(state){
                null -> R.drawable.pin_lieu_normal
                1 -> R.drawable.pin_lieu_target
                2 -> R.drawable.pin_lieu_collected
                else -> R.drawable.pin_lieu_normal
            }
        }
        return  R.drawable.pin_lieu_normal
    }

    fun addOeuvre(state: Int?) {
        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { oeuvreList ->
            val items = ArrayList<OverlayItem>()
            for (oeuvre in oeuvreList) {
                if (oeuvre.state == state) {
                    val item_latitude = oeuvre.location!!.lat
                    val item_longitude = oeuvre.location!!.lng
                    val oeuvre_location = GeoPoint(item_latitude, item_longitude)
                    val overlayItem =
                        OverlayItem(oeuvre.title, oeuvre.id.toString(), oeuvre_location)

                    val pinIconId = getDrawable(state,oeuvre.type)
                    val markerDrawable = ContextCompat.getDrawable(this.requireContext(), pinIconId)

                    overlayItem.setMarker(markerDrawable)
                    items.add(overlayItem)
                }
            }

            val overlayObject = ItemizedIconOverlay(
                items,
                object : OnItemGestureListener<OverlayItem> {
                    override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                        Toast.makeText(requireActivity(), item.title, Toast.LENGTH_SHORT).show()
                        return true
                    }

                    override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                        val oeuvreId = item.snippet.toInt()
                        val arrayId = oeuvreId - 1
                        val oeuvre = oeuvreList[arrayId]
                        val action = HomeViewPagerFragmentDirections.homeToOeuvre(oeuvre)
                        findNavController().navigate(action)
                        return true
                    }
                },
                this.requireContext()
            )

            map.overlays.add(overlayObject)

        })

    }
    /*
    fun addLieu(state: Int?, pinIconId: Int) {
        lieuViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieuList ->
            val items = ArrayList<OverlayItem>()
            for (lieu in lieuList) {
                if (lieu.state == state) {
                    val item_latitude = lieu.location!!.lat
                    val item_longitude = lieu.location!!.lng
                    val lieu_location = GeoPoint(item_latitude, item_longitude)
                    val overlayItem = OverlayItem(lieu.title, lieu.id.toString(), lieu_location)
                    val markerDrawable = ContextCompat.getDrawable(this.requireContext(), pinIconId)

                    overlayItem.setMarker(markerDrawable)
                    items.add(overlayItem)
                }
            }

            val overlayObject = ItemizedIconOverlay(
                items,
                object : OnItemGestureListener<OverlayItem> {
                    override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                        Toast.makeText(requireActivity(), item.title, Toast.LENGTH_LONG).show()
                        return true
                    }

                    override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                        val itemId = item.snippet.toInt()
                        val arrayId = itemId - 1
                        val lieu = lieuList[arrayId]
                        val action = HomeViewPagerFragmentDirections.homeToLieu(lieu)
                        findNavController().navigate(action)
                        return true
                    }
                },
                this.requireContext()
            )

            map.overlays.add(overlayObject)

        })

    }
    */
    fun addUser(location: Location, first: Boolean) {

        if(!first){
            map.overlays.remove(userObject)
        }

        val point = GeoPoint(location!!.latitude, location!!.longitude)

        //Montreal random geo point start for testing
        //val point = GeoPoint(45.5044372, -73.578502)

        mapController.setCenter(point)

        //your items
        val userItems = ArrayList<OverlayItem>()

        var userOverlay = OverlayItem(
            "You",
            "Your position",
            point
        )

        val userOverlayMarker =
            ContextCompat.getDrawable(requireContext(), R.drawable.pin_user)
        userOverlay.setMarker(userOverlayMarker)
        userItems.add(userOverlay)

        userObject = ItemizedIconOverlay(
            userItems,
            object : OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                    Toast.makeText(requireActivity(), item.title, Toast.LENGTH_LONG).show()
                    return true
                }

                override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                    //open item fragment

                    return false
                }
            },
            this.requireContext()
        )

        map.overlays.add(userObject)

    }

    /**
     * call this method in onCreate
     * onLocationResult call when location is changed
     */
    private fun getLocationUpdates() {
        locationRequest = LocationRequest()
        locationRequest.interval = 3000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 100f // 100m
        locationRequest.priority =
            LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                if (locationResult.locations.isNotEmpty()) {
                    // get latest location
                    val location = locationResult.lastLocation

                    addUser(location, first)
                    first = false
                }


            }
        }
    }

    //start location updates
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }

    // stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


}
