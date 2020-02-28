package com.example.mona

import MainMenuAdapter
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.lifecycle.ViewModelProvider
import com.example.mona.fragment.CollectionFragment
import com.example.mona.fragment.ListFragment
import com.example.mona.fragment.MapFragment
import com.example.mona.fragment.OeuvreJourFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
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



class MainActivity : AppCompatActivity() {

    private var mMap: MapView? = null
    private lateinit var oeuvreViewModel: OeuvreViewModel

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

            oeuvreViewModel = ViewModelProvider(this).get(OeuvreViewModel::class.java)

            setupMainActivity()




        }
    }


    override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        mMap?.onResume() //needed for compass, my location overlays, v6.0.0 and up
/*
        oeuvreViewModel.oeuvreList.observe(this, Observer {
            println(it.size)
        })

 */



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

                    oeuvreViewModel = ViewModelProvider(this).get(OeuvreViewModel::class.java)

                    setupMainActivity()

                    //Collecting Artworks
                    //TODO: permission for internet
                    //TODO: Parse all data and create a DB for it?
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

        val adapter = MainMenuAdapter(supportFragmentManager)
        adapter.addFragment(OeuvreJourFragment(), "ODJ")
        adapter.addFragment(MapFragment(), "MAP")
        adapter.addFragment(ListFragment(), "LIST")
        adapter.addFragment(CollectionFragment(), "GALLERY")


        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

        //Setup of Tab Icons
        val selectedIcons = intArrayOf(
            R.drawable.odj_selected,
            R.drawable.map_selected,
            R.drawable.list_selected,
            R.drawable.collection_selected
        )
        val unselectedIcons = intArrayOf(
            R.drawable.odj_unselected,
            R.drawable.map_unselected,
            R.drawable.list_unselected,
            R.drawable.collection_unselected
        )

        //Initial Icons
        tabs.getTabAt(0)!!.icon = getDrawable(selectedIcons[0])
        for(i in 1..3){
            tabs.getTabAt(i)!!.icon = getDrawable(unselectedIcons[i])
        }

        //Listener
        tabs.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.icon = getDrawable(selectedIcons[tab.position])
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.icon = getDrawable(unselectedIcons[tab.position])
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

    }


}
