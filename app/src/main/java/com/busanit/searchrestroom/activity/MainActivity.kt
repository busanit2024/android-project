package com.busanit.searchrestroom.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.busanit.searchrestroom.BuildConfig
import com.busanit.searchrestroom.R
import com.busanit.searchrestroom.database.DatabaseCopier
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.cos

class MainActivity : AppCompatActivity(){
  private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

  // 지도 초기화
  private val PERMISSIONS = arrayOf(
    android.Manifest.permission.ACCESS_COARSE_LOCATION,
    android.Manifest.permission.ACCESS_FINE_LOCATION
  )

  val REQUEST_PERMISSION_CODE = 1

  val DEFAULT_ZOOM_LEVEL = 17f

  val CITY_HALL = LatLng(37.5662952, 126.97794509999994)

  var googleMap: GoogleMap? = null

  private lateinit var job: Job

  private val searchViewModel : SearchViewModel by viewModels()

  private lateinit var selectedPlace : LatLng

  private var filterDistance = 200.0
  private var filterUnisex = false
  private var filterAccessible = false
  private var filterDiaper = false

  // 주변 화장실 좌표 리스트를 저장할 변수 (이름, 좌표)
  var locations = mutableListOf<Restroom>()

  // 기존 마커를 저장하는 리스트를 선언
  private val markers = mutableListOf<com.google.android.gms.maps.model.Marker>()



  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    if (checkPermissions()) {
      initMap()
    } else {
      ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE)
    }

    binding.mapView.onCreate(savedInstanceState)

    selectedPlace = getMyLocation()
    val searchBar = findViewById<View>(R.id.search_bar)
    val listButton = searchBar.findViewById<LinearLayout>(R.id.listButton)




    // DB 가져오기
    job = CoroutineScope(Dispatchers.IO).launch {
      DatabaseCopier.copyAttachedDatabase(context = applicationContext)
    }

    runBlocking {
      job.join()
    }

    // 기본 검색 반경 설정
    binding.searchRadius200.isChecked = true

    // 상세 검색 조건 설정
    binding.checkDiaper.isChecked = filterDiaper
    binding.checkAccessible.isChecked = filterAccessible
    binding.checkUnisex.isChecked = filterUnisex

    val db = DatabaseCopier.getAppDataBase(context = applicationContext)
    var restroom = db!!.restroomDao().getRestroomById(1)
    Log.d("test", "restroom: $restroom")

    // 필터 반경이 변경될 때마다 업데이트
    binding.searchRadius200.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked) {
        filterDistance = 200.0
        updateLocations()
      }
    }

    binding.searchRadius500.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked) {
        filterDistance = 500.0
        updateLocations()
      }
    }

    binding.checkUnisex.setOnCheckedChangeListener { _, isChecked ->
      filterUnisex = isChecked
      updateLocations()
    }
    binding.checkAccessible.setOnCheckedChangeListener { _, isChecked ->
      filterAccessible = isChecked
      updateLocations()
    }
    binding.checkDiaper.setOnCheckedChangeListener { _, isChecked ->
      filterDiaper = isChecked
      updateLocations()
    }

    // 초기 위치 업데이트
    updateLocations()

    setupSearchBar()

    searchViewModel.selectedLocation.observe(this, Observer { location ->
      val (latLng, name) = location
      googleMap?.addMarker(MarkerOptions().position(latLng).title(name))
      googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_LEVEL))

    })

    binding.myLocationButton.setOnClickListener { onMyLocationButtonClick() }

    binding.addRestroomButton.setOnClickListener {
      // TODO: 화장실 추가 액티비티로 이동
    }

    //상세검색 메뉴 관련
    var bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
    bottomSheetBehavior.peekHeight = 200

    listButton.setOnClickListener {
      val intent = Intent(this, SearchListActivity::class.java)
      intent.putParcelableArrayListExtra("locations", locations as ArrayList<Restroom>)
      intent.putExtra("currentLat", selectedPlace.latitude)
      intent.putExtra("currentLong", selectedPlace.longitude)
      startActivity(intent)
    }

    binding.menuCollapseButton.setOnClickListener {
      if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.menuCollapseButton.setImageResource(R.drawable.icon_up)
      } else {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.menuCollapseButton.setImageResource(R.drawable.icon_down)
      }
    }

    binding.bottomNavigation.setOnItemSelectedListener {
      //it.itemid에 따라 액티비티 이동
      true
    }
  }


  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == REQUEST_PERMISSION_CODE) {
      if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
        initMap()
      } else {
        showPermissionDeniedDialog()
      }
    }
  }

  private fun showPermissionDeniedDialog() {
    // 권한 거부 시 사용자에게 안내하는 다이얼로그 표시
    AlertDialog.Builder(this)
      .setTitle("권한 필요")
      .setMessage("앱을 사용하려면 위치 권한이 필요합니다.")
      .setPositiveButton("권한 설정") { _, _ ->
        // 권한 설정 화면으로 이동
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
      }
      .setNegativeButton("취소") { _, _ ->
        // 앱 종료 또는 권한이 필요한 기능 비활성화
        // finish() // 앱 종료
        // 또는 권한이 필요한 기능을 비활성화하는 처리
      }
      .show()
  }


  private fun checkPermissions(): Boolean {

    for (permission in PERMISSIONS) {
      if (ActivityCompat.checkSelfPermission(
          this,
          permission
        ) != PackageManager.PERMISSION_GRANTED
      ) {
        return false
      }
    }
    return true
  }

  @SuppressLint("MissingPermission")
  fun initMap() {
    binding.mapView.getMapAsync {

      googleMap = it
      it.uiSettings.isMyLocationButtonEnabled = false

      when {
        checkPermissions() -> {
          it.isMyLocationEnabled = true
          it.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedPlace, DEFAULT_ZOOM_LEVEL))
        }

        else -> {
          it.moveCamera(CameraUpdateFactory.newLatLngZoom(CITY_HALL, DEFAULT_ZOOM_LEVEL))
        }
      }

      updateMapMarkers()

      googleMap!!.setOnMapClickListener { latLng ->
        selectedPlace = latLng
        googleMap?.clear()
        googleMap?.addMarker(MarkerOptions().position(latLng))
        updateLocations()
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_LEVEL))
      }

      googleMap!!.setOnMarkerClickListener { marker ->
        if (marker.tag != "selected") {
          val id = marker.tag as Int
          //TODO("마커 클릭했을 때 id에 해당하는 상세페이지로 이동")
        }
        true
      }

    }
  }

  private fun updateLocations() {
    val distance = filterDistance
    val latChange = distance / (111.32 * 1000)
    val longChange = distance / (111.32 * 1000 * cos(Math.toRadians(selectedPlace.latitude)))

    val minLat = selectedPlace.latitude - latChange
    val maxLat = selectedPlace.latitude + latChange
    val minLong = selectedPlace.longitude - longChange
    val maxLong = selectedPlace.longitude + longChange

    val db = DatabaseCopier.getAppDataBase(context = applicationContext)
    val locationsList = db!!.restroomDao().getRestroomsWithinArea(minLat, maxLat, minLong, maxLong) as MutableList<Restroom>

    // 필터링된 위치만 locations에 저장
    locations.clear()
    locations.addAll(locationsList.filter { restroom ->
      val distanceToRestroom = calculateDistance(selectedPlace, restroom)
      val matchesUisex = !filterUnisex || (restroom.unisex ?: false)
      val matchesAccessible = !filterAccessible || (restroom.accessible ?: false)
      val matchesDiaper = !filterDiaper || (restroom.diaper ?: false)
      distanceToRestroom <= filterDistance && matchesUisex && matchesAccessible && matchesDiaper
    })

    // 업데이트된 locations를 화면에 표시
    updateMapMarkers()
  }

  // 마커를 업데이트하는 함수
  private fun updateMapMarkers() {

    googleMap?.clear()
    // 기존 마커 제거
    markers.forEach { it.remove() }
    markers.clear()

    val iconBitmap = BitmapFactory.decodeResource(resources, R.drawable.icon_pin_bitmap)
    val iconBitmapScaled = Bitmap.createScaledBitmap(iconBitmap, 100, 100, false)
    val markerIcon = BitmapDescriptorFactory.fromBitmap(iconBitmapScaled)

    // 선택한 위치에 마커 추가
    selectedPlace?.let { latLng ->
      val selectedMarker = googleMap?.addMarker(
        MarkerOptions()
          .position(latLng)
          .title("선택한 위치")
      )
      selectedMarker?.tag = "selected"
    }

    // locations 리스트에 있는 위치로 새 마커 추가
    locations.forEach { location ->
      val marker = googleMap?.addMarker(
        com.google.android.gms.maps.model.MarkerOptions()
          .position(LatLng(location.latitude!!, location.longitude!!))
          .title(location.restroomName)
          .icon(markerIcon)
      )
      marker?.tag = location.restroomId
      marker?.let { markers.add(it) }  // null 체크 후 리스트에 추가
    }
  }

  @SuppressLint("MissingPermission")
  fun getMyLocation(): LatLng {

    val locationProvider: String = LocationManager.GPS_PROVIDER

    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

    val lastKnownLocation: Location? = locationManager.getLastKnownLocation(locationProvider)

    return if (lastKnownLocation != null) {
      LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
    } else {
      LatLng(35.157696, 129.059116)
    }
  }

  private fun onMyLocationButtonClick() {
    when {
      checkPermissions() -> {
        selectedPlace = getMyLocation()
        googleMap?.moveCamera(
          CameraUpdateFactory.newLatLngZoom(selectedPlace, DEFAULT_ZOOM_LEVEL)
        )
      }

      else -> Toast.makeText(applicationContext, "위치사용권한 설정에 동의해주세요", Toast.LENGTH_LONG).show()
    }
    updateLocations()
  }

  // 위치 간 거리 계산 함수
  private fun calculateDistance(location1: LatLng, restroom: Restroom): Double {
    val results = FloatArray(1)
    Location.distanceBetween(
      location1.latitude, location1.longitude,
      restroom.latitude!!, restroom.longitude!!,
      results
    )
    return results[0].toDouble()
  }

  private fun setupSearchBar() {
    if (!Places.isInitialized()) {
      Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
    }

    val autocompleteFragment = supportFragmentManager
      .findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

    autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
    autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
      override fun onPlaceSelected(place: Place) {
        searchViewModel.setSelectedLocation(place.latLng!!, place.name)
        selectedPlace = place.latLng!!
        updateLocations()
      }

      override fun onError(status: com.google.android.gms.common.api.Status) {
        Log.e("error", "An error occurred: $status")
      }
    })
  }


  override fun onResume() {
    super.onResume()
    binding.mapView.onResume()
  }

  override fun onPause() {
    super.onPause()
    binding.mapView.onPause()
  }

  override fun onDestroy() {
    job.cancel()
    super.onDestroy()
    binding.mapView.onDestroy()

  }

  override fun onLowMemory() {
    super.onLowMemory()
    binding.mapView.onLowMemory()
  }

}