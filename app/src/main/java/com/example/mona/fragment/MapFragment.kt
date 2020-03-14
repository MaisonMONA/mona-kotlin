package com.example.mona.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.mona.LieuViewModel
import com.example.mona.OeuvreViewModel
import com.example.mona.R
import com.example.mona.entity.Lieu
import com.example.mona.entity.Oeuvre
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import org.osmdroid.api.IGeoPoint
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.ITileSource
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme


// Instances of this class are fragments representing a single
// object in our collection.
class MapFragment : Fragment(), LocationListener {

    private var mMap : MapView? = null
    private val oeuvreViewModel : OeuvreViewModel by viewModels()
    private val lieuViewModel: LieuViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private  var mLatitude : Double = 45.5044372
    private  var mLongitude : Double = -73.578502
    private lateinit var mapController : IMapController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val ctx: Context? = context
        org.osmdroid.config.Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMap = view.findViewById(R.id.main_map)
        fusedLocationClient =  LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            mLatitude = location!!.latitude
            mLongitude = location.longitude
        }
        mMap?.setMultiTouchControls(true)
        //Start Point Montreal
        //TODO: User location is start point
        mapController = mMap!!.controller
        mapController.setZoom(11.0)

        var startPoint = GeoPoint(mLatitude,mLongitude)

        val startMarker = Marker(mMap)
        startMarker.position = startPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mMap?.getOverlays()?.add(startMarker)


        //TODO: pick a marker
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_location));
        startMarker.setTitle("Start point");

        //Adding Items
        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer<List<Oeuvre>> { oeuvreList->
            if(oeuvreList.isNotEmpty()){

                //See: https://osmdroid.github.io/osmdroid/javadocs/osmdroid-android/debug/index.html?org/osmdroid/views/overlay/ItemizedIconOverlay.html
                val items = ArrayList<OverlayItem>()

                    for (oeuvre in oeuvreList){
                        val item_latitude = oeuvre.location!!.lat
                        val item_longitude = oeuvre.location!!.lng
                        val oeuvre_location = GeoPoint(item_latitude, item_longitude)
                        val overlayItem = OverlayItem(oeuvre.title, oeuvre.id.toString(), oeuvre_location)
                        val markerDrawable = ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_oeuvre_normal)
                        overlayItem.setMarker(markerDrawable)
                        items.add(overlayItem)
                    }
                    val overlayObject = ItemizedIconOverlay(
                        items,
                        object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                            override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                                Toast.makeText(context?.applicationContext, item.title, Toast.LENGTH_LONG).show()
                                return true
                            }

                            override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                                //open item fragment
                                val oeuvre = oeuvreList.get(item.snippet.toInt() - 1)
                                if(findNavController().currentDestination?.id == R.id.map_dest){
                                    val action = MapFragmentDirections.mapToItem(oeuvre)
                                    findNavController().navigate(action)
                                }
                                return false
                            }
                        },this.requireContext())
                    mMap?.overlays?.add(overlayObject)
                }

        })

        lieuViewModel.lieuList.observe(viewLifecycleOwner, Observer<List<Lieu>> { lieuList ->
            if(lieuList.isNotEmpty()){
                val lieuItems = ArrayList<OverlayItem>()
                context?.let{
                    for(lieu in lieuList){
                        val lieu_latitude = lieu.location!!.lat
                        val lieu_longitude = lieu.location!!.lng
                        val lieu_location = GeoPoint(lieu_latitude, lieu_longitude)
                        val overlayItem = OverlayItem(lieu.title, lieu.category?.fr, lieu_location)
                        val markerDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_lieu_normal)
                        overlayItem.setMarker(markerDrawable)
                        lieuItems.add(overlayItem)

                    }
                    val overlayObject = ItemizedIconOverlay(
                        lieuItems,
                        object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                            override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                                Toast.makeText(requireContext(), item.title, Toast.LENGTH_LONG).show()
                                return true
                            }

                            override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                                //open item fragment
                                val lieu = lieuList.get(item.snippet.toInt() - 1)
                                if(findNavController().currentDestination?.id == R.id.map_dest){

                                }
                                return false
                            }
                        }, this.requireContext())
                    mMap?.overlays?.add(overlayObject)
                }
            }
        })
        mapController.setCenter(GeoPoint(mLatitude,mLongitude))
        mapController.animateTo(GeoPoint(mLatitude,mLongitude))
    }

    override fun onResume() {
        super.onResume()
        mMap?.onResume()
        mapController.setCenter(GeoPoint(mLatitude,mLongitude))
        mapController.animateTo(GeoPoint(mLatitude,mLongitude))
    }

    override fun onPause() {
        super.onPause()
        mMap?.onPause();
    }

    override fun onLocationChanged(location: Location?) {
        fusedLocationClient.lastLocation.result
        if (fusedLocationClient.lastLocation.isSuccessful) {
            mapController.setCenter(GeoPoint(mLatitude,mLongitude))
            mapController.animateTo(GeoPoint(mLatitude,mLongitude))
        }

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        TODO("Not yet implemented")
    }

    // Location Listener
    override fun onProviderEnabled(provider: String?) {
        TODO("Not yet implemented")
    }

    override fun onProviderDisabled(provider: String?) {
        TODO("Not yet implemented")
    }
}
