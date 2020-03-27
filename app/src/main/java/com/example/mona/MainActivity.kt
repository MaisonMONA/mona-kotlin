package com.example.mona


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import com.example.mona.navigation.TabManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.views.MapView




/*
*This application is developped following strict practice and respect of architecture components
*to efficiently manage a UI's component lifecyle and handling data persistence
*
* Find everything you need to know here:
* https://developer.android.com/topic/libraries/architecture/index.html
*
* Initial setup was drive by Google's codelab found at
* https://codelabs.developers.google.com/codelabs/android-room-with-a-view-kotlin/#0
*
*
*
*
 */



class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private var mMap: MapView? = null
    private lateinit var lieuViewModel: LieuViewModel
    private lateinit var appBarConfiguration : AppBarConfiguration

    private companion object {
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_AND_FINE_LOCATION: Int = 1
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL: Int = 2
        private const val MY_PERMISSIONS_REQUEST_FINE_LOCATION: Int = 3
    }

    private var mSavedInstanceState: Bundle? = null


    private val tabManager: TabManager by lazy { TabManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //We set the state
        mSavedInstanceState = savedInstanceState


        //Check if user has current session via Shared Prefferences
        if (SaveSharedPreference.getToken(this).length == 0){
            val myIntent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(myIntent)
        } else {

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

                bottom_nav_view.setOnNavigationItemSelectedListener(this)

                //So Tab Icons have theyre own color
                bottom_nav_view.setItemIconTintList(null)


                if (mSavedInstanceState == null) {
                    tabManager.currentController = tabManager.navOdjController
                    setSupportActionBar(findViewById(R.id.toolbar))
                }

            }

        }
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


                    bottom_nav_view.setOnNavigationItemSelectedListener(this)

                    //So tab icons have theyre own color
                    bottom_nav_view.setItemIconTintList(null)

                    if (mSavedInstanceState == null) {
                        tabManager.currentController = tabManager.navOdjController
                        setSupportActionBar(findViewById(R.id.toolbar))
                    }

                    //Collecting Artworks
                    //TODO: permission for internet
                    //TODO: Parse all data and create a DB for it?
                    // Note that the Toolbar defined in the layout has the id "my_toolbar"
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        tabManager.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        tabManager.onRestoreInstanceState(savedInstanceState)
    }

    override fun supportNavigateUpTo(upIntent: Intent) {
        tabManager.supportNavigateUpTo(upIntent)
    }

    override fun onBackPressed() {
        // We are not on the last tab: do backpressed
        // Otherwise ignore backpressed
        if (!tabManager.isLastTab()){
            tabManager.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        tabManager.switchTab(menuItem.itemId)
        return true
    }


}
