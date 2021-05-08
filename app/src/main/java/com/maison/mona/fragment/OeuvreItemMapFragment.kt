package com.maison.mona.fragment

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.maison.mona.R
import com.maison.mona.data.SaveSharedPreference
import com.maison.mona.databinding.FragmentItemMapBinding
import kotlinx.android.synthetic.main.list_menu_drawer.*
import kotlinx.android.synthetic.main.list_menu_drawer.view.*
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

//a clean
class OeuvreItemMapFragment : Fragment() {

    private lateinit var mMap: MapView
    private lateinit var mapController: IMapController
    private val ZOOM_LEVEL = 17.0
    val safeArgs : OeuvreItemMapFragmentArgs by navArgs()

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

        mMap.setMultiTouchControls(true)
        mMap.setBuiltInZoomControls(false)

        mapController = mMap.controller
        mapController.setZoom(ZOOM_LEVEL)

        val lieu = safeArgs.oeuvre

        val item_latitude = lieu.location!!.lat
        val item_longitude = lieu.location!!.lng
        val oeuvre_location = GeoPoint(item_latitude, item_longitude)

        val startMarker = Marker(mMap)
        startMarker.position = oeuvre_location
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.icon = ContextCompat.getDrawable(requireContext(), getDrawable(lieu.state, lieu.type))

        val pinLocation = SaveSharedPreference.getGeoLoc(context)
        val pinMarker = Marker(mMap)
        pinMarker.position = pinLocation
        pinMarker.icon = ContextCompat.getDrawable(requireContext(), R.drawable.pin_user)

        mMap.getOverlays()?.add(startMarker)
        mMap.getOverlays()?.add(pinMarker)

        mapController.setCenter(oeuvre_location)

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

    //TO DO
    fun getDrawable(state: Int?, type: String?): Int{
        if(type == "artwork"){
            return when(state){
                null -> R.drawable.pin_oeuvre_normal
                1 -> R.drawable.pin_oeuvre_target
                2, 3 -> R.drawable.pin_oeuvre_collected
                else -> R.drawable.pin_oeuvre_normal
            }
        }else if(type == "place"){
            return when(state){
                null -> R.drawable.pin_lieu_normal
                1 -> R.drawable.pin_lieu_target
                2, 3 -> R.drawable.pin_lieu_collected
                else -> R.drawable.pin_lieu_normal
            }
        }
        return  R.drawable.pin_lieu_normal
    }
}
