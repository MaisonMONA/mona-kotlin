package com.maison.mona.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil.setContentView
import com.maison.mona.R
import com.maison.mona.data.SaveSharedPreference
import org.osmdroid.views.MapView

/*
*This application is developed following strict practice and respect of architecture components
*to efficiently manage a UI's component lifecycle and handling data persistence
*
* Find everything you need to know here:
* https://developer.android.com/topic/libraries/architecture/index.html
*
* Initial setup was drive by Google's codelab found at
* https://codelabs.developers.google.com/codelabs/android-room-with-a-view-kotlin/#0
*
 */

class MainActivity : AppCompatActivity() {

    //For the map fragment, map view has to be implemented in it's respecting activity
    private var mMap: MapView? = null

    private companion object {
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_AND_FINE_LOCATION: Int = 1
        //private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL: Int = 2
        //private const val MY_PERMISSIONS_REQUEST_FINE_LOCATION: Int = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        //Check if user has current session via SharedPreferences
        if(SaveSharedPreference.firstTime(applicationContext)) {
            val intent = Intent(applicationContext, SplashActivity::class.java)
            startActivity(intent)
        }else {
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
                    val intent = Intent(this, SplashActivity::class.java).apply {
                    }
                    startActivity(intent)
                } else {
                    // Request permissions
                    ActivityCompat.requestPermissions(this, arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_AND_FINE_LOCATION
                    )
                }
            }else {
                // Both permissions are granted:
                // Setup Main Activity
                setContentView(
                    this,
                    R.layout.activity_main
                )
            }
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_AND_FINE_LOCATION ->{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Both permissions are granted:
                    // Setup Main Activity
                    setContentView(this,
                        R.layout.activity_main
                    )

                    //Collecting Artworks
                    //TODO: permission for internet
                    //TODO: Parse all data and create a DB for it?
                    //Note that the Toolbar defined in the layout has the id "my_toolbar"
                } else {
                    // Permissions denied
                    // Send to PermissionsDeniedActivity
                    val intent = Intent(this, SplashActivity::class.java).apply {
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
}