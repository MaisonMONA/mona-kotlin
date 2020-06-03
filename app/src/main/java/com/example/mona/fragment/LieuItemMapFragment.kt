package com.example.mona.fragment

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.mona.R
import com.example.mona.databinding.FragmentItemMapBinding
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class LieuItemMapFragment : Fragment() {

    private lateinit var mMap: MapView
    private lateinit var mapController: IMapController
    private val ZOOM_LEVEL = 17.0
    val safeArgs : LieuItemMapFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx: Context? = context
        org.osmdroid.config.Configuration.getInstance()
            .load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentItemMapBinding.inflate(inflater, container, false)
        context ?: return binding.root

        mMap = binding.itemMap

        mMap?.setTileSource(TileSourceFactory.MAPNIK)

        mapController = mMap.controller
        mapController.setZoom(ZOOM_LEVEL)

        val mapController: IMapController = mMap!!.controller
        mapController.setZoom(18.0)

        val lieu = safeArgs.lieu

        val item_latitude = lieu.location!!.lat
        val item_longitude = lieu.location!!.lng
        val lieu_location = GeoPoint(item_latitude, item_longitude)

        val startMarker = Marker(mMap)
        startMarker.position = lieu_location
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mMap?.getOverlays()?.add(startMarker)

        mapController.setCenter(lieu_location)

        return binding.root
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
