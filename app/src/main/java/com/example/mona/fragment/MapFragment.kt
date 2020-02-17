package com.example.mona.fragment

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mona.R
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView


// Instances of this class are fragments representing a single
// object in our collection.
class MapFragment : Fragment() {
    private var mMap : MapView? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val ctx: Context? = context
        org.osmdroid.config.Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMap = view.findViewById(R.id.main_map);
        mMap?.setTileSource(TileSourceFactory.MAPNIK);

        //Start Point Montreal
        //TODO: User location is start point
        val mapController: IMapController = mMap!!.controller
        mapController.setZoom(15.0)
        val startPoint = GeoPoint(45.5044372, -73.578502)
        mapController.setCenter(startPoint)
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
