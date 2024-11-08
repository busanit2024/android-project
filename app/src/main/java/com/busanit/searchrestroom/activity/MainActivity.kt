package com.busanit.searchrestroom.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.busanit.searchrestroom.activity.RegisterRestroomActivity
import com.busanit.searchrestroom.database.DatabaseCopier
import com.busanit.searchrestroom.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
  private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

  val PERMISSIONS = arrayOf(
    android.Manifest.permission.ACCESS_COARSE_LOCATION,
    android.Manifest.permission.ACCESS_FINE_LOCATION
  )

  val REQUEST_PERMISSION_CODE = 1

  val DEFAULT_ZOOM_LEVEL = 17f

  val CITY_HALL = LatLng(37.5662952, 126.97794509999994)

  var googleMap: GoogleMap? = null

  private lateinit var job : Job

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(binding.root)

    binding.mapView.onCreate(savedInstanceState)

    job = CoroutineScope(Dispatchers.IO).launch {
      DatabaseCopier.copyAttachedDatabase(context = applicationContext)
    }

    runBlocking {
      job.join()
    }

    val db = DatabaseCopier.getAppDataBase(context = applicationContext)
    // Restroom 객체 가져오기
    var restroom = db?.restroomDao()?.getRestroomById(1)  // null safety 처리
    Log.d("test", "restroom: $restroom")

    if (checkPermissions()) {
      initMap()
    } else {
      ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE)
    }

    binding.fabMyLocation.setOnClickListener { onMyLocationButtonClick() }

    // 위치 등록 버튼 클릭 이벤트
    binding.fabRegisterLocation.setOnClickListener {
      val intent = Intent(this, RegisterRestroomActivity::class.java)
      startActivity(intent)
    }

  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    when (requestCode) {
      REQUEST_PERMISSION_CODE -> {
        if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
          initMap()
        } else {
          Toast.makeText(this, "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  private fun checkPermissions(): Boolean {

    for (permission in PERMISSIONS) {
      if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
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
          it.moveCamera(CameraUpdateFactory.newLatLngZoom(getMyLocation(), DEFAULT_ZOOM_LEVEL))
        }
        else -> {
          it.moveCamera(CameraUpdateFactory.newLatLngZoom(CITY_HALL, DEFAULT_ZOOM_LEVEL))
        }
      }
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
      checkPermissions() -> googleMap?.moveCamera(
        CameraUpdateFactory.newLatLngZoom(getMyLocation(), DEFAULT_ZOOM_LEVEL)
      )
      else -> Toast.makeText(applicationContext, "위치사용권한 설정에 동의해주세요", Toast.LENGTH_LONG).show()
    }
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