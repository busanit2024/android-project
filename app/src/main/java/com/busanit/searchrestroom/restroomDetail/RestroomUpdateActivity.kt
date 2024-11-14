package com.busanit.searchrestroom.restroomDetail

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.busanit.searchrestroom.BuildConfig
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.ActivityRestroomUpdateBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.launch

class RestroomUpdateActivity : AppCompatActivity(){
    private lateinit var binding: ActivityRestroomUpdateBinding
    private lateinit var db : AppDatabase
    private var restroomId: Int = 0

    private lateinit var map: GoogleMap
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
        binding = ActivityRestroomUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)

        restroomId = intent.getIntExtra("restroomId", 0)

        loadRestroomInfo()

        binding.btnUpdate.setOnClickListener {
            updateRestroom()
        }

        binding.btnClose.setOnClickListener {
            finish()
        }

        // 지도 부분
//        if (!Places.isInitialized()) {
//            Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
//        }
//
//        if (!checkPermissions()) {
//            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE)
//        }
//
//        db = AppDatabase.getDatabase(this)
//        binding.mapViewRegister.onCreate(savedInstanceState)
//        binding.mapViewRegister.getMapAsync(this)
    }

    private fun loadRestroomInfo(){
        lifecycleScope.launch {
            try {
                // 해당 ID로 화장실 정보 불러오기
                val restroom = db.restroomDao().getRestroomById(restroomId)
                restroom?.let{
                    binding.updateRestroomName.setText(restroom.restroomName)
                    binding.updateOpenTime.setText(restroom.openTime)
                    binding.updateMemo.setText(restroom.memo)
                    binding.checkFullTime.isChecked = restroom.fullTime ?: false
                    binding.checkUnisex.isChecked = restroom.unisex ?: false
                    binding.checkDiaper.isChecked = restroom.diaper ?: false
                    binding.checkAccessible.isChecked = restroom.accessible ?: false

                    //지도 부분!!
                    selectedLocation = LatLng(it.latitude ?: 0.0, it.longitude ?: 0.0)
//                  updateMapLocation(selectedLocation ?: DEFAULT_LOCATION)
                }
            } catch (e: Exception) {
                Log.e("EditRestroomActivity", "Error loading restroom info: ${e.message}")
                Toast.makeText(this@RestroomUpdateActivity, "정보를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 지도!
//    private fun setupLocationInput() {
//        binding.updateLocation.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                val address = binding.updateLocation.text.toString()
//                if (address.isNotEmpty()) {
//                    searchLocation(address)
//                }
//                true
//            } else {
//                false
//            }
//        }
//    }
//
//    private fun searchLocation(address: String) {
//        val geocoder = Geocoder(this)
//        try {
//            val addresses = geocoder.getFromLocationName(address, 1)
//            if (!addresses.isNullOrEmpty()) {
//                val location = LatLng(addresses[0].latitude, addresses[0].longitude)
//                selectedLocation = location
//                updateMapLocation(location)
//
//                val fullAddress = addresses[0].getAddressLine(0)
//                binding.updateLocation.setText(fullAddress)
//
//                if (binding.updateRestroomName.text.toString().isEmpty()) {
//                    val featureName = addresses[0].featureName
//                    binding.updateRestroomName.setText(featureName)
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("Geocoding", "Error: ${e.message}")
//            Toast.makeText(this, "주소를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun updateMapLocation(latLng: LatLng) {
//        try {
//            map.clear()
//            map.addMarker(MarkerOptions().position(latLng))
//            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_LEVEL))
//        } catch (e: Exception) {
//            Log.e("RestroomUpdateActivity", "Error updating map location: ${e.message}")
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    override fun onMapReady(googleMap: GoogleMap) {
//        try {
//            map = googleMap
//            map.setOnCameraMoveStartedListener { reason ->
//                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
//                    binding.scrollView.requestDisallowInterceptTouchEvent(true)
//                }
//            }
//
//            map.setOnCameraIdleListener {
//                binding.scrollView.requestDisallowInterceptTouchEvent(false)
//            }
//
//            if (checkPermissions()) {
//                map.isMyLocationEnabled = true
//                updateMapLocation(getMyLocation())
//            } else {
//                updateMapLocation(DEFAULT_LOCATION)
//            }
//
//            map.setOnMapClickListener { latLng ->
//                selectedLocation = latLng
//                updateMapLocation(latLng)
//                getAddressFromLocation(latLng)
//            }
//        } catch (e: Exception) {
//            Log.e("RestroomUpdateActivity", "Error in onMapReady: ${e.message}")
//            Toast.makeText(this, "지도 초기화 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//            finish()
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun getMyLocation(): LatLng {
//        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        if (!checkPermissions()) {
//            return DEFAULT_LOCATION
//        }
//
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            Toast.makeText(this, "GPS를 활성화해주세요", Toast.LENGTH_SHORT).show()
//            return DEFAULT_LOCATION
//        }
//
//        return try {
//            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//            if (lastLocation != null) {
//                LatLng(lastLocation.latitude, lastLocation.longitude)
//            } else {
//                DEFAULT_LOCATION
//            }
//        } catch (e: SecurityException) {
//            Log.e("Location", "Error: ${e.message}")
//            DEFAULT_LOCATION
//        }
//    }
//
//    private fun getAddressFromLocation(latLng: LatLng) {
//        val geocoder = Geocoder(this)
//        try {
//            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
//            if (!addresses.isNullOrEmpty()) {
//                val address = addresses[0].getAddressLine(0)
//                binding.updateLocation.setText(address)
//            }
//        } catch (e: Exception) {
//            Log.e("Geocoding", "Error: ${e.message}")
//        }
//    }
//
//    private fun checkPermissions(): Boolean {
//        return PERMISSIONS.all {
//            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_PERMISSION_CODE) {
//            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
//                if (::map.isInitialized) {
//                    map.isMyLocationEnabled = true
//                    updateMapLocation(getMyLocation())
//                }
//            } else {
//                Toast.makeText(this, "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show()
//                updateMapLocation(DEFAULT_LOCATION)
//            }
//        }
//    }

//
    private fun updateRestroom() {
        val restroom = Restroom(
            restroomId = restroomId,
            restroomName = binding.updateRestroomName.text.toString(),
            location = binding.updateLocation.text.toString(), // 주소는 수정할 필요가 없다면 넘어가도 됨
            latitude = null, // 현재는 사용하지 않지만, 위치가 변경될 경우 추가로 처리할 수 있음
            longitude = null,
            openTime = binding.updateOpenTime.text.toString(),
            fullTime = binding.checkFullTime.isChecked,
            unisex = binding.checkUnisex.isChecked,
            diaper = binding.checkDiaper.isChecked,
            accessible = binding.checkAccessible.isChecked,
            memo = binding.updateMemo.text.toString()
        )

        lifecycleScope.launch {
            try {
                db.restroomDao().update(restroom)
                Toast.makeText(this@RestroomUpdateActivity, "수정되었습니다", Toast.LENGTH_SHORT).show()
                finish() // 수정 후 종료
            } catch (e: Exception) {
                Log.e("EditRestroomActivity", "Error updating restroom: ${e.message}")
                Toast.makeText(this@RestroomUpdateActivity, "수정 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }




}