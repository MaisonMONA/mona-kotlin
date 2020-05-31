package com.example.mona.fragment

import android.content.Context
import android.graphics.Bitmap
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
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
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
    private lateinit var userOverlay: OverlayItem

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
        context ?: return binding.root

        map = binding.mainMap
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        map.setMultiTouchControls(true)
        map.setBuiltInZoomControls(false)

        mapController = map.controller
        mapController.setZoom(ZOOM_LEVEL)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                //val point = GeoPoint(location!!.latitude, location!!.longitude)

                //Montreal random geo point start for testing
                val point = GeoPoint(45.5044372,-73.578502)

                mapController.setCenter(point)

                //your items
                val userItems = ArrayList<OverlayItem>()

                userOverlay = OverlayItem(
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

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addOeuvre(null, R.drawable.pin_oeuvre_normal)
        addOeuvre(1, R.drawable.pin_oeuvre_target)
        addOeuvre(2, R.drawable.pin_oeuvre_collected)

        addLieu(null, R.drawable.pin_lieu_normal)
        addLieu(1, R.drawable.pin_lieu_target)
        addLieu(2, R.drawable.pin_lieu_collected)


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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.oeuvre_noncollected -> {
                map.overlays.clear()
                addOeuvre(null, R.drawable.pin_oeuvre_normal)
                true
            }
            R.id.oeuvre_targetted -> {
                map.overlays.clear()
                addOeuvre(1, R.drawable.pin_oeuvre_target)
                true
            }
            R.id.oeuvre_collected ->{
                map.overlays.clear()
                addOeuvre(2, R.drawable.pin_oeuvre_collected)
                true
            }
            R.id.lieu_noncollected -> {
                map.overlays.clear()
                addLieu(null, R.drawable.pin_lieu_normal)
                true
            }
            R.id.lieu_targetted -> {
                map.overlays.clear()
                addLieu(1, R.drawable.pin_lieu_target)
                true
            }
            R.id.lieu_collected ->{
                map.overlays.clear()
                addLieu(2, R.drawable.pin_lieu_collected)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    fun addOeuvre(state: Int?, pinIconId: Int) {
        oeuvreViewModel.oeuvreList.observe(viewLifecycleOwner, Observer { oeuvreList ->
            val items = ArrayList<OverlayItem>()
            for (oeuvre in oeuvreList) {
                if(oeuvre.state ==  state){
                    val item_latitude = oeuvre.location!!.lat
                    val item_longitude = oeuvre.location!!.lng
                    val oeuvre_location = GeoPoint(item_latitude, item_longitude)
                    val overlayItem = OverlayItem(oeuvre.title, oeuvre.id.toString(), oeuvre_location)
                    val markerDrawable = ContextCompat.getDrawable(this.requireContext(), pinIconId)

                    overlayItem.setMarker(markerDrawable)
                    items.add(overlayItem)
                }
            }

            items.add(userOverlay)

            val overlayObject = ItemizedIconOverlay(
                items,
                object : OnItemGestureListener<OverlayItem> {
                    override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                        Toast.makeText(requireActivity(), item.title, Toast.LENGTH_SHORT).show()
                        return true
                    }

                    override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                        val oeuvreId = item.snippet.toInt()
                        val arrayId =  oeuvreId - 1
                        val oeuvre = oeuvreList[arrayId]
                        val action = HomeViewPagerFragmentDirections.homeToOeuvre(oeuvre)
                        findNavController().navigate(action)
                        return true
                    }
                },
                this.requireContext())

            map.overlays.add(overlayObject)

        })

    }

    fun addLieu(state: Int?, pinIconId: Int) {
        lieuViewModel.lieuList.observe(viewLifecycleOwner, Observer { lieuList ->
            val items = ArrayList<OverlayItem>()
            for (lieu in lieuList) {
                if(lieu.state ==  state){
                    val item_latitude = lieu.location!!.lat
                    val item_longitude = lieu.location!!.lng
                    val lieu_location = GeoPoint(item_latitude, item_longitude)
                    val overlayItem = OverlayItem(lieu.title, lieu.id.toString(), lieu_location)
                    val markerDrawable = ContextCompat.getDrawable(this.requireContext(), pinIconId)

                    overlayItem.setMarker(markerDrawable)
                    items.add(overlayItem)
                }
            }

            items.add(userOverlay)

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
                this.requireContext())

            map.overlays.add(overlayObject)

        })

    }






}
