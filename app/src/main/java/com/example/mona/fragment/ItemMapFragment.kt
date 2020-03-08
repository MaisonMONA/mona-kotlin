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
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class ItemMapFragment : Fragment() {

    private var mMap: MapView? = null
    val safeArgs : ItemMapFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ctx: Context? = context
        org.osmdroid.config.Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        return inflater.inflate(R.layout.fragment_item_map, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mMap = view.findViewById(R.id.item_map)
        mMap?.setTileSource(TileSourceFactory.MAPNIK)

        val mapController: IMapController = mMap!!.controller
        mapController.setZoom(18.0)

        val oeuvre = safeArgs.oeuvre

        val item_latitude = oeuvre.location!!.lat
        val item_longitude = oeuvre.location!!.lng
        val oeuvre_location = GeoPoint(item_latitude, item_longitude)

        val startMarker = Marker(mMap)
        startMarker.position = oeuvre_location
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mMap?.getOverlays()?.add(startMarker)


        //TODO: pick a marker
        //startMarker.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
        //startMarker.setTitle("Start point");


        mapController.setCenter(oeuvre_location)
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
