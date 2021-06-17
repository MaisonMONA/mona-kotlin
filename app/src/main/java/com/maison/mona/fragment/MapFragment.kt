package com.maison.mona.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.maison.mona.R
import com.maison.mona.data.SaveSharedPreference
import com.maison.mona.databinding.FragmentMapBinding
import com.maison.mona.entity.Oeuvre
import com.maison.mona.viewmodels.OeuvreViewModel
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.OverlayItem
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sqrt

// Instances of this class are fragments representing a single
// object in our collection.
class MapFragment : Fragment() {

    //Map attribute
    private lateinit var map: MapView
    private lateinit var mapController: IMapController
    private val ZOOM_LEVEL = 17.0

    private lateinit var userObject: ItemizedIconOverlay<OverlayItem>
    private var pin_set = false
    private var pin_loc: ItemizedIconOverlay<OverlayItem>? = null
    private var pin_user: ItemizedIconOverlay<OverlayItem>? = null
    private lateinit var init_coord: GeoPoint
    private lateinit var coord: GeoPoint

    //view models
    private val oeuvreViewModel: OeuvreViewModel by viewModels()

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

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    Log.d("UPDATES", location.toString())
                    val geoPoint = GeoPoint(location.latitude, location.longitude)

                    if(!pin_set) {
                        SaveSharedPreference.setGeoLoc(context, geoPoint)
                        map.overlays.remove(userObject)

                        addUser(geoPoint, ContextCompat.getDrawable(requireContext(), R.drawable.pin_localisation_user), false)
                    }

