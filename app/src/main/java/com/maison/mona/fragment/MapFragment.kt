package com.maison.mona.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.maison.mona.R
import com.maison.mona.data.SaveSharedPreference
import com.maison.mona.databinding.FragmentMapBinding
import com.maison.mona.viewmodels.OeuvreViewModel
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.OverlayItem

// Instances of this class are fragments representing a single
// object in our collection.
class MapFragment : Fragment() {

    //Map attribute
    private lateinit var map: MapView
    private lateinit var mapController: IMapController
    private var pinLocation: GeoPoint = GeoPoint(45.5044372, -73.578502)
    private val ZOOM_LEVEL = 17.0

    private lateinit var userObject: ItemizedIconOverlay<OverlayItem>
    private var pin_set = false
    private var pin_loc: ItemizedIconOverlay<OverlayItem>? = null

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
        //startLocationUpdates()

        val coord = SaveSharedPreference.getGeoLoc(context)
        Log.d("COORD", coord.toString())
        addUser(coord, false, ContextCompat.getDrawable(requireContext(), R.drawable.pin_localisation_user), true)

        val touchOverlay = object: Overlay(){
            override fun onLongPress(e: MotionEvent?, mapView: MapView?): Boolean {
                val proj = mapView!!.projection
                proj.fromPixels(e!!.x.toInt(), e.y.toInt()) as GeoPoint
                val geoPoint = proj.fromPixels(e.x.toInt(), e.y.toInt()) as GeoPoint

                val pinConfirm = AlertDialog.Builder(context, R.style.locationPinTheme)
                pinConfirm.setTitle(R.string.pinDialogAlertTitle)
                pinConfirm.setMessage(R.string.pinDialogAlertMessage)

                pinConfirm.setPositiveButton(R.string.Yes) { dialog, which ->
                    pinLocation = geoPoint
                    SaveSharedPreference.setGeoLoc(context, geoPoint)

                    addUser(geoPoint, false, ContextCompat.getDrawable(requireContext(), R.drawable.pin_new_location), false)
                    pin_set = true
                }

                pinConfirm.setNegativeButton(R.string.No) {dialog, which -> null}

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

        addOeuvre(null)
        addOeuvre(1)
        addOeuvre(2)
        addOeuvre(3)
    }

    override fun onResume() {
        super.onResume()

        setHasOptionsMenu(true)
        map.onResume()
    }

    override fun onPause() {
        super.onPause()

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
            R.id.map_geo -> {
                getLastLocation()
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

    fun addUser(location: GeoPoint, first: Boolean, pin: Drawable?, new_pin: Boolean) {
        if(pin_set){
            map.overlays.remove(userObject)
        } else if (first){
            map.overlays.remove(userObject)
            if(pin_set){
                Log.d("MAPLOG", "pinloc delete : " + pin_loc.toString())
                map.overlays.remove(pin_loc)
            }
        }

        mapController.setCenter(location)

        //your items
        val userItems = ArrayList<OverlayItem>()

        var userOverlay = OverlayItem(
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
            Log.d("MAPLOG", "pinloc def " + pin_loc.toString())
        }

        Log.d("MAPLOG", "userObject def " + userObject.toString())

        map.overlays.add(userObject)
    }

    private fun getLastLocation(){
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    Log.d("Map", location.toString())
                    if (location != null) {
                        Log.d("Map", "optien location")
                        // get latest location
                        val geoP = GeoPoint(location.latitude, location.longitude)
                        SaveSharedPreference.setGeoLoc(context, geoP)
                        addUser(geoP, true, ContextCompat.getDrawable(requireContext(), R.drawable.pin_localisation_user), true)
                    }
                }
        }
    }

    /**
     * call this method in onCreate
     * onLocationResult call when location is changed
     */

    /*
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
            null /* Looper */
        )
    }

    // stop location updates
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    */

}
