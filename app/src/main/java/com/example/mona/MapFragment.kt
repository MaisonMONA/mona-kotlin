package com.example.mona

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView


// Instances of this class are fragments representing a single
// object in our collection.
class MapFragment : Fragment() {
    var mMap : MapView? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val ctx: Context? = context
        org.osmdroid.config.Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mMap = view.findViewById(R.id.main_map);
        mMap?.setTileSource(TileSourceFactory.MAPNIK);
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