                    coord = geoPoint
                }
            }
        }
        locationRequest = LocationRequest.create()?.apply {
            interval = 1000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }!!
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

        init_coord = SaveSharedPreference.getGeoLoc(context)
        coord = init_coord
        addUser(init_coord, ContextCompat.getDrawable(requireContext(), R.drawable.pin_localisation_user), false)
        mapController.setCenter(init_coord)

        //Updates his or her location
        startLocationUpdates()

        val touchOverlay = object: Overlay(){
            override fun onLongPress(e: MotionEvent?, mapView: MapView?): Boolean {
                val proj = mapView!!.projection
                proj.fromPixels(e!!.x.toInt(), e.y.toInt()) as GeoPoint
                val geoPoint = proj.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint

                val pinConfirm = AlertDialog.Builder(context, R.style.locationPinTheme)
                pinConfirm.setTitle(R.string.pinDialogAlertTitle)
                pinConfirm.setMessage(R.string.pinDialogAlertMessage)

                pinConfirm.setPositiveButton(R.string.Yes) { _, _ ->
                    SaveSharedPreference.setGeoLoc(context, geoPoint)

                    if(pin_set){
                        map.overlays.remove(pin_loc)
                    }

                    addUser(geoPoint, ContextCompat.getDrawable(requireContext(), R.drawable.pin_new_location), true)
                    mapController.setCenter(geoPoint)
                    pin_set = true

                    oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, { list ->
                        for(oeuvre in list) {
                            val distance = distance(
                                geoPoint.latitude,
                                geoPoint.longitude,
                                oeuvre.location!!.lat,
                                oeuvre.location!!.lng
                            )
                            oeuvre.distance = distance
                        }

                        val sortedList = list.sortedWith(compareBy(Oeuvre::distance))

                        val threeList = mutableListOf(sortedList[0], sortedList[1], sortedList[2])

                        for(oeuvre in threeList){
                            Log.d("MAPMAP", oeuvre.title.toString() + " à " + oeuvre.distance?.times(1000)?.toInt().toString() + " m")
                        }
                    })
                }

                pinConfirm.setNegativeButton(R.string.No) { _, _ -> }

                val alert = pinConfirm.create()
                alert.show()

                return super.onLongPress(e, mapView)
            }
        }

        map.overlays.add(touchOverlay)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addOeuvre(null, "place")
        addOeuvre(null, "artwork")
        addOeuvre(3, "place")
        addOeuvre(3, "artwork")
        addOeuvre(2, "place")
        addOeuvre(2, "artwork")
        addOeuvre(1, "place")
        addOeuvre(1, "artwork")
    }

    override fun onResume() {
        super.onResume()

        if(pin_set && coord == SaveSharedPreference.getGeoLoc(context)){
            map.overlays.remove(pin_loc)
            map.overlays.remove(pin_user)
            pin_set = false
            addUser(coord, ContextCompat.getDrawable(requireContext(), R.drawable.pin_localisation_user), false)
        }

        startLocationUpdates()
        setHasOptionsMenu(true)
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
                addOeuvre(null, "artwork")
                addUser(coord, ContextCompat.getDrawable(requireContext(), R.drawable.pin_localisation_user), false)
                true
            }
            R.id.oeuvre_targetted -> {
                map.overlays.clear()
                addOeuvre(1, "artwork")
                addUser(coord, ContextCompat.getDrawable(requireContext(), R.drawable.pin_localisation_user), false)
                true
            }
            R.id.oeuvre_collected -> {
                map.overlays.clear()
                addOeuvre(2, "artwork")
                addUser(coord, ContextCompat.getDrawable(requireContext(), R.drawable.pin_localisation_user), false)
                true
            }
            R.id.lieu_noncollected -> {
                map.overlays.clear()
                addOeuvre(null, "place")
                addUser(coord, ContextCompat.getDrawable(requireContext(), R.drawable.pin_localisation_user), false)
                true
            }
            R.id.lieu_targetted -> {
                map.overlays.clear()
                addOeuvre(1, "place")
                addUser(coord, ContextCompat.getDrawable(requireContext(), R.drawable.pin_localisation_user), false)
                true
            }
            R.id.lieu_collected -> {
                map.overlays.clear()
                addOeuvre(2, "place")
                addUser(coord, ContextCompat.getDrawable(requireContext(), R.drawable.pin_localisation_user), false)
                true
            }
            R.id.map_geo -> {
                if(pin_set){
                    map.overlays.remove(pin_loc)
                    map.overlays.remove(pin_user)
                    pin_set = false
                    addUser(coord, ContextCompat.getDrawable(requireContext(), R.drawable.pin_localisation_user), false)
                    SaveSharedPreference.setGeoLoc(context, coord)
                }
                Log.d("UPDATES", "recentre")
                mapController.setCenter(coord)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getDrawable(state: Int?, type: String?): Int{
        if(type == "artwork"){
            return when(state){
                null -> R.drawable.pin_oeuvre_normal
                1 -> R.drawable.pin_oeuvre_target
                2, 3 -> R.drawable.pin_oeuvre_collected
                else -> R.drawable.pin_oeuvre_normal
            }
        }else if(type == "place"){
            return when(state){
                null -> R.drawable.pin_lieu_normal
                1 -> R.drawable.pin_lieu_target
                2, 3 -> R.drawable.pin_lieu_collected
                else -> R.drawable.pin_lieu_normal
            }
        }

        return  R.drawable.pin_lieu_normal
    }

    private fun addOeuvre(state: Int?, type: String) {
        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, { oeuvreList ->
            val items = ArrayList<OverlayItem>()
            for (oeuvre in oeuvreList) {
                if (oeuvre.state == state && oeuvre.type == type) {
                    val item_latitude = oeuvre.location!!.lat
                    val item_longitude = oeuvre.location!!.lng
                    val oeuvre_location = GeoPoint(item_latitude, item_longitude)
                    val overlayItem =
                        OverlayItem(oeuvre.title, oeuvre.id.toString(), oeuvre_location)

                    val pinIconId = getDrawable(state, oeuvre.type)
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

    fun addUser(location: GeoPoint, pin: Drawable?, new_pin: Boolean) {
        //your items
        val userItems = ArrayList<OverlayItem>()

        val userOverlay = OverlayItem(
            "You",
            "Your position",
            location
        )

        userOverlay.setMarker(pin)
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

        if(new_pin){
            pin_loc = userObject
        } else {
            pin_user = userObject
        }

        map.overlays.add(userObject)
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

//    private fun getLastLocation(){
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//
//        } else {
//            fusedLocationClient.lastLocation
//                .addOnSuccessListener { location: Location? ->
//                    // Got last known location. In some rare situations this can be null.
//                    Log.d("Map", location.toString())
//                    if (location != null) {
//                        Log.d("Map", "optien location")
//                        // get latest location
//                        val geoP = GeoPoint(location.latitude, location.longitude)
//                        SaveSharedPreference.setGeoLoc(context, geoP)
//                        //addUser(geoP, true, ContextCompat.getDrawable(requireContext(), R.drawable.pin_localisation_user), true, true)
//                        Toast.makeText(requireActivity(), "Position recentrée !", Toast.LENGTH_LONG).show()
//                    }
//                }
//        }
//    }

    /**
     * call this method in onCreate
     * onLocationResult call when location is changed
     */

    //start location updates
    private fun startLocationUpdates() {
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
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper() /* Looper */
        )
    }

    // stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
