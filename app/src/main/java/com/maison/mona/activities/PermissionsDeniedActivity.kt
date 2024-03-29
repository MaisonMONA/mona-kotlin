package com.maison.mona.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.maison.mona.R
import kotlinx.android.synthetic.main.activity_permissions_denied.*

class PermissionsDeniedActivity : AppCompatActivity() {

    private companion object {
        private const val MY_PERMISSIONS_REQUEST_FINE_LOCATION_AND_WRITE_EXTERNAL : Int = 0
//        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL : Int = 1
//        private const val MY_PERMISSIONS_REQUEST_FINE_LOCATION : Int = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions_denied)
        btn_try_again_request_permissions_id.setOnClickListener {
            /*
             *  We must check that all permissions are granted before using the app
             *  1. Write external storage
             * 2. Fine location
             */

            // One or both of the two required permissions are missing:
            // Ask for permissions

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                // Don't show explanation
                //      ???????????????? mais pourquoi ??

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE ) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                        ),
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION_AND_WRITE_EXTERNAL
                    )
                }
            } else {
                // Permissions are all granted, go to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_FINE_LOCATION_AND_WRITE_EXTERNAL -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // All permissions were granted, send to MainActivity
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}