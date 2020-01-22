package com.example.mona

import MyAdapter
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.views.MapView


class MainActivity : AppCompatActivity() {

    private var mMap : MapView? = null

    private companion object {
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL : Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                // resquestPersmissions calls onRequestPermissionResult
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL)

                // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }else{
            setContentView(R.layout.activity_main)
            setupMainActivity()
            // Note that the Toolbar defined in the layout has the id "my_toolbar"
            setSupportActionBar(findViewById(R.id.toolbar))
        }

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    setContentView(R.layout.activity_main)
                    setupMainActivity()
                    // Note that the Toolbar defined in the layout has the id "my_toolbar"
                    setSupportActionBar(findViewById(R.id.toolbar))
                } else {
                    // Add permissions denied layout

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }


    // Called when all persmissions are granted
    private fun setupMainActivity(){
        setSupportActionBar(toolbar)

        val adapter = MyAdapter(supportFragmentManager)
        adapter.addFragment(OeuvreJourFragment(), "ODJ")
        adapter.addFragment(MapFragment(), "MAP")
        adapter.addFragment(ListFragment(), "LIST")
        adapter.addFragment(CollectionFragment(), "GALLERY")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
    }


}
