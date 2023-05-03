package com.zuhal.storyapp.view.maps

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.zuhal.storyapp.databinding.ActivityMapsBinding
import com.zuhal.storyapp.R
import com.zuhal.storyapp.view.ViewModelFactory
import com.zuhal.storyapp.data.Result
import com.zuhal.storyapp.view.login.LoginViewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        factory = ViewModelFactory.getInstance(this)

        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST) {
            //println(it.name)
        }

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

        // Add a marker in Sydney and move the camera

        val dicodingSpace = LatLng(-6.8957643, 107.6338462)
        mMap.addMarker(
            MarkerOptions()
                .position(dicodingSpace)
                .title("Dicoding Space")
                .snippet("Batik Kumeli No.50")
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dicodingSpace, 15f))

        mMap.setOnMapLongClickListener { latLng ->
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("New Marker")
                    .snippet("Lat: ${latLng.latitude} Long: ${latLng.longitude}")
                    .icon(
                        vectorToBitmap(
                            R.drawable.ic_android_black_24dp,
                            Color.parseColor("#3DDC84")
                        )
                    )
            )
        }

        mMap.setOnPoiClickListener { pointOfInterest ->
            val poiMarker = mMap.addMarker(
                MarkerOptions()
                    .position(pointOfInterest.latLng)
                    .title(pointOfInterest.name)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
            )
            poiMarker?.showInfoWindow()
        }

        val mapsViewModel: MapsViewModel by viewModels { factory }
        val loginViewModel: LoginViewModel by viewModels { factory }

        getMyLocation()
        setMapStyle()
        addManyMarker(mapsViewModel, loginViewModel)
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private val boundsBuilder = LatLngBounds.Builder()

    private fun addManyMarker(mapsViewModel: MapsViewModel, loginViewModel: LoginViewModel) {
        loginViewModel.getToken().observe(this) { token ->
            if (token != "") {
                mapsViewModel.getListStoriesLocation("${getString(R.string.bearer)} $token")
                    .observe(this) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {

                                }
                                is Result.Success -> {
                                    val data = result.data

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
                                }
                                else -> {}
                            }
                        }
                    }
            }
        }
    }

    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
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

    companion object {
        private const val TAG = "MapsActivity"
    }
}