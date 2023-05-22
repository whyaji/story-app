package com.whyaji.storyapp.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources.NotFoundException
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.whyaji.storyapp.R
import com.whyaji.storyapp.data.remote.response.ListStoryItem
import com.whyaji.storyapp.databinding.ActivityMapsBinding
import com.whyaji.storyapp.util.Resource
import com.whyaji.storyapp.util.ViewModelFactory


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val mapsViewModel: MapsViewModel by viewModels{
        ViewModelFactory(this)
    }
    private val markerHashMap = HashMap<Marker?, ListStoryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.run {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }

        mMap.setOnMarkerClickListener { marker ->
            val story = markerHashMap[marker]
            story?.let {
                val detailFragment = DetailFragment.newInstance(it.id, it.name!!, it.description!!, it.photoUrl!!)
                detailFragment.show(supportFragmentManager, "detailFragment")
            }
            false
        }

        getMyLocation()
        mapsViewModel.getStoriesWithLocation().observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    addManyMarker(resource.data.listStory)
                    moveCameraToIncludeMarkers()
                }
                is Resource.Error -> {
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }

                else -> {}
            }
        }
        setMapStyle(googleMap)
    }

    private val boundsBuilder = LatLngBounds.Builder()

    private fun addManyMarker(stories: List<ListStoryItem>) {
        stories.forEach { story ->
            val latLng = LatLng(story.lat as Double, story.lon as Double)
            val markerOptions = MarkerOptions().position(latLng).title(story.name)
            val marker = mMap.addMarker(markerOptions)
            boundsBuilder.include(latLng)
            markerHashMap[marker] = story
        }
    }

    private fun moveCameraToIncludeMarkers() {
        val bounds = boundsBuilder.build()
        val padding = resources.getDimensionPixelSize(R.dimen.map_padding)
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap.moveCamera(cameraUpdate)
    }

    private fun setMapStyle(googleMap: GoogleMap) {
        val styleResId = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> R.raw.map_style_night
            else -> R.raw.map_style
        }
        try {
            val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, styleResId))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getMyLocation()
        }
    }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
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