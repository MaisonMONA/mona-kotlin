package com.example.mona

import MyAdapter
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TableLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import org.osmdroid.views.MapView


class MainActivity : AppCompatActivity() {

    private var mMap: MapView? = null

    private companion object {
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_AND_FINE_LOCATION: Int = 1
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL: Int = 2
        private const val MY_PERMISSIONS_REQUEST_FINE_LOCATION: Int = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*

        We must check that all permissions are granted before using the app
        1. Write external storage
        2. Fine location

         */


        // One or both of the two required permissions are missing:
        // Ask for permissions

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Even if we want to show request rationale, we send the user to PermissionsDeniedActivity
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Send to PermissionsDeniedActivity
                val intent = Intent(this, PermissionsDeniedActivity::class.java).apply {
                    // Optionally add message
                    // putExtra(EXTRA_MESSAGE, message)
                }
                startActivity(intent)
            } else {
                // Request permissions
                ActivityCompat.requestPermissions(this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MainActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_AND_FINE_LOCATION)
            }

        }else{
            // Both permissions are granted:
            //  Setup Main Activity
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
            MainActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_AND_FINE_LOCATION ->{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // Both permissions are granted:
                    //  Setup Main Activity
                    setContentView(R.layout.activity_main)
                    setupMainActivity()
                    // Note that the Toolbar defined in the layout has the id "my_toolbar"
                    setSupportActionBar(findViewById(R.id.toolbar))
                } else {
                    // Permissions denied
                    // Send to PermissionsDeniedActivity
                    val intent = Intent(this, PermissionsDeniedActivity::class.java).apply {
                        // Optionally add message
                        // putExtra(EXTRA_MESSAGE, message)
                    }
                    startActivity(intent)

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


    // Called when all permissions are granted
    private fun setupMainActivity() {
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
