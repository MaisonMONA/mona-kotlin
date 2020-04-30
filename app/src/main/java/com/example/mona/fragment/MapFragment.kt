package com.example.mona.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import com.example.mona.R
import com.example.mona.databinding.FragmentMapBinding
import com.example.mona.entity.Oeuvre
import com.example.mona.viewmodels.LieuViewModel
import com.example.mona.viewmodels.OeuvreViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.OverlayItem


// Instances of this class are fragments representing a single
// object in our collection.
class MapFragment : Fragment() {

    private lateinit var map : MapView
    private lateinit var mapController: IMapController
    private val ZOOM_LEVEL = 17.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val oeuvreViewModel: OeuvreViewModel by viewModels()
    private val lieuViewModel: LieuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx: Context? = context
        org.osmdroid.config.Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding = FragmentMapBinding.inflate(inflater, container, false)

        map = binding.mainMap
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        mapController = map.controller
        mapController.setZoom(ZOOM_LEVEL)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                //val point = GeoPoint(location!!.latitude, location!!.longitude)
                val point = GeoPoint(45.5044372,-73.578502)

                mapController.setCenter(point)

                //your items
                val userItems = ArrayList<OverlayItem>()

                val userOverlay = OverlayItem(
                    "You",
                    "Your position",
                    point
                )

                val userOverlayMarker = resize(ContextCompat.getDrawable(requireContext(), R.drawable.ic_user_navigation), 26)
                userOverlay.setMarker(userOverlayMarker)
                userItems.add(userOverlay)

                val overlayObject = ItemizedIconOverlay(
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
                    this.requireContext())

                map.overlays.add(overlayObject)
            }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer {oeuvreList ->

            lieuViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieuList ->

                val items = ArrayList<OverlayItem>()

                for (oeuvre in oeuvreList) {
                    val item_latitude = oeuvre.location!!.lat
                    val item_longitude = oeuvre.location!!.lng
                    val oeuvre_location = GeoPoint(item_latitude, item_longitude)
                    val overlayItem = OverlayItem(oeuvre.title, oeuvre.id.toString(), oeuvre_location)
                    val markerDrawable = resize(ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_oeuvre_normal), 125)

                    overlayItem.setMarker(markerDrawable)
                    items.add(overlayItem)
                }

                for (lieu in lieuList) {
                    val item_latitude = lieu.location!!.lat
                    val item_longitude = lieu.location!!.lng
                    val lieu_location = GeoPoint(item_latitude, item_longitude)
                    val overlayItem = OverlayItem(lieu.title, lieu.id.toString(), lieu_location)
                    val markerDrawable = resize(ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_lieu_normal), 125)

                    overlayItem.setMarker(markerDrawable)
                    items.add(overlayItem)
                }

                val overlayObject = ItemizedIconOverlay(
                    items,
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
                    this.requireContext())

                map.overlays.add(overlayObject)

            })

        })


    }

    override fun onResume() {
        super.onResume()
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




}
