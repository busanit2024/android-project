package com.busanit.searchrestroom.activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng

class SearchViewModel(application: Application) : AndroidViewModel(application) {
  private val _selectedLocation = MutableLiveData<Pair<LatLng, String?>>()
  val selectedLocation : LiveData<Pair<LatLng, String?>> = _selectedLocation

  fun setSelectedLocation(latLng: LatLng, name: String?) {
    _selectedLocation.value = Pair(latLng, name)

  }
}