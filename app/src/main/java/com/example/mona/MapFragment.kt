package com.example.mona

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_map.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.util.concurrent.Executor
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

// Instances of this class are fragments representing a single
// object in our collection.
class MapFragment : Fragment(), LocationListener  {

    private var mMap : MapView? = null
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private val DEFAULT_LATITUDE = 45.508888
    private val DEFAULT_LONGITUDE = -73.561668
    private var mCurrentLocation : Location = Location(LocationManager.GPS_PROVIDER)
    private var mDefaultLocation : Location = Location(LocationManager.GPS_PROVIDER)
    private val DEFAULT_ZOOM : Double = 15.0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx: Context? = context
        org.osmdroid.config.Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationProviderClient.lastLocation
            .addOnCompleteListener {
                mCurrentLocation.latitude = it.result!!.latitude
                mCurrentLocation.longitude = it.result!!.longitude
                mDefaultLocation.latitude = it.result!!.latitude
                mDefaultLocation.longitude = it.result!!.longitude
            }
        mMap = main_map
        mMap?.setTileSource(TileSourceFactory.MAPNIK)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
        mDefaultLocation.latitude = DEFAULT_LATITUDE
        mDefaultLocation.longitude = DEFAULT_LONGITUDE
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mFusedLocationProviderClient.lastLocation
            .addOnCompleteListener {
                mCurrentLocation.latitude = it.result!!.latitude
                mCurrentLocation.longitude = it.result!!.longitude
                mDefaultLocation.latitude = it.result!!.latitude
                mDefaultLocation.longitude = it.result!!.longitude
            }
        setCurrentLocation(mCurrentLocation)
    }

    override fun onResume() {
        super.onResume()
        mMap?.onResume()

    }

    override fun onPause() {
        super.onPause()
        mMap?.onPause()
    }

    override fun onLocationChanged(location: Location?) {

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }

    private fun setCurrentLocation(loc : Location?){
        mMap?.controller?.setZoom(DEFAULT_ZOOM)
        mMap?.controller?.setCenter(GeoPoint(loc!!.latitude,loc.longitude))
    }

    private fun getCurrentLocation() {
        mFusedLocationProviderClient.lastLocation
    }
}
