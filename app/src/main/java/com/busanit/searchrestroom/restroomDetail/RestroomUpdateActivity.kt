package com.busanit.searchrestroom.restroomDetail

import android.app.AlertDialog
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.busanit.searchrestroom.AuthHelper
import com.busanit.searchrestroom.BuildConfig
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.DeleteRequest
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.ActivityRestroomUpdateBinding
import com.busanit.searchrestroom.mainPage.MainActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RestroomUpdateActivity : AppCompatActivity(), OnMapReadyCallback {
  private lateinit var binding: ActivityRestroomUpdateBinding
  private var db: AppDatabase? = null
  private lateinit var map: GoogleMap
  private var selectedLocation: LatLng? = null

  companion object {
    private const val DEFAULT_ZOOM_LEVEL = 17f
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityRestroomUpdateBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // Places API 초기화
    if (!Places.isInitialized()) {
      Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
    }

    db = AppDatabase.getDatabase(applicationContext)
    val restroom: Restroom? = intent.getParcelableExtra("restroom")

    // MapView 초기화
    binding.mapViewUpdate.onCreate(savedInstanceState)
    binding.mapViewUpdate.getMapAsync(this)

    // 기존 데이터 표시
    setupExistingData(restroom)

    // 주소 검색 설정
    setupLocationInput()

    // 버튼 설정
    setupButtons(restroom)

    binding.backBtn.setOnClickListener {
      finish()
    }
  }
  private fun setupButtons(restroom: Restroom?) {
    // 수정하기 버튼
    binding.rewriteInfo.setOnClickListener {
      Log.d("RestroomUpdate", "수정 버튼 클릭됨")
      if (validateInput()) {
        Log.d("RestroomUpdate", "입력값 검증 통과")
        updateRestroom(restroom)
      }
    }

    // 삭제 요청 버튼
    binding.deleteRequestButton.setOnClickListener {
      val requestMessage = EditText(this)
      requestMessage.hint = "요청사유를 입력해주세요."

      AlertDialog.Builder(this).run {
        setTitle("삭제 요청")
        setMessage("${restroom?.restroomName ?: "(알수없음)"} 의 삭제를 요청합니다.")
        setView(requestMessage)
        setPositiveButton("요청하기") { _, _ ->
          val message = requestMessage.text.toString()
          handleDeleteRequest(restroom, message)
        }
        setNegativeButton("취소", null)
        show()
      }
    }
  }
  private fun handleDeleteRequest(restroom: Restroom?, message: String) {
    val regTime = Date()
    val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedTime = outputFormat.format(regTime)

    if (restroom != null) {
      val request = DeleteRequest(
        requestId = 0,
        memberId = AuthHelper.getMemberId(),
        restroomId = restroom.restroomId,
        requestMessage = message,
        regTime = formattedTime.toString()
      )

      lifecycleScope.launch(Dispatchers.IO) {
        try {
          db?.DeleteRequestDao()?.insert(request)
          withContext(Dispatchers.Main) {
            Toast.makeText(this@RestroomUpdateActivity, "삭제 요청이 등록되었습니다.", Toast.LENGTH_SHORT).show()
            finish()
          }
        } catch (e: Exception) {
          withContext(Dispatchers.Main) {
            Toast.makeText(this@RestroomUpdateActivity, "삭제 요청 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
          }
        }
      }
    } else {
      Toast.makeText(this, "화장실 정보가 없습니다.", Toast.LENGTH_SHORT).show()
    }
  }
  private fun validateInput(): Boolean {
    if (binding.editRestroomName.text.toString().trim().isEmpty()) {
      Toast.makeText(this, "화장실 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
      return false
    }
    if (binding.editLocation.text.toString().trim().isEmpty()) {
      Toast.makeText(this, "위치를 입력해주세요", Toast.LENGTH_SHORT).show()
      return false
    }
    if (selectedLocation == null) {
      Toast.makeText(this, "지도에서 위치를 선택해주세요", Toast.LENGTH_SHORT).show()
      return false
    }
    return true
  }

  private fun updateRestroom(oldRestroom: Restroom?) {
    if (oldRestroom == null) {
      Toast.makeText(this, "수정할 화장실 정보가 없습니다.", Toast.LENGTH_SHORT).show()
      return
    }

    // selectedLocation이 null이 아닌지 확인
    if (selectedLocation == null) {
      Toast.makeText(this, "위치를 선택해주세요", Toast.LENGTH_SHORT).show()
      return
    }

    val updatedRestroom = Restroom(
      restroomId = oldRestroom.restroomId,
      restroomName = binding.editRestroomName.text.toString().trim(),
      location = binding.editLocation.text.toString().trim(),
      latitude = selectedLocation?.latitude,    // 선택된 위치의 위도
      longitude = selectedLocation?.longitude,  // 선택된 위치의 경도
      openTime = binding.editOpenTime.text.toString().trim(),
      fullTime = binding.chipFullTime.isChecked,
      unisex = binding.chipUnisex.isChecked,
      diaper = binding.chipDiaper.isChecked,
      accessible = binding.chipAccessible.isChecked,
      memo = binding.editMemo.text.toString().trim()
    )

    lifecycleScope.launch(Dispatchers.IO) {
      try {
        db?.restroomDao()?.update(updatedRestroom)
        withContext(Dispatchers.Main) {
          Toast.makeText(this@RestroomUpdateActivity, "수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
          finish()
        }
      } catch (e: Exception) {
        Log.e("RestroomUpdate", "DB 업데이트 실패", e)
        withContext(Dispatchers.Main) {
          Toast.makeText(this@RestroomUpdateActivity, "수정 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  private fun setupExistingData(restroom: Restroom?) {
    restroom?.let {
      binding.editRestroomName.setText(it.restroomName)
      binding.editLocation.setText(it.location)
      binding.editOpenTime.setText(it.openTime)
      binding.chipFullTime.isChecked = it.fullTime ?: false
      binding.chipUnisex.isChecked = it.unisex ?: false
      binding.chipDiaper.isChecked = it.diaper ?: false
      binding.chipAccessible.isChecked = it.accessible ?: false
      binding.editMemo.setText(it.memo)

      // 초기 위치 설정
      selectedLocation = LatLng(it.latitude ?: 0.0, it.longitude ?: 0.0)
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
        }
      }
    } catch (e: Exception) {
      Toast.makeText(this, "주소를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
    }
  }

  private fun updateMapLocation(latLng: LatLng) {
    map.clear()
    map.addMarker(MarkerOptions().position(latLng))
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_LEVEL))
  }

  override fun onMapReady(googleMap: GoogleMap) {
    map = googleMap

    // 스크롤 처리
    map.setOnCameraMoveStartedListener { reason ->
      if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
        binding.scrollView.requestDisallowInterceptTouchEvent(true)
      }
    }

    map.setOnCameraIdleListener {
      binding.scrollView.requestDisallowInterceptTouchEvent(false)
    }

    // 초기 위치 표시
    selectedLocation?.let { updateMapLocation(it) }

    // 지도 클릭 이벤트
    map.setOnMapClickListener { latLng ->
      selectedLocation = latLng
      updateMapLocation(latLng)
      getAddressFromLocation(latLng)
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
      Toast.makeText(this, "주소를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
    }
  }

  // MapView 생명주기 메서드들
  override fun onResume() {
    super.onResume()
    binding.mapViewUpdate.onResume()
  }

  override fun onPause() {
    super.onPause()
    binding.mapViewUpdate.onPause()
  }

  override fun onDestroy() {
    binding.mapViewUpdate.onDestroy()
    super.onDestroy()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    binding.mapViewUpdate.onLowMemory()
  }
}