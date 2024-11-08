package com.busanit.searchrestroom.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.busanit.searchrestroom.database.AppDatabase
import com.busanit.searchrestroom.database.Restroom
import com.busanit.searchrestroom.databinding.ActivityRegisterRestroomBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch


class RegisterRestroomActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityRegisterRestroomBinding
    private lateinit var map: GoogleMap
    private lateinit var db: AppDatabase
    private var selectedLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterRestroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        binding.mapViewRegister.onCreate(savedInstanceState)
        binding.mapViewRegister.getMapAsync(this)

        binding.btnRegister.setOnClickListener {
            registerRestroom()
        }

        binding.btnClose.setOnClickListener {
            finish()
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
            Toast.makeText(this@RegisterRestroomActivity, "등록되었습니다", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapClickListener { latLng ->
            selectedLocation = latLng
            map.clear()
            map.addMarker(MarkerOptions().position(latLng))
        }
    }

    // MapView 생명주기 메서드들
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