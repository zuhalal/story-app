package com.zuhal.storyapp.view.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.zuhal.storyapp.databinding.ActivityMapsBinding
import com.zuhal.storyapp.R
import com.zuhal.storyapp.view.ViewModelFactory
import com.zuhal.storyapp.data.Result
import com.zuhal.storyapp.view.add.AddStoryActivity
import com.zuhal.storyapp.view.login.LoginActivity
import com.zuhal.storyapp.view.login.LoginViewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var factory: ViewModelFactory
    val mapsViewModel: MapsViewModel by viewModels { factory }
    val loginViewModel: LoginViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        factory = ViewModelFactory.getInstance(this)

        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST) {}

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        mMap.setOnPoiClickListener { pointOfInterest ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )
            poiMarker?.showInfoWindow()
        }

        getMyLocation()
        setMapStyle()
        addManyMarker()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, getString(R.string.style_parsing_failed))
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, getString(R.string.cant_find_style), exception)
        }
    }

    private val boundsBuilder = LatLngBounds.Builder()

    private fun addManyMarker() {
        loginViewModel.getToken().observe(this) { token ->
            if (token != "") {
                mapsViewModel.getListStoriesLocation("${getString(R.string.bearer)} $token", 1)
                    .observe(this) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    showLoading(true)
                                }
                                is Result.Success -> {
                                    val data = result.data

                                    if (data.isEmpty()) {
                                        Toast.makeText(
                                            this,
                                            getString(R.string.no_location_data),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    data.forEach { story ->
                                        if (story.lat != null && story.lon != null) {
                                            val latLng = LatLng(story.lat, story.lon)
                                            mMap.addMarker(
                                                MarkerOptions().position(latLng).title(story.name)
                                            )
                                            boundsBuilder.include(latLng)
                                        }
                                    }

                                    val bounds: LatLngBounds = boundsBuilder.build()
                                    mMap.animateCamera(
                                        CameraUpdateFactory.newLatLngBounds(
                                            bounds,
                                            resources.displayMetrics.widthPixels,
                                            resources.displayMetrics.heightPixels,
                                            300
                                        )
                                    )

                                    showLoading(false)
                                }
                                else -> {
                                    showLoading(false)
                                }
                            }
                        }
                    }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                loginViewModel.logout()
                return true
            }
            R.id.menu_add -> {
                val intent = Intent(this@MapsActivity, AddStoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                return true
            }
            R.id.menu_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                return true
            }
            R.id.menu_maps -> {
                val intent = Intent(this@MapsActivity, MapsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                return true
            }
            else -> return true
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}