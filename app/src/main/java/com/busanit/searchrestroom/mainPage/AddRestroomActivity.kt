package com.busanit.searchrestroom.mainPage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import com.busanit.searchrestroom.databinding.ActivityAddRestroomBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddRestroomActivity : AppCompatActivity(), OnMapReadyCallback {
    private var _binding: ActivityAddRestroomBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private var db: AppDatabase? = null
    private var selectedLocation: LatLng? = null

    companion object {
        private const val REQUEST_PERMISSION_CODE = 1
        private const val DEFAULT_ZOOM_LEVEL = 17f
        private val DEFAULT_LOCATION = LatLng(35.157696, 129.059116) // 부산 기본 위치
        private val PERMISSIONS = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        PERMISSIONS.forEach { permission ->
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSION_CODE
            )
        } else {
            initializeMap()
        }
    }

    private fun initializeMap() {
        try {
            if (::map.isInitialized) {
                map.isMyLocationEnabled = true
                updateMapLocation(getMyLocation())
            }
        } catch (e: SecurityException) {
            Log.e("AddRestroomActivity", "Error initializing map", e)
            Toast.makeText(this, "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            _binding = ActivityAddRestroomBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Places API 초기화
            if (!Places.isInitialized()) {
                Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
            }

            // 권한 체크 및 요청
            checkAndRequestPermissions()

            // 데이터베이스 초기화
            db = AppDatabase.getDatabase(applicationContext)

            binding.mapViewRegister.onCreate(savedInstanceState)
            binding.mapViewRegister.getMapAsync(this)

            setupLocationInput()
            setupButtons()

        } catch (e: Exception) {
            Log.e("AddRestroomActivity", "Error in onCreate", e)
            Toast.makeText(this, "초기화 중 오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupButtons() {
        binding.btnRegister.setOnClickListener {
            if (checkPermissions()) {
                registerRestroom()
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show()
                checkAndRequestPermissions()
            }
        }

        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun setupLocationInput() {
        binding.editLocation.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val address = binding.editLocation.text.toString()
                if (address.isNotEmpty()) {
                    searchLocation(address)
                }
                true
            } else {
                false
            }
        }

    }

    private fun searchLocation(address: String) {
        val geocoder = Geocoder(this)
        try {
            geocoder.getFromLocationName(address, 1)?.let { addresses ->
                if (addresses.isNotEmpty()) {
                    val location = LatLng(addresses[0].latitude, addresses[0].longitude)
                    selectedLocation = location
                    updateMapLocation(location)

                    val fullAddress = addresses[0].getAddressLine(0)
                    binding.editLocation.setText(fullAddress)

                    if (binding.editRestroomName.text.toString().isEmpty()) {
                        val featureName = addresses[0].featureName
                        binding.editRestroomName.setText(featureName)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Geocoding", "Error: ${e.message}")
            Toast.makeText(this, "주소를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMapLocation(latLng: LatLng) {
        try {
            map.clear()
            map.addMarker(MarkerOptions().position(latLng))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_LEVEL))
        } catch (e: Exception) {
            Log.e("AddRestroomActivity", "Error updating map location: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        try {
            map = googleMap
            map.setOnCameraMoveStartedListener { reason ->
                if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    binding.scrollView.requestDisallowInterceptTouchEvent(true)
                }
            }

            map.setOnCameraIdleListener {
                binding.scrollView.requestDisallowInterceptTouchEvent(false)
            }

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
        } catch (e: Exception) {
            Log.e("AddRestroomActivity", "Error in onMapReady: ${e.message}")
            Toast.makeText(this, "지도 초기화 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation(): LatLng {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!checkPermissions()) {
            return DEFAULT_LOCATION
        }

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
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)?.let { addresses ->
                if (addresses.isNotEmpty()) {
                    val address = addresses[0].getAddressLine(0)
                    binding.editLocation.setText(address)
                }
            }
        } catch (e: Exception) {
            Log.e("Geocoding", "Error: ${e.message}")
            Toast.makeText(this, "주소를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissions(): Boolean {
        return PERMISSIONS.all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun registerRestroom() {
        // 필수 필드 검증
        if (binding.editRestroomName.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "화장실 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.editLocation.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "위치를 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedLocation == null) {
            Toast.makeText(this, "지도에서 위치를 선택해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        val restroom = Restroom(
            restroomId = 0, // Room이 자동으로 ID를 생성
            restroomName = binding.editRestroomName.text.toString().trim(),
            location = binding.editLocation.text.toString().trim(),
            latitude = selectedLocation?.latitude,
            longitude = selectedLocation?.longitude,
            openTime = binding.editOpenTime.text.toString().trim(),
            fullTime = binding.checkFullTime.isChecked,
            unisex = binding.checkUnisex.isChecked,
            diaper = binding.checkDiaper.isChecked,
            accessible = binding.checkAccessible.isChecked,
            memo = binding.editMemo.text.toString().trim()
        )

        // 데이터 저장 시도

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                // IO 스레드에서 데이터베이스 작업 수행
                withContext(Dispatchers.IO) {
                    db!!.restroomDao().insert(restroom)
                }

                // 성공 메시지 표시
                Toast.makeText(this@AddRestroomActivity, "화장실이 등록되었습니다", Toast.LENGTH_SHORT).show()

                // MainActivity로 이동
                val intent = Intent(this@AddRestroomActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // 이전 액티비티들을 모두 제거
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                Log.e("AddRestroomActivity", "Error registering restroom", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AddRestroomActivity,
                        "등록 중 오류가 발생했습니다: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
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
                if (::map.isInitialized) {
                    map.isMyLocationEnabled = true
                    updateMapLocation(getMyLocation())
                }
            } else {
                Toast.makeText(this, "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show()
                updateMapLocation(DEFAULT_LOCATION)
            }
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
        binding.mapViewRegister.onDestroy()  // 먼저 mapView destroy
        super.onDestroy()
        _binding = null  // 마지막에 binding null 처리
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapViewRegister.onLowMemory()
    }
}