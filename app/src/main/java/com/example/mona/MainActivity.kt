package com.example.mona

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView


class MainActivity : AppCompatActivity() {

    private var mMap : MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mMap = findViewById(R.id.mMap);
        mMap?.setTileSource(TileSourceFactory.MAPNIK);
    }

    override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
//if you make changes to the configuration, use
//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        mMap?.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause()
        //this will refresh the osmdroid configuration on resuming.
//if you make changes to the configuration, use
//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//Configuration.getInstance().save(this, prefs);
        mMap?.onPause() //needed for compass, my location overlays, v6.0.0 and up
    }
}
