package com.busanit.searchrestroom.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.busanit.searchrestroom.BuildConfig
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.ActivityAddRestroomBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.launch

class AddRestroomActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityAddRestroomBinding
    private lateinit var map: GoogleMap
    private lateinit var db: AppDatabase
    private var selectedLocation: LatLng? = null

    private val PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    private val REQUEST_PERMISSION_CODE = 1
    private val DEFAULT_ZOOM_LEVEL = 17f
    private val DEFAULT_LOCATION = LatLng(35.157696, 129.059116) // 부산 기본 위치

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRestroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 권한 체크
        if (!checkPermissions()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE)
        }

        db = AppDatabase.getDatabase(this)
        binding.mapViewRegister.onCreate(savedInstanceState)
        binding.mapViewRegister.getMapAsync(this)

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        }

        setupSearchBar()
        setupLocationInput()

        binding.btnRegister.setOnClickListener {
            registerRestroom()
        }

        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun setupSearchBar() {
        val autocompleteFragment = supportFragmentManager
            .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.ADDRESS
        ))

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                selectedLocation = place.latLng
                binding.editLocation.setText(place.address)
                binding.editRestroomName.setText(place.name)
                updateMapLocation(place.latLng!!)
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                Log.e("Places", "An error occurred: $status")
            }
        })
    }

    private fun setupLocationInput() {
        binding.editLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    searchLocation(s.toString())
                }
            }
        })
    }

    private fun searchLocation(address: String) {
        val geocoder = Geocoder(this)
        try {
            val addresses = geocoder.getFromLocationName(address, 1)
            if (!addresses.isNullOrEmpty()) {
                val location = LatLng(addresses[0].latitude, addresses[0].longitude)
                selectedLocation = location
                updateMapLocation(location)
            }
        } catch (e: Exception) {
            Log.e("Geocoding", "Error: ${e.message}")
        }
    }

    private fun updateMapLocation(latLng: LatLng) {
        map.clear()
        map.addMarker(MarkerOptions().position(latLng))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_LEVEL))
    }

    // onMapReady 함수만 수정
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (checkPermissions()) {
            map.isMyLocationEnabled = true
            updateMapLocation(getMyLocation())
        } else {
            updateMapLocation(DEFAULT_LOCATION)
        }

        map.setOnMapClickListener { latLng ->
            selectedLocation = latLng
            updateMapLocation(latLng)
            getAddressFromLocation(latLng)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation(): LatLng {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // 권한 체크
        if (!checkPermissions()) {
            return DEFAULT_LOCATION
        }

        // GPS 활성화 체크
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS를 활성화해주세요", Toast.LENGTH_SHORT).show()
            return DEFAULT_LOCATION
        }

        return try {
            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation != null) {
                LatLng(lastLocation.latitude, lastLocation.longitude)
            } else {
                DEFAULT_LOCATION
            }
        } catch (e: SecurityException) {
            Log.e("Location", "Error: ${e.message}")
            DEFAULT_LOCATION
        }
    }

    private fun getAddressFromLocation(latLng: LatLng) {
        val geocoder = Geocoder(this)
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0].getAddressLine(0)
                binding.editLocation.setText(address)
            }
        } catch (e: Exception) {
            Log.e("Geocoding", "Error: ${e.message}")
        }
    }

    private fun checkPermissions(): Boolean {
        return PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // 권한이 승인된 경우
                if (::map.isInitialized) {  // map이 초기화되었는지 확인
                    map.isMyLocationEnabled = true
                    updateMapLocation(getMyLocation())
                }
            } else {
                // 권한이 거부된 경우
                Toast.makeText(this, "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show()
                updateMapLocation(DEFAULT_LOCATION)
            }
        }
    }

    private fun registerRestroom() {
        if (selectedLocation == null) {
            Toast.makeText(this, "지도에서 위치를 선택해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        val restroom = Restroom(
            restroomId = 0,
            restroomName = binding.editRestroomName.text.toString(),
            location = binding.editLocation.text.toString(),
            latitude = selectedLocation?.latitude,
            longitude = selectedLocation?.longitude,
            openTime = binding.editOpenTime.text.toString(),
            fullTime = binding.checkFullTime.isChecked,
            unisex = binding.checkUnisex.isChecked,
            diaper = binding.checkDiaper.isChecked,
            accessible = binding.checkAccessible.isChecked,
            memo = binding.editMemo.text.toString()
        )

        lifecycleScope.launch {
            db.restroomDao().insert(restroom)
            Toast.makeText(this@AddRestroomActivity, "등록되었습니다", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapViewRegister.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapViewRegister.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapViewRegister.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapViewRegister.onLowMemory()
    }
}