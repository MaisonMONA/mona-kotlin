package com.example.mona.fragment

import android.content.Context
import android.location.Location
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem


// Instances of this class are fragments representing a single
// object in our collection.
class MapFragment : Fragment() {

    private var mMap : MapView? = null
    private val oeuvreViewModel : OeuvreViewModel by viewModels()
    private val lieuViewModel: LieuViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(it)
        }

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
        mMap?.setTileSource(TileSourceFactory.MAPNIK)
        mMap?.setMultiTouchControls(true);

        //Start Point Montreal
        //TODO: User location is start point
        val mapController: IMapController = mMap!!.controller
        mapController.setZoom(15.0)

        var latitude = 45.5044372
        var longitude = -73.578502
/*
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
                latitude = location?.latitude
                longitude = location?.longitude

            }

 */
        var startPoint = GeoPoint(latitude,longitude)

        mapController.setCenter(startPoint)

        //Adding Items
        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { oeuvreList->
            if(oeuvreList.size != 0){

                //See: https://osmdroid.github.io/osmdroid/javadocs/osmdroid-android/debug/index.html?org/osmdroid/views/overlay/ItemizedIconOverlay.html
                val items = ArrayList<OverlayItem>()
                context?.let{
                    for (oeuvre in oeuvreList){
                        val item_latitude = oeuvre.location!!.lat
                        val item_longitude = oeuvre.location!!.lng
                        val oeuvre_location = GeoPoint(item_latitude, item_longitude)
                        val overlayItem = OverlayItem(oeuvre.title, oeuvre.id.toString(), oeuvre_location)
                        val markerDrawable = ContextCompat.getDrawable(it, R.drawable.ic_oeuvre_normal)
                        overlayItem.setMarker(markerDrawable)
                        items.add(overlayItem)
                    }
                    val overlayObject = ItemizedIconOverlay(
                        items,
                        object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                            override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                                Toast.makeText(it, item.title, Toast.LENGTH_LONG).show()
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
                        }, it)
                    mMap?.overlays?.add(overlayObject)
                }
            }
        })

        lieuViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieuList ->
            if(lieuList.size != 0){
                val lieuItems = ArrayList<OverlayItem>()
                context?.let{
                    for(lieu in lieuList){
                        val lieu_latitude = lieu.location!!.lat
                        val lieu_longitude = lieu.location!!.lng
                        val lieu_location = GeoPoint(lieu_latitude, lieu_longitude)
                        val overlayItem = OverlayItem(lieu.title, lieu.category?.fr, lieu_location)
                        val markerDrawable = ContextCompat.getDrawable(it, R.drawable.ic_lieu_normal)
                        overlayItem.setMarker(markerDrawable)
                        lieuItems.add(overlayItem)

                    }
                    val overlayObject = ItemizedIconOverlay(
                        lieuItems,
                        object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                            override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                                Toast.makeText(it, item.title, Toast.LENGTH_LONG).show()
                                return true
                            }

                            override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                                return false
                            }
                        }, it)
                    mMap?.overlays?.add(overlayObject)
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        mMap?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMap?.onPause();
    }
}
